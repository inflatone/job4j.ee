package ru.job4j.jobseeker.service;

import org.junit.jupiter.api.Test;
import org.quartz.Trigger;
import ru.job4j.jobseeker.TestHelper;
import ru.job4j.jobseeker.TestMatchers;
import ru.job4j.jobseeker.exeption.ApplicationException;
import ru.job4j.jobseeker.model.RepeatRule;
import ru.job4j.jobseeker.model.ScanSource;
import ru.job4j.jobseeker.model.Task;
import ru.job4j.jobseeker.service.executor.WebJobExecutor;
import ru.job4j.vacancy.util.TimeUtil;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.job4j.jobseeker.TestHelper.*;
import static ru.job4j.vacancy.util.TimeUtil.toDate;

public class TaskServiceTest extends AbstractServiceTest {
    @Inject
    private TaskService service;

    @Test
    void find() {
        Task task = service.find(USER_TASK1.getId(), USER.getId());
        TASK_MATCHERS.assertMatch(task, USER_TASK1);
    }

    @Test
    void findNotOwn() {
        var thrown = assertThrows(ApplicationException.class, () -> service.find(ADMIN_TASK1.getId(), USER.getId()));
        assertEquals("Not found entity with id=" + ADMIN_TASK1.getId(), thrown.getMessage());
    }

    @Test
    void create(WebJobExecutor mockExecutor) {
        doReturn(mock(Trigger.class)).when(mockExecutor)
                .schedule(anyString(),anyBoolean(), any(Date.class), anyString(), anyMap());

        Task newTask = TestHelper.createNewTask(USER, null, RepeatRule.monthly);
        Date now = new Date();

        Task created = service.create(newTask, USER.getId());
        newTask.setId(created.getId());
        TASK_MATCHERS.assertMatch(created, newTask);

        TestMatchers.assertDatesAlmostEqual(created.getLaunch(), now);

        List<Task> tasks = service.findAll(USER.getId());
        TASK_MATCHERS.assertMatch(tasks, USER_TASK1, USER_TASK2, USER_TASK3, created);
    }

    @Test
    void createManually() {
        Task newTask = TestHelper.createNewTask(USER, null, RepeatRule.manually);

        Task created = service.create(newTask, USER.getId());
        newTask.setId(created.getId());
        TASK_MATCHERS.assertMatch(created, newTask);
        assertNull(created.getLaunch());

        List<Task> tasks = service.findAll(USER.getId());
        TASK_MATCHERS.assertMatch(tasks, USER_TASK1, USER_TASK2, USER_TASK3, created);
    }

    @Test
    void createDelayed() {
        String dateLine = "2020-03-15 17:00";
        Task newTask = TestHelper.createNewTask(USER, toDate(dateLine), RepeatRule.hourly);

        int id = service.create(newTask, USER.getId()).getId();
        newTask.setId(id);
        Task persisted = service.find(id, USER.getId());
        TASK_MATCHERS.assertMatch(persisted, newTask);
        assertEquals(dateLine, TimeUtil.asLine(persisted.getLaunch()));
    }

    @Test
    void update() {
        var updatedModel = new Task(USER_TASK1);
        updatedModel.setLaunch(toDate("2020-03-01 12:00"));
        updatedModel.setActive(false);
        updatedModel.setRule(RepeatRule.monthly);

        service.update(updatedModel, USER.getId());

        var persisted = service.find(USER_TASK1.getId(), USER.getId());
        TASK_MATCHERS.assertMatch(persisted, updatedModel);
    }

    @Test
    void updateNotFound() {
        Task task = TestHelper.createNewTask(USER, null, RepeatRule.manually);
        task.setId(0);

        var thrown = assertThrows(ApplicationException.class, () -> service.update(task, USER.getId()));
        assertEquals("Not found entity with id=0", thrown.getMessage());
    }

    @Test
    void updateNotOwn() {
        Task task = new Task(ADMIN_TASK2);
        var thrown = assertThrows(ApplicationException.class, () -> service.update(task, USER.getId()));
        assertEquals("Not found entity with id=" + ADMIN_TASK2.getId(), thrown.getMessage());
    }

    @Test
    void delete() {
        service.delete(ADMIN_TASK1.getId(), ADMIN.getId());
        var thrown = assertThrows(ApplicationException.class, () -> service.find(ADMIN_TASK1.getId(), ADMIN.getId()));
        assertEquals("Not found entity with id=" + ADMIN_TASK1.getId(), thrown.getMessage());
    }

    @Test
    void deleteNotOwn() {
        var thrown = assertThrows(ApplicationException.class, () -> service.find(ADMIN_TASK1.getId(), USER.getId()));
        assertEquals("Not found entity with id=" + ADMIN_TASK1.getId(), thrown.getMessage());
    }

    @Test
    void recount(VacancyService vacancyService) {
        var id = USER_TASK1.getId();
        var userId = USER.getId();
        assertEquals(0, service.find(id, userId).getAmount());
        service.recount(userId);

        assertEquals(vacancyService.findAll(id, userId).size(), service.find(id, userId).getAmount());
    }

    @Test
    void findAllSources() {
        List<ScanSource> sources = service.findAllSources();
        SOURCE_MATCHERS.assertMatch(sources, SOURCE_HABR_COM, SOURCE_HH_RU, SOURCE_SQL_RU);
    }
}
