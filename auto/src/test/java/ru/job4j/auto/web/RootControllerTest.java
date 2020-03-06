package ru.job4j.auto.web;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.job4j.auto.TestModelData.DEALER;
import static ru.job4j.auto.TestModelData.USER;

class RootControllerTest extends AbstractControllerTest {
    RootControllerTest() {
        super("");
    }

    @Test
    void root() throws Exception {
        perform(doGet().auth(USER))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("profile"));
    }

    @Test
    void profile() throws Exception {
        perform(doGet("profile").auth(USER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/profile.jsp"));
    }

    @Test
    void profileNotOwn() throws Exception {
        perform(doGet("profile/{id}", DEALER.getId()).auth(USER))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void profileNotOwnAsAdmin() throws Exception {
        perform(doGet("profile/{id}", USER.getId()).auth(DEALER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/profile.jsp"));
    }

    @Test
    void users() throws Exception {
        perform(doGet("users").auth(USER))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void usersAsAdmin() throws Exception {
        perform(doGet("users").auth(DEALER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/users.jsp"));
    }

    @Test
    void loginUnauth() throws Exception {
        perform(doGet("login"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/login.jsp"));
    }

    @Test
    void loginAuth() throws Exception {
        perform(doGet("login").auth(DEALER))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void profileUnauth() throws Exception {
        perform(doGet("profile"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
}
