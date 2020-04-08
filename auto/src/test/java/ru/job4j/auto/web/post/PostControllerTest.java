package ru.job4j.auto.web.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.job4j.auto.config.helper.BaseEntityTestHelper;
import ru.job4j.auto.config.helper.ToTestHelpers.PostToTestHelper;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.service.PostService;
import ru.job4j.auto.web.AbstractControllerTest;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.job4j.auto.TestModelData.*;

class PostControllerTest extends AbstractControllerTest {
    private final BaseEntityTestHelper<Post> testHelper;

    private final PostToTestHelper toTestHelper;

    private final PostService service;

    @Autowired
    PostControllerTest(BaseEntityTestHelper<Post> testHelper, PostToTestHelper toTestHelper, PostService service) {
        super(PostController.URL);
        this.testHelper = testHelper;
        this.toTestHelper = toTestHelper;
        this.service = service;
    }

    @Test
    void findFiltered() throws Exception {
        perform(doGet("filter").auth(USER).unwrap()
                .param("user", String.valueOf(1))
                .param("year.min", String.valueOf(2015))
                .param("year.max", String.valueOf(2018))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(toTestHelper.forAuth(USER).contentJson(POST_MAZDA6, POST_BMW));
    }
}
