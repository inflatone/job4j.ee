package ru.job4j.ee.store.web.auth;

import org.junit.jupiter.api.Test;
import ru.job4j.ee.store.web.AbstractServletTest;
import ru.job4j.ee.store.web.mock.WebMock;

import static ru.job4j.ee.store.AssertionUtil.USER;
import static ru.job4j.ee.store.web.mock.WebMock.GET_METHOD;

class LogoutServletTest extends AbstractServletTest {
    private static final String LOGOUT_URL = "/logout";

    @Test
    void doGet() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(LOGOUT_URL)
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withRedirectExpected()
                .act()
                .checkRedirectPath("/")
                .checkUnauthorized()
                .terminate();
    }
}
