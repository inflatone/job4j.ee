package ru.job4j.jobseeker.web;

import org.junit.jupiter.api.Test;
import ru.job4j.jobseeker.TestHelper;
import ru.job4j.jobseeker.dao.VacancyDao;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.job4j.jobseeker.TestHelper.*;
import static ru.job4j.jobseeker.web.json.JsonHelper.asJson;
import static ru.job4j.jobseeker.web.mock.WebMock.GET_METHOD;
import static ru.job4j.jobseeker.web.mock.WebMock.POST_METHOD;

class VacancyControllerTest extends AbstractControllerTest {
    private static final String VACANCY_URL = "/vacancy";

    @Inject
    private VacancyDao dao;

    @Test
    void findAll() throws Exception {
        webMock.withUrl(VACANCY_URL)
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withAction("find")
                .withRequestParameter("taskId", TestHelper.USER_TASK2.getId())
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson(List.of(TASK2_VACANCY3, TASK2_VACANCY2, TASK2_VACANCY1)));
    }

    @Test
    void highlight() {
        assertFalse(dao.findAll(ADMIN_TASK1.getId()).get(0).isHighlighted());
        webMock.withUrl(VACANCY_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("") // empty
                .withRequestParameter("id", ADMIN_TASK1_VACANCY1.getId())
                .withRequestParameter("taskId", ADMIN_TASK1.getId())
                .withRequestParameter("userId", ADMIN.getId())
                .withRequestParameter("highlighted", true)
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT);
        assertTrue(dao.findAll(ADMIN_TASK1.getId()).get(0).isHighlighted());
    }

    @Test
    void highlightOff() {
        webMock.withUrl(VACANCY_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("") // empty
                .withRequestParameter("id", ADMIN_TASK1_VACANCY1.getId())
                .withRequestParameter("taskId", ADMIN_TASK1.getId())
                .withRequestParameter("userId", ADMIN.getId())
                .withRequestParameter("highlighted", false)
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT);
        VACANCY_MATCHERS.assertMatch(dao.findAll(ADMIN_TASK1.getId()).get(0), ADMIN_TASK1_VACANCY1);
    }

    @Test
    void remove() {
        VACANCY_MATCHERS.assertMatch(dao.findAll(USER_TASK2.getId()), TASK2_VACANCY3, TASK2_VACANCY2, TASK2_VACANCY1);
        webMock.withUrl(VACANCY_URL)
                .withAuth(USER)
                .withMethod(POST_METHOD)
                .withAction("delete")
                .withRequestParameter("id", TASK2_VACANCY1.getId())
                .withRequestParameter("taskId", USER_TASK2.getId())
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT);
        VACANCY_MATCHERS.assertMatch(dao.findAll(USER_TASK2.getId()), TASK2_VACANCY3, TASK2_VACANCY2);
    }
}
