package ru.job4j.jobseeker.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.quartz.Trigger;
import ru.job4j.jobseeker.TestHelper;
import ru.job4j.jobseeker.dao.TaskDao;
import ru.job4j.jobseeker.model.RepeatRule;
import ru.job4j.jobseeker.model.Task;
import ru.job4j.jobseeker.model.User;
import ru.job4j.jobseeker.model.Vacancy;
import ru.job4j.jobseeker.service.executor.WebJobExecutor;
import ru.job4j.vacancy.jsoup.JsoupProcessor;
import ru.job4j.vacancy.jsoup.ParseParameters;
import ru.job4j.vacancy.util.TimeUtil;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.job4j.jobseeker.TestHelper.*;
import static ru.job4j.jobseeker.web.json.JsonHelper.asJson;
import static ru.job4j.jobseeker.web.json.JsonObjectMapper.getJsonMapper;
import static ru.job4j.jobseeker.web.mock.WebMock.GET_METHOD;
import static ru.job4j.jobseeker.web.mock.WebMock.POST_METHOD;

class TaskControllerTest extends AbstractControllerTest {
    private static final String TASK_URL = "/task";

    @Inject
    private TaskDao dao;

    @Test
    void findAll() throws Exception {
        webMock.withUrl(TASK_URL)
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withAction("find")
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson(List.of(USER_TASK1, USER_TASK2, USER_TASK3)));
    }

    @Test
    void findTask() throws Exception {
        webMock.withUrl(TASK_URL)
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withAction("find")
                .withRequestParameter("id", USER_TASK2.getId())
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson(USER_TASK2));
    }

    @Test
    void viewJsp() {
        webMock.withUrl(TASK_URL)
                .withAuth(USER)
                .withMethod(GET_METHOD)
                .withForwardExpected()
                .act()
                .checkForwardPath("WEB-INF/jsp/task.jsp");
    }

    @Test
    void create() throws Exception {
        var newTask = TestHelper.createNewTask(USER, null, RepeatRule.manually);
        var taskToCreate = new Task(newTask);

        webMock.withUrl(TASK_URL)
                .withAuth(USER)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestJson(asJsonIgnoreFields(taskToCreate, "id", "ruleOrdinal", "launch"))
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_CREATED);

        List<Task> actual = dao.findAll(USER.getId());
        TASK_MATCHERS.assertMatch(actual, USER_TASK1, USER_TASK2, USER_TASK3, taskToCreate);
    }

    @Test
    void createAsAdmin() throws Exception {
        var newTask = TestHelper.createNewTask(USER, null, RepeatRule.manually);
        var taskToCreate = new Task(newTask);

        webMock.withUrl(TASK_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestJson(asJsonWithUserAndIgnoreFields(taskToCreate, USER, "id", "ruleOrdinal", "launch"))
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_CREATED);

        List<Task> actual = dao.findAll(USER.getId());
        TASK_MATCHERS.assertMatch(actual, USER_TASK1, USER_TASK2, USER_TASK3, taskToCreate);
    }

    @Test
    void update(WebJobExecutor mockExecutor) throws Exception {
        when(mockExecutor.reschedule(anyString(), anyBoolean(), any(Date.class), anyString(), anyMap()))
                .thenReturn(mock(Trigger.class));

        var updatedModel = new Task(USER_TASK1);
        updatedModel.setLimit(new Date());
        updatedModel.setRule(RepeatRule.daily);
        updatedModel.setLaunch(TimeUtil.toDate("2020-03-01 10:00"));

        webMock.withUrl(TASK_URL)
                .withAuth(USER)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestJson(asJsonIgnoreFields(updatedModel, "ruleOrdinal", "keyword", "city", "source"))
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT);
        List<Task> actual = dao.findAll(USER.getId());
        TASK_MATCHERS.assertMatch(actual, updatedModel, USER_TASK2, USER_TASK3);
    }

    @Test
    void delete() {
        assertNotNull(dao.find(USER_TASK1.getId(), USER.getId()));
        webMock.withUrl(TASK_URL)
                .withAuth(USER)
                .withMethod(POST_METHOD)
                .withAction("delete")
                .withRequestParameter("id", USER_TASK1.getId())
                .withNoDefaultStatusExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT);
        assertNull(dao.find(USER_TASK1.getId(), USER.getId()));
    }

    @Test
    void start(Set<JsoupProcessor> mockProcessors) throws Exception {
        Vacancy newVacancy = createNewVacancy(null);

        when(mockProcessors.iterator().next().parseVacancies(any(ParseParameters.class)))
                .thenReturn(List.of(newVacancy.getData()));

        webMock.withUrl(TASK_URL)
                .withAuth(USER)
                .withMethod(POST_METHOD)
                .withAction("start")
                .withRequestParameter("id", USER_TASK1.getId())
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK);
        Map<String, ?> launchLog = jsonAsPropertyMap(webMock.getResponseBody());
        assertEquals("OK", launchLog.get("status"));
        assertEquals(1, launchLog.get("foundAmount"));
        assertEquals(1, launchLog.get("addedAmount"));
    }

    @Test
    void recount() {

    }

    private static String asJsonWithUserAndIgnoreFields(Task obj, User user, String... fieldsToIgnore) {
        Map<String, Object> objAsPropertyMap = getJsonMapper().convertValue(obj, new TypeReference<>() {
        });
        objAsPropertyMap.keySet().removeAll(Set.of(fieldsToIgnore));
        objAsPropertyMap.put("user", Map.of("id", user.getId()));
        return asJson(objAsPropertyMap);
    }

    private static <T> String asJsonIgnoreFields(T obj, String... fieldsToIgnore) {
        Map<String, Object> objAsPropertyMap = getJsonMapper().convertValue(obj, new TypeReference<>() {
        });
        objAsPropertyMap.keySet().removeAll(Set.of(fieldsToIgnore));
        return asJson(objAsPropertyMap);
    }

    private static Map<String, ?> jsonAsPropertyMap(String json) throws JsonProcessingException {
        return getJsonMapper().readValue(json, new TypeReference<>() {
        });
    }
}
