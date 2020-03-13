package ru.job4j.auto.web.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import ru.job4j.auto.BaseEntityTestHelper;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.service.PostService;
import ru.job4j.auto.web.AbstractSecondLevelControllerTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.job4j.auto.TestModelData.*;

class AdminPostControllerTest extends AbstractSecondLevelControllerTest {
    private final BaseEntityTestHelper<Post> testHelper;

    private final PostService service;

    @Autowired
    AdminPostControllerTest(BaseEntityTestHelper<Post> testHelper, PostService service) {
        super(AdminPostController.URL);
        this.testHelper = testHelper;
        this.service = service;
    }

    @Test
    void find() throws Exception {
        perform(doGet(USER.getId(), POST_BMW.getId()).auth(DEALER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.contentJson(POST_BMW));
    }

    @Test
    void findAll() throws Exception {
        perform(doGet(USER.getId()).auth(DEALER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.contentJson(POST_MAZDA6, POST_BMW));
    }

    @Test
    void create() throws Exception {
        Post newPost = testHelper.newEntity();
        MvcResult result = perform(doPost(DEALER.getId()).postAsFormData(newPost).auth(DEALER))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        int newId = getCreatedResourceId(result);
        newPost.setId(newId);

        var persisted = service.find(newId, DEALER.getId());
        newPost.getCar().setId(persisted.getCar().getId());
        testHelper.assertMatch(persisted, newPost);
        testHelper.assertMatch(service.findAll(DEALER.getId()), POST_MAZDA3, newPost);
    }

    @Test
    void update() throws Exception {
        Post postToUpdate = testHelper.copy(POST_MAZDA3);
        perform(doPost(DEALER.getId()).postAsFormData(postToUpdate).auth(DEALER))
                .andDo(print())
                .andExpect(status().isNoContent());
        testHelper.assertMatch(service.findAll(DEALER.getId()), POST_MAZDA3);
    }

    @Test
    void complete() throws Exception {
        perform(doPost(DEALER.getId(), POST_MAZDA3.getId()).auth(DEALER).unwrap().param("completed", "true"))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertTrue(service.find(POST_MAZDA3.getId(), DEALER.getId()).isCompleted());
    }

    @Test
    void delete() throws Exception {
        perform(doDelete(USER.getId(), POST_MAZDA6.getId()).auth(DEALER))
                .andDo(print())
                .andExpect(status().isNoContent());
        testHelper.assertMatch(service.findAll(USER.getId()), POST_BMW);
    }
}
