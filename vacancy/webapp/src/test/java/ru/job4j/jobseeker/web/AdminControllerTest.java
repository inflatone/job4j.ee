package ru.job4j.jobseeker.web;

import org.junit.jupiter.api.Test;
import ru.job4j.jobseeker.dao.UserDao;
import ru.job4j.jobseeker.model.Role;
import ru.job4j.jobseeker.model.User;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.job4j.jobseeker.TestHelper.*;
import static ru.job4j.jobseeker.web.json.JsonHelper.asJson;
import static ru.job4j.jobseeker.web.json.JsonHelper.asJsonAdditional;
import static ru.job4j.jobseeker.web.mock.WebMock.GET_METHOD;
import static ru.job4j.jobseeker.web.mock.WebMock.POST_METHOD;

class AdminControllerTest extends AbstractControllerTest {
    private static final String ADMIN_URL = "/users";

    @Inject
    private UserDao dao;

    @Test
    void doCreate() throws Exception {
        var newUser = createNewUser();
        var userToCreate = new User(newUser);
        webMock.withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestJson(asJsonWithPassword(userToCreate))
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_CREATED);

        var persisted = dao.findByLogin(newUser.getLogin());
        USER_SENSITIVE_MATCHERS.assertMatch(persisted, userToCreate);
    }

    @Test
    void doUpdate() throws Exception {
        var updatedModel = new User(USER);
        updatedModel.setLogin("UpdatedName");
        updatedModel.setPassword("UpdatedPassword");
        updatedModel.setRole(Role.ADMIN);

        webMock.withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestJson(asJsonWithPassword(updatedModel))
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT);
        var persisted = dao.find(USER.getId());
        USER_MATCHERS.assertMatch(persisted, updatedModel);
    }

    @Test
    void doUpdateWithoutPassword() throws Exception {
        var updatedModel = new User(USER);
        updatedModel.setLogin("UpdatedName");

        webMock.withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestJson(asJson(updatedModel)) // w/o password, has to stay the same
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT);
        var persisted = dao.find(USER.getId());
        USER_MATCHERS.assertMatch(persisted, updatedModel);
    }

    @Test
    void doUpdateNotFound() throws Exception {
        var newUser = createNewUser();
        var updatedModel = new User(newUser);
        updatedModel.setId(0);

        webMock.withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestJson(asJson(updatedModel))
                .withAnswerExpected()
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_FORBIDDEN)
                .checkResponseBody(asJson("error", "Not found entity with id=" + updatedModel.getId()));
    }

    @Test
    void doDelete() {
        webMock.withUrl(ADMIN_URL)
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
    void doDeleteWithMissingId() throws Exception {
        webMock.withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("delete")
                .withAnswerExpected()
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_FORBIDDEN)
                .checkResponseBody(asJson("error", "id must be present"));
    }

    @Test
    void getAllUsers() throws Exception {
        webMock.withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(GET_METHOD)
                .withAction("find")
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson(List.of(ADMIN, USER)));
    }

    @Test
    void getUserById() throws Exception {
        webMock.withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(GET_METHOD)
                .withAction("find")
                .withRequestParameter("id", USER.getId())
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson(USER));
    }


    @Test
    void getListJsp() {
        webMock.withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(GET_METHOD)
                .withForwardExpected()
                .act()
                .checkForwardPath("WEB-INF/jsp/users.jsp");
    }

    @Test
    void doPostWithExceptionHandling() throws Exception {
        var message = "this is only simulation";
        webMock.withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("delete")
                .withRequestException("id", new RuntimeException(message))
                .withAnswerExpected()
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_FORBIDDEN)
                .checkResponseBody(asJson("error", message));
    }

    public static String asJsonWithPassword(User user) {
        return asJsonAdditional(user, "password", user.getPassword());
    }
}
