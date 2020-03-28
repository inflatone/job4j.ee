package ru.job4j.jobseeker.web;

import org.junit.jupiter.api.Test;
import ru.job4j.jobseeker.dao.UserDao;
import ru.job4j.jobseeker.model.User;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.job4j.jobseeker.TestHelper.*;
import static ru.job4j.jobseeker.web.AdminControllerTest.asJsonWithPassword;
import static ru.job4j.jobseeker.web.json.JsonHelper.asJson;
import static ru.job4j.jobseeker.web.mock.WebMock.GET_METHOD;
import static ru.job4j.jobseeker.web.mock.WebMock.POST_METHOD;

class ProfileControllerTest extends AbstractControllerTest {
    private static final String PROFILE_URL = "/profile";

    @Inject
    private UserDao dao;

    @Test
    void doCreate() throws Exception {
        var newUser = createNewUser();
        var userToCreate = new User(newUser);
        webMock.withUrl(PROFILE_URL)
                .withNoAuth()
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestJson(asJsonWithPassword(userToCreate))
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_CREATED)
                .checkAuthorized(userToCreate);

        var persisted = dao.findByLogin(newUser.getLogin());
        USER_SENSITIVE_MATCHERS.assertMatch(persisted, userToCreate);
    }

    @Test
    void doCreateIfAuthorized() throws Exception {
        var newUser = createNewUser();
        var userToCreate = new User(newUser);
        webMock.withUrl(PROFILE_URL)
                .withAuth(USER)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestJson(asJsonWithPassword(userToCreate))
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT);

        var persisted = dao.find(USER.getId());
        USER_SENSITIVE_MATCHERS.assertMatch(persisted, userToCreate);
    }

    @Test
    void doUpdateIfAdmin() throws Exception {
        var updatedModel = new User(USER);
        updatedModel.setLogin("Updated");
        updatedModel.setPassword("UpdatedPassword");

        webMock.withUrl(PROFILE_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestJson(asJsonWithPassword(updatedModel))
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT);
        var persisted = dao.find(USER.getId());
        USER_SENSITIVE_MATCHERS.assertMatch(persisted, updatedModel);
    }

    @Test
    void doUpdateRedundantId() throws Exception {
        var userToUpdate = new User(USER);
        userToUpdate.setId(ADMIN.getId());
        userToUpdate.setLogin("Updated");

        webMock.withUrl(PROFILE_URL)
                .withAuth(USER)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestJson(asJson(userToUpdate))
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT);
        var stored = dao.find(ADMIN.getId());

        // request with another id has had no impact on another user
        USER_SENSITIVE_MATCHERS.assertMatch(dao.find(ADMIN.getId()), stored);

        // actual auth user profile has been updated
        USER_SENSITIVE_MATCHERS.assertMatch(dao.find(USER.getId()), userToUpdate);
    }

    @Test
    void doDeleteAuthorizedUser() {
        webMock.withUrl(PROFILE_URL)
                .withAuth(USER)
                .withMethod(POST_METHOD)
                .withAction("delete")
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT)
                .checkUnauthorized();
        assertNull(dao.find(USER.getId()));
    }

    @Test
    void doDeleteIfAdmin() {
        webMock.withUrl(PROFILE_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("delete")
                .withRequestParameter("id", USER.getId())
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT);
        assertNull(dao.find(USER.getId()));
    }

    @Test
    void getUsers() throws Exception {
        webMock.withUrl(PROFILE_URL)
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withAction("find")
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson(USER));
    }

    @Test
    void getProfileJsp() {
        webMock.withUrl(PROFILE_URL)
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withForwardExpected()
                .act()
                .checkForwardPath("WEB-INF/jsp/profile.jsp");
    }

    @Test
    void getWrongAction() throws Exception {
        webMock.withUrl(PROFILE_URL)
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withAction("unlisted")
                .withForwardWithErrorExpected()
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_BAD_REQUEST)
                .checkForwardErrorMessage("Wrong action has been chosen, please try again");
    }

    @Test
    void doPostWithExceptionHandling() throws Exception {
        webMock.withUrl(PROFILE_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("delete")
                .withRequestException("id", new RuntimeException())
                .withAnswerExpected()
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_FORBIDDEN)
                .checkResponseBody(asJson("error", "RuntimeException"));
    }
}
