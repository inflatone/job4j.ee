package ru.job4j.ee.store.web;

import org.junit.jupiter.api.Test;
import ru.job4j.ee.store.model.User;
import ru.job4j.ee.store.web.mock.WebMock;

import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.job4j.ee.store.AssertionUtil.*;
import static ru.job4j.ee.store.web.json.JsonUtil.asJson;
import static ru.job4j.ee.store.web.mock.WebMock.GET_METHOD;
import static ru.job4j.ee.store.web.mock.WebMock.POST_METHOD;

class AdminServletTest extends AbstractServletTest {
    private static final String ADMIN_URL = "/users";

    @Test
    void doCreate() throws Exception {
        var userToCreate = new User(UNREGISTERED);
        WebMock.create()
                .setUp(injector)
                .withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestJson(asJsonWithPassword(userToCreate))
                .act()
                .checkResponseStatus(HttpServletResponse.SC_CREATED)
                .terminate();

        var persisted = userRepository.findByLogin(UNREGISTERED.getLogin());
        assertMatch(persisted, userToCreate);
    }

    @Test
    void doUpdate() throws Exception {
        var updatedModel = new User(USER);
        updatedModel.setName("UpdatedName");
        updatedModel.setPassword("UpdatedPassword");
        updatedModel.setCity(NEW_CITY);

        WebMock.create()
                .setUp(injector)
                .withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestJson(asJsonWithPassword(updatedModel))
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT)
                .terminate();
        var persisted = userRepository.find(USER.getId());
        assertMatch(persisted, updatedModel);
    }

    @Test
    void doUpdateWithoutPassword() throws Exception {
        var updatedModel = new User(USER);
        updatedModel.setName("UpdatedName");
        updatedModel.setCity(NEW_CITY);

        WebMock.create()
                .setUp(injector)
                .withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestJson(asJson(updatedModel)) // w/o password, has to stay the same
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT)
                .terminate();
        var persisted = userRepository.find(USER.getId());
        assertMatch(persisted, updatedModel);
    }

    @Test
    void doUpdateNotFound() throws Exception {
        var updatedModel = new User(UNREGISTERED);
        updatedModel.setId(0);

        WebMock.create()
                .setUp(injector)
                .withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestJson(asJson(updatedModel))
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_FORBIDDEN)
                .checkResponseBody(asJson("error", "Not found entity with id=" + updatedModel.getId()))
                .terminate();
    }

    @Test
    void doDelete() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("delete")
                .withRequestParameter("id", USER.getId())
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT)
                .terminate();
        assertNull(userRepository.find(USER.getId()));
    }

    @Test
    void doDeleteWithMissingId() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("delete")
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_FORBIDDEN)
                .checkResponseBody(asJson("error", "id must be present"))
                .terminate();
    }

    @Test
    void doEnable() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("enable")
                .withRequestParameter("id", BANNED.getId())
                .withRequestParameter("enabled", true)
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT)
                .terminate();
        assertTrue(userRepository.find(BANNED.getId()).isEnabled());
    }

    @Test
    void doDisable() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("enable")
                .withRequestParameter("id", USER.getId())
                .withRequestParameter("enabled", false)
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT)
                .terminate();
        assertFalse(userRepository.find(USER.getId()).isEnabled());
    }

    @Test
    void doEnableWithMissingParameter() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("enable")
                .withRequestParameter("id", BANNED.getId())
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_FORBIDDEN)
                .checkResponseBody(asJson("error", "enabled must be present"))
                .terminate();
    }

    @Test
    void getAllUsers() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(GET_METHOD)
                .withAction("find")
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson(List.of(ADMIN, BANNED, USER)))
                .terminate();
    }

    @Test
    void getUserById() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(GET_METHOD)
                .withAction("find")
                .withRequestParameter("id", USER.getId())
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson(USER))
                .terminate();
    }


    @Test
    void getListJsp() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(GET_METHOD)
                .withForwardExpected()
                .act()
                .checkForwardPath("WEB-INF/jsp/list.jsp")
                .terminate();
    }

    @Test
    void doPostWithExceptionHandling() throws Exception {
        var message = "this is only simulation";
        WebMock.create()
                .setUp(injector)
                .withUrl(ADMIN_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("delete")
                .withRequestException("id", new SQLException(message))
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_FORBIDDEN)
                .checkResponseBody(asJson("error", message))
                .terminate();
    }
}
