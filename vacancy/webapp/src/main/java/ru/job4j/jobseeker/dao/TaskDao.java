package ru.job4j.jobseeker.dao;

import com.google.common.collect.Maps;
import one.util.streamex.StreamEx;
import ru.job4j.jobseeker.model.*;
import ru.job4j.vacancy.model.SourceTitle;

import javax.inject.Inject;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * User DAO shell
 * Contains methods to complete CRUD operations with {@link Task} objects
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-16
 */
public class TaskDao {
    private static final AtomicInteger SEQ = new AtomicInteger(1010);

    private static final ScanSource HH_RU = new ScanSource(SEQ.incrementAndGet(), SourceTitle.hh_ru, null, null);
    private static final ScanSource HABR_COM = new ScanSource(SEQ.incrementAndGet(), SourceTitle.habr_com, null, null);
    private static final ScanSource SQL_RU = new ScanSource(SEQ.incrementAndGet(), SourceTitle.sql_ru, null, null);

    private static final Map<Integer, ScanSource> sources = Maps.uniqueIndex(List.of(HH_RU, HABR_COM, SQL_RU), BaseEntity::getId);

    private final Map<Integer, Task> tasks;

    private final UserDao dao;

    private Map<Integer, Task> initMap() {
        return new ConcurrentHashMap<>() {
            {
                Map<String, User> users = StreamEx.of(dao.findAll()).toMap(User::getLogin, Function.identity());

                User admin = users.get("admin");
                User user = users.get("user");

                Date limitYearStart = toDate(LocalDate.of(2019, Month.JANUARY, 1));
                Date limitWeekStart = toDate(LocalDate.now().with(WeekFields.SUNDAY_START.dayOfWeek(), 1L));

                Date nextDayStart = toDate(LocalDateTime.now().plusDays(1));
                Date nextWeekStart = toDate(LocalDateTime.now().plusWeeks(1));

                put(SEQ.incrementAndGet(), new Task(SEQ.get(), "Java", "Samara", limitYearStart, nextDayStart, RepeatRule.daily, HH_RU, admin));
                put(SEQ.incrementAndGet(), new Task(SEQ.get(), "Kotlin", null, limitWeekStart, null, RepeatRule.daily, HABR_COM, admin));
                put(SEQ.incrementAndGet(), new Task(SEQ.get(), "Java", "Tolyatti", limitYearStart, null, RepeatRule.weekly, HABR_COM, admin));
                put(SEQ.incrementAndGet(), new Task(SEQ.get(), "Java", null, limitYearStart, nextDayStart, RepeatRule.monthly, HH_RU, user));
                put(SEQ.incrementAndGet(), new Task(SEQ.get(), "Java", null, limitWeekStart, nextWeekStart, RepeatRule.monthly, SQL_RU, user));
            }

            private Date toDate(LocalDate ld) {
                return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }

            private Date toDate(LocalDateTime ldt) {
                return Date.from(ldt.truncatedTo(ChronoUnit.MINUTES).toInstant(ZoneOffset.UTC));
            }
        };
    }

    @Inject
    public TaskDao(UserDao dao) {
        this.dao = dao;
        this.tasks = initMap();
    }

    public List<ScanSource> findAllScanSources() {
        return List.copyOf(sources.values());
    }

    public List<Task> findAll(int userId) {
        return StreamEx.of(tasks.values()).filterBy(t -> t.getUser().getId(), userId).toList();
    }

    public Task find(int id, int userId) {
        return StreamEx.of(tasks.values())
                .findAny(t -> t.getId() == id && t.getUser().getId() == userId)
                .orElse(null);
    }

    public Task create(Task task, int userId) {
        task.setId(SEQ.incrementAndGet());
        task.setUser(dao.find(userId));
        task.setSource(sources.get(task.getSource().getId()));
        tasks.put(task.getId(), task);
        return task;
    }

    public boolean update(Task task, int userId) {
        task.setUser(dao.find(userId));
        return tasks.computeIfPresent(task.getId(), (k, v) -> {
            task.setKeyword(v.getKeyword());
            task.setCity(v.getCity());
            task.setSource(v.getSource());
            task.setLimit(v.getLimit());
            return task;
        }) != null;
    }

    public boolean delete(int id, int userId) {
        return tasks.computeIfPresent(id, (k, v) -> v.getUser().getId() == userId ? null : v) == null;
    }
}