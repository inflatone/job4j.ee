package ru.job4j.jobseeker.web.filter;

import org.junit.jupiter.api.Test;
import ru.job4j.jobseeker.web.AbstractControllerTest;

import javax.servlet.http.HttpServletResponse;

import static ru.job4j.jobseeker.TestHelper.USER;
import static ru.job4j.jobseeker.web.mock.WebMock.GET_METHOD;

public class SecurityFiltersTest extends AbstractControllerTest {
    @Test
    void doGetProfileUnauthorized() throws Exception {
        webMock.withNoAuth()
                .withMethod(GET_METHOD)
                .withUrl("/profile")
                .withAction("enable")
                .withRedirectExpected()
                .act()
                .checkRedirectPath("/login");
    }

    @Test
    void doGetProfileWithoutAction() throws Exception {
        webMock.withNoAuth()
                .withMethod(GET_METHOD)
                .withUrl("/profile")
                .withRedirectExpected()
                .act()
                .checkRedirectPath("/login");
    }

    @Test
    void doGetResource() {
        webMock.withNoAuth()
                .withMethod(GET_METHOD)
                .withUrl("/resources/style/style.css")
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doGetAdminUrl() throws Exception {
        webMock.withUrl("/users")
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withNoDefaultStatusExpected()
                .withForwardWithErrorExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_FORBIDDEN)
                .checkForwardErrorMessage("You're not authorized to pass here");
    }
}