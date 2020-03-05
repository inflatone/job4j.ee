package ru.job4j.auto.web;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RootControllerTest extends AbstractControllerTest {
    RootControllerTest() {
        super("");
    }

    @Test
    void root() throws Exception {
        perform(doGet())
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("profile"));
    }

    @Test
    void profile() throws Exception {
        perform(doGet("profile"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/profile.jsp"));
    }
}
