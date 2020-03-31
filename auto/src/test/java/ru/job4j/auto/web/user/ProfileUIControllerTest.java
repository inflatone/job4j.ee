package ru.job4j.auto.web.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.job4j.auto.web.AbstractControllerTest;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.job4j.auto.TestModelData.USER;

class ProfileUIControllerTest extends AbstractControllerTest {
    @Autowired
    ProfileUIControllerTest() {
        super(ProfileUIController.URL);
    }

    @Test
    void profile() throws Exception {
        perform(doGet().auth(USER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/profile.jsp"));
    }

    @Test
    void profileNotOwn() throws Exception {
        perform(doGet(USER.getId()).auth(USER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/profile.jsp"));
    }
}
