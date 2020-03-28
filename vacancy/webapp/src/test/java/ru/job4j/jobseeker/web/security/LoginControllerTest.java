package ru.job4j.jobseeker.web.security;

import org.junit.jupiter.api.Test;
import ru.job4j.jobseeker.web.AbstractControllerTest;

import javax.servlet.http.HttpServletResponse;

import static ru.job4j.jobseeker.TestHelper.USER;
import static ru.job4j.jobseeker.web.json.JsonHelper.asJson;
import static ru.job4j.jobseeker.web.mock.WebMock.GET_METHOD;
import static ru.job4j.jobseeker.web.mock.WebMock.POST_METHOD;

class LoginControllerTest extends AbstractControllerTest {
    private static final String LOGIN_URL = "/login";

    @Test
    void doGetLoginUnauthorized() {
        webMock.withUrl(LOGIN_URL)
                .withNoAuth()
                .withMethod(GET_METHOD)
                .withForwardExpected()
                .act()
                .checkForwardPath("WEB-INF/jsp/login.jsp");
    }

    @Test
    void doGetLoginAuthorized() throws Exception {
        webMock.withUrl(LOGIN_URL)
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withRedirectExpected()
                .act()
                .checkRedirectPath("/profile");
    }

    @Test
    void doPostLogin() throws Exception {
        webMock.withUrl(LOGIN_URL)
                .withNoAuth()
                .withMethod(POST_METHOD)
                .withRequestParameter("login", USER.getLogin())
                .withRequestParameter("password", USER.getPassword())
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson("redirection", "profile"));
    }

    @Test
    void doPostUnregistered() throws Exception {
        webMock.withUrl(LOGIN_URL)
                .withNoAuth()
                .withMethod(POST_METHOD)
                .withRequestParameter("login", "unregistered")
                .withRequestParameter("password", "unregistered")
                .withAnswerExpected()
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_FORBIDDEN)
                .checkResponseBody(asJson("error", "User with the given login is not registered"));
    }

}
