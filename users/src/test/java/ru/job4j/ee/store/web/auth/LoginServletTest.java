package ru.job4j.ee.store.web.auth;

import org.junit.jupiter.api.Test;
import ru.job4j.ee.store.web.AbstractServletTest;
import ru.job4j.ee.store.web.mock.WebMock;
import ru.job4j.ee.store.web.json.JsonUtil;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static ru.job4j.ee.store.AssertionUtil.*;
import static ru.job4j.ee.store.web.mock.WebMock.GET_METHOD;
import static ru.job4j.ee.store.web.mock.WebMock.POST_METHOD;

class LoginServletTest extends AbstractServletTest {
    private static final String LOGIN_URL = "/login";

    @Test
    void doGetLoginUnauthorized() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(LOGIN_URL)
                .withNoAuth()
                .withMethod(GET_METHOD)
                .withForwardExpected()
                .act()
                .checkForwardPath("WEB-INF/jsp/login.jsp")
                .terminate();
    }

    @Test
    void doGetLoginAuthorized() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(LOGIN_URL)
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withRedirectExpected()
                .act()
                .checkRedirectPath("/profile")
                .terminate();
    }

    @Test
    void doGetLoginAuthorizedAsAdmin() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(LOGIN_URL)
                .withAuth(ADMIN)
                .withMethod(GET_METHOD)
                .withRedirectExpected()
                .act()
                .checkRedirectPath("/users")
                .terminate();
    }

    @Test
    void dePostLogin() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(LOGIN_URL)
                .withNoAuth()
                .withMethod(POST_METHOD)
                .withRequestJson(asJson(USER.getLogin(), USER.getPassword()))
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(JsonUtil.asJson("redirection", "profile"))
                .terminate();
    }

    @Test
    void doPostUnregistered() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(LOGIN_URL)
                .withMethod(POST_METHOD)
                .withRequestJson(asJson(UNREGISTERED.getLogin(), UNREGISTERED.getPassword()))
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_FORBIDDEN)
                .checkResponseBody(JsonUtil.asJson("error", "User with the given login is not registered"))
                .terminate();
    }

    private String asJson(String login, String password) {
        return JsonUtil.asJson(Map.of("login", login, "password", password));
    }

}
