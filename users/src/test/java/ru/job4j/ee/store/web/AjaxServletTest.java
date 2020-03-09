package ru.job4j.ee.store.web;

import one.util.streamex.StreamEx;
import org.junit.jupiter.api.Test;
import ru.job4j.ee.store.model.BaseNamedEntity;
import ru.job4j.ee.store.model.Role;
import ru.job4j.ee.store.web.mock.WebMock;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.job4j.ee.store.AssertionUtil.*;
import static ru.job4j.ee.store.web.AjaxServlet.CountryTo;
import static ru.job4j.ee.store.web.json.JsonUtil.asJson;
import static ru.job4j.ee.store.web.mock.WebMock.GET_METHOD;
import static ru.job4j.ee.store.web.mock.WebMock.POST_METHOD;

public class AjaxServletTest extends AbstractServletTest {
    private static final String AJAX_URL = "/ajax";

    @Test
    void getAllRoles() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(AJAX_URL)
                .withAuth(ADMIN)
                .withMethod(GET_METHOD)
                .withDataType("roles")
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson(Set.of(Role.values())))
                .terminate();
    }

    @Test
    void getUserRole() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(AJAX_URL)
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withDataType("roles")
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson(Set.of(Role.USER)))
                .terminate();
    }

    @Test
    void getCountries() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(AJAX_URL)
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withDataType("countries")
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson(List.of(RUSSIA, USA, UKRAINE)))
                .terminate();
    }

    @Test
    void updateCountries() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(AJAX_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withDataType("countries")
                .withAction("update")
                .withRequestJson(asJson(List.of(
                        new CountryTo(RUSSIA.getName()), new CountryTo(USA.getName()),
                        new CountryTo("New Country"), new CountryTo("New Country 2"))))
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson("message", "4 countries received, 2 countries saved"))
                .terminate();
        var all = StreamEx.of(cityRepository.findAllCountries()).map(BaseNamedEntity::getName).toSet();
        assertTrue(all.containsAll(Set.of(RUSSIA.getName(), UKRAINE.getName(), USA.getName(), "New Country", "New Country 2")));
    }

    @Test
    void clearCountries() throws Exception {
        assertTrue(cityRepository.findAllCountries().contains(USA));
        WebMock.create()
                .setUp(injector)
                .withUrl(AJAX_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withDataType("countries")
                .withAction("clear")
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson("message", "1 countries removed"))
                .terminate();
        assertFalse(cityRepository.findAllCountries().contains(USA));
    }

    @Test
    void getCities() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(AJAX_URL)
                .withAuth(ADMIN)
                .withMethod(GET_METHOD)
                .withDataType("cities")
                .withRequestParameter("id", MOSCOW.getCountry().getId())
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson(List.of(MOSCOW, SAINT_PETERSBURG)))
                .terminate();
    }

    @Test
    void getCitiesEmpty() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(AJAX_URL)
                .withAuth(ADMIN)
                .withMethod(GET_METHOD)
                .withDataType("cities")
                .withRequestParameter("id", NEW_CITY.getCountry().getId())
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson(List.of()))
                .terminate();
    }

    @Test
    void getRolesWithoutAuth() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(AJAX_URL)
                .withNoAuth()
                .withMethod(GET_METHOD)
                .withDataType("roles")
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson(Set.of(Role.USER)))
                .terminate();
    }

    @Test
    void getCitiesWithoutAuth() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(AJAX_URL)
                .withNoAuth()
                .withMethod(GET_METHOD)
                .withDataType("cities")
                .withRequestParameter("id", MOSCOW.getCountry().getId())
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson(List.of(MOSCOW, SAINT_PETERSBURG)))
                .terminate();
    }

    @Test
    void doGetWithExceptionHandling() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(AJAX_URL)
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withDataType("cities")
                .withRequestException("id", new IOException())
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_FORBIDDEN)
                .checkResponseBody(asJson("error", IOException.class.getSimpleName()))
                .terminate();
    }

    @Test
    void getWrongAction() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(AJAX_URL)
                .withAuth(USER)
                .withMethod(POST_METHOD)
                .withDataType("countries")
                .withAction("unlisted")
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_FORBIDDEN)
                .checkResponseBody(asJson("error", "Wrong action has been chosen, please try again"))
                .terminate();
    }
}
