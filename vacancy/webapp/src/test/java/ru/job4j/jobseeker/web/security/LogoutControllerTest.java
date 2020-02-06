package ru.job4j.jobseeker.web.security;

import org.junit.jupiter.api.Test;
import ru.job4j.jobseeker.web.AbstractControllerTest;

import static ru.job4j.jobseeker.TestHelper.USER;
import static ru.job4j.jobseeker.web.mock.WebMock.GET_METHOD;

class LogoutControllerTest extends AbstractControllerTest {
    private static final String LOGOUT_URL = "/logout";

    @Test
    void logout() throws Exception {
        webMock.withUrl(LOGOUT_URL)
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withRedirectExpected()
                .act()
                .checkRedirectPath("/")
                .checkUnauthorized();
    }
}