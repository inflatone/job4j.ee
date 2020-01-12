package ru.job4j.ee.store.web;

import org.junit.jupiter.api.Test;
import ru.job4j.ee.store.model.User;
import ru.job4j.ee.store.web.mock.WebMock;

import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.job4j.ee.store.AssertionUtil.*;
import static ru.job4j.ee.store.web.json.JsonUtil.asJson;
import static ru.job4j.ee.store.web.mock.WebMock.GET_METHOD;
import static ru.job4j.ee.store.web.mock.WebMock.POST_METHOD;

class ProfileServletTest extends AbstractServletTest {
    private static final String PROFILE_URL = "/profile";

    @Test
    void doCreate() throws Exception {
        var userToCreate = new User(UNREGISTERED);
        WebMock.create()
                .setUp(injector)
                .withUrl(PROFILE_URL)
                .withNoAuth()
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestJson(asJsonWithPassword(userToCreate))
                .act()
                .checkResponseStatus(HttpServletResponse.SC_CREATED)
                .checkAuthorized(userToCreate)
                .terminate();

        var persisted = userRepository.findByLogin(UNREGISTERED.getLogin());
        assertMatch(persisted, userToCreate);
    }

    @Test
    void doCreateIfAuthorized() throws Exception {
        var userToCreate = new User(UNREGISTERED);
        WebMock.create()
                .setUp(injector)
                .withUrl(PROFILE_URL)
                .withAuth(USER)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestJson(asJsonWithPassword(userToCreate))
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT)
                .terminate();

        var persisted = userRepository.find(USER.getId());
        assertMatch(persisted, userToCreate);
    }

    @Test
    void doUpdateIfAdmin() throws Exception {
        var updatedModel = new User(USER);
        updatedModel.setName("UpdatedName");
        updatedModel.setPassword("UpdatedPassword");
        updatedModel.setCity(NEW_CITY);

        WebMock.create()
                .setUp(injector)
                .withUrl(PROFILE_URL)
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
    void doUpdateRedundantId() throws Exception {
        var userToUpdate = new User(USER);
        userToUpdate.setId(BANNED.getId());
        userToUpdate.setName("Updated");
        userToUpdate.setCity(NEW_CITY);

        WebMock.create()
                .setUp(injector)
                .withUrl(PROFILE_URL)
                .withAuth(USER)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestJson(asJson(userToUpdate))
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT)
                .terminate();
        var banned = userRepository.find(BANNED.getId());

        // request with another id has had no impact on another user
        assertMatch(userRepository.find(BANNED.getId()), banned);

        // actual auth user profile has been updated
        assertMatch(userRepository.find(USER.getId()), userToUpdate);
    }

    @Test
    void doDeleteAuthorizedUser() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(PROFILE_URL)
                .withAuth(USER)
                .withMethod(POST_METHOD)
                .withAction("delete")
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT)
                .checkUnauthorized()
                .terminate();
        assertNull(userRepository.find(USER.getId()));
    }

    @Test
    void doDeleteIfAdmin() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(PROFILE_URL)
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
    void getUsers() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(PROFILE_URL)
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withAction("find")
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson(USER))
                .terminate();
    }

    @Test
    void getUsersRedundantId() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(PROFILE_URL)
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withAction("find")
                .withRequestParameter("id", ADMIN.getId())
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson(USER)) // auth user data
                .terminate();
    }

    @Test
    void getProfileJsp() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(PROFILE_URL)
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withForwardExpected()
                .act()
                .checkForwardPath("WEB-INF/jsp/profile.jsp")
                .terminate();
    }

    @Test
    void getWrongAction() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(PROFILE_URL)
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withAction("unlisted")
                .withForwardWithErrorExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_BAD_REQUEST)
                .checkForwardErrorMessage("Wrong action has been chosen, please try again")
                .terminate();
    }
}
