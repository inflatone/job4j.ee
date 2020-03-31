package ru.job4j.auto.web.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import ru.job4j.auto.config.helper.BaseEntityTestHelper;
import ru.job4j.auto.config.helper.ToTestHelpers.PostToTestHelper;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.service.PostService;
import ru.job4j.auto.web.AbstractSecondLevelControllerTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.job4j.auto.TestModelData.*;
import static ru.job4j.auto.config.helper.BaseEntityTestHelper.setIdIfRequired;

class AdminPostControllerTest extends AbstractSecondLevelControllerTest {
    private final BaseEntityTestHelper<Post> testHelper;

    private final PostToTestHelper toTestHelper;

    private final PostService service;

    @Autowired
    AdminPostControllerTest(BaseEntityTestHelper<Post> testHelper, PostToTestHelper toTestHelper, PostService service) {
        super(AdminPostController.URL);
        this.testHelper = testHelper;
        this.toTestHelper = toTestHelper;
        this.service = service;
    }

    @Test
    void find() throws Exception {
        perform(doGet(USER.getId(), POST_BMW.getId()).auth(DEALER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(toTestHelper.forAuth(DEALER).contentJson(POST_BMW));
    }

    @Test
    void findAll() throws Exception {
        perform(doGet(USER.getId()).auth(DEALER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(toTestHelper.forAuth(DEALER).contentJson(POST_MAZDA6, POST_BMW));
    }

    @Test
    void create() throws Exception {
        Post newPost = testHelper.newEntity();
        ResultActions actions = perform(doPost(DEALER.getId()).postAsFormData(newPost).auth(DEALER))
                .andDo(print())
                .andExpect(status().isCreated());

        setIdIfRequired(newPost, actions.andReturn());
        var persisted = service.find(newPost.getId(), DEALER.getId());
        newPost.getCar().setId(persisted.getCar().getId());
        newPost.setUser(DEALER);

        actions.andExpect(toTestHelper.forAuth(DEALER).contentJson(newPost));
        testHelper.assertMatch(persisted, newPost);
        testHelper.assertMatch(service.findAll(DEALER.getId()), POST_MAZDA3, newPost);
    }

    @Test
    void update() throws Exception {
        Post postToUpdate = testHelper.copy(POST_MAZDA3);
        perform(doPost(DEALER.getId(), POST_MAZDA3.getId()).postAsFormData(postToUpdate).auth(DEALER))
                .andDo(print())
                .andExpect(status().isNoContent());
        testHelper.assertMatch(service.findAll(DEALER.getId()), POST_MAZDA3);
    }

    @Test
    void complete() throws Exception {
        perform(doPut("{id}/completed", DEALER.getId(), POST_MAZDA3.getId()).auth(DEALER).jsonBody(true))
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
