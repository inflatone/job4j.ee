package ru.job4j.ee.store.web.filter;

import org.junit.jupiter.api.Test;
import ru.job4j.ee.store.web.AbstractServletTest;
import ru.job4j.ee.store.web.mock.WebMock;

import javax.servlet.http.HttpServletResponse;

import static ru.job4j.ee.store.AssertionUtil.USER;
import static ru.job4j.ee.store.web.mock.WebMock.GET_METHOD;

class SecurityFiltersTest extends AbstractServletTest {
    @Test
    void doGetProfileUnauthorized() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withNoAuth()
                .withMethod(GET_METHOD)
                .withUrl("/profile")
                .withAction("enable")
                .withRedirectExpected()
                .act()
                .checkRedirectPath("/login")
                .terminate();
    }

    @Test
    void doGetProfileWithoutAction() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withNoAuth()
                .withMethod(GET_METHOD)
                .withUrl("/profile")
                .withRedirectExpected()
                .act()
                .checkRedirectPath("/login")
                .terminate();
    }

    @Test
    void doGetResource() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withNoAuth()
                .withMethod(GET_METHOD)
                .withUrl("/resources/style/style.css")
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doGetAdminUrl() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl("/users")
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withForwardWithErrorExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_FORBIDDEN)
                .checkForwardErrorMessage("You're not authorized to pass here")
                .terminate();
    }
}
