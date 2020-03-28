package ru.job4j.jobseeker.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import ru.job4j.jobseeker.TestHelper;
import ru.job4j.jobseeker.model.RepeatRule;
import ru.job4j.jobseeker.model.Role;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.job4j.jobseeker.TestHelper.*;
import static ru.job4j.jobseeker.web.json.JsonObjectMapper.getJsonMapper;
import static ru.job4j.jobseeker.web.mock.WebMock.GET_METHOD;

class AjaxFormDataControllerTest extends AbstractControllerTest {
    private static final String AJAX_URL = "/ajax";

    @Test
    void getAllRoles() throws Exception {
        webMock.withUrl(AJAX_URL)
                .withAuth(ADMIN)
                .withMethod(GET_METHOD)
                .withFormType("user")
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK);

        var actual = jsonAsMap(webMock.getResponseBody());
        var expected = Map.of("roles", Map.of(Role.USER.ordinal(), Role.USER.name(), Role.ADMIN.ordinal(), Role.ADMIN.name()));
        assertEquals(expected, actual);
    }

    @Test
    void getUserRole() throws Exception {
        webMock.withUrl(AJAX_URL)
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withFormType("user")
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK);

        var actual = jsonAsMap(webMock.getResponseBody());
        var expected = Map.of("roles", Map.of(Role.USER.ordinal(), Role.USER.name()));
        assertEquals(expected, actual);
    }

    @Test
    void getRolesWithoutAuth() throws Exception {
        webMock.withUrl(AJAX_URL)
                .withNoAuth()
                .withMethod(GET_METHOD)
                .withFormType("user")
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK);
        var actual = jsonAsMap(webMock.getResponseBody());
        var expected = Map.of("roles", Map.of(Role.USER.ordinal(), Role.USER.name()));
        assertEquals(expected, actual);
    }

    @Test
    void getTaskFormData() throws Exception {
        webMock.withUrl(AJAX_URL)
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withFormType("task")
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK);

        var actual = jsonAsMap(webMock.getResponseBody());
        var expected = Map.of(
                "rules", Map.of(
                        RepeatRule.hourly.ordinal(), RepeatRule.hourly.name(),
                        RepeatRule.daily.ordinal(), RepeatRule.daily.name(),
                        RepeatRule.weekly.ordinal(), RepeatRule.weekly.name(),
                        RepeatRule.monthly.ordinal(), RepeatRule.monthly.name(),
                        RepeatRule.manually.ordinal(), RepeatRule.manually.name()
                ),
                "sources", Map.of(
                        SOURCE_HH_RU.getId(), SOURCE_HH_RU.getTitle().title(),
                        SOURCE_HABR_COM.getId(), SOURCE_HABR_COM.getTitle().title(),
                        SOURCE_SQL_RU.getId(), SOURCE_SQL_RU.getTitle().title()
                )
        );
        assertEquals(expected, actual);
    }

    private static Map<String, Map<Integer, String>> jsonAsMap(String json) throws JsonProcessingException {
        return getJsonMapper().readValue(json, new TypeReference<>() {});
    }
}
