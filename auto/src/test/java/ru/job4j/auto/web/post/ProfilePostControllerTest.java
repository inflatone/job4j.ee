package ru.job4j.auto.web.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import ru.job4j.auto.BaseEntityTestHelper;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.service.PostService;
import ru.job4j.auto.web.AbstractControllerTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.job4j.auto.TestModelData.*;

class ProfilePostControllerTest extends AbstractControllerTest {
    private final BaseEntityTestHelper<Post> testHelper;

    private final PostService service;

    @Autowired
    public ProfilePostControllerTest(BaseEntityTestHelper<Post> testHelper, PostService service) {
        super(ProfilePostController.URL);
        this.testHelper = testHelper;
        this.service = service;
    }

    @Test
    void find() throws Exception {
        perform(doGet(POST_BMW.getId()).auth(USER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.contentJson(POST_BMW));
    }

    @Test
    void findAll() throws Exception {
        perform(doGet().auth(USER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.contentJson(POST_MAZDA6, POST_BMW));
    }

    @Test
    void create() throws Exception {
        Post newPost = testHelper.newEntity();
        MvcResult result = perform(doPost().postAsFormData(newPost).auth(USER))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        int newId = getCreatedResourceId(result);
        newPost.setId(newId);

        var persisted = service.find(newId, USER.getId());
        newPost.getCar().setId(persisted.getCar().getId());
        testHelper.assertMatch(persisted, newPost);
        testHelper.assertMatch(service.findAll(USER.getId()), POST_MAZDA6, POST_BMW, newPost);
    }

    @Test
    void update() throws Exception {
        Post postToUpdate = testHelper.copy(POST_BMW);
        perform(doPost().postAsFormData(postToUpdate).auth(USER))
                .andDo(print())
                .andExpect(status().isNoContent());
        testHelper.assertMatch(service.findAll(USER.getId()), POST_MAZDA6, postToUpdate);
    }

    @Test
    void complete() throws Exception {
        perform(doPost(POST_MAZDA3.getId()).auth(DEALER).unwrap().param("completed", "true"))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertTrue(service.find(POST_MAZDA3.getId(), DEALER.getId()).isCompleted());
    }

    @Test
    void delete() throws Exception {
        perform(doDelete(POST_MAZDA6.getId()).auth(USER))
                .andDo(print())
                .andExpect(status().isNoContent());
        testHelper.assertMatch(service.findAll(USER.getId()), POST_BMW);
    }
}
