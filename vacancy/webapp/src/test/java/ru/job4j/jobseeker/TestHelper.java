package ru.job4j.jobseeker;

import ru.job4j.jobseeker.model.*;

import java.util.Date;

import static ru.job4j.vacancy.util.TimeUtil.toDate;
import static ru.job4j.vacancy.util.TimeUtil.toZonedDateTime;

public class TestHelper {
    public static final ScanSource SOURCE_HH_RU = new ScanSource(1, "hh_ru", "https://hh.ru/", "https://hh.ru/favicon.ico");
    public static final ScanSource SOURCE_HABR_COM = new ScanSource(2, "habr_com", "https://career.habr.com/", "https://career.habr.com/images/favicons/favicon-16.png");
    public static final ScanSource SOURCE_SQL_RU = new ScanSource(3, "sql_ru", "https://www.sql.ru/", "https://www.sql.ru/favicon.ico");

    public static final User USER = new User(10, "user", "password", toDate("2020-01-09 10:00"), Role.USER);
    public static final User ADMIN = new User(11, "admin", "admin", toDate("2020-01-01 04:55"), Role.ADMIN);

    public static final Task USER_TASK1 = new Task(100, "java", "Moscow", toDate("2020-01-01 10:00"), true, null, RepeatRule.manually, SOURCE_HH_RU, 0, 10);
    public static final Task USER_TASK2 = new Task(101, "java", "Samara", toDate("2020-01-10 16:00"), true, null, RepeatRule.daily, SOURCE_HH_RU, 0, 10);
    public static final Task USER_TASK3 = new Task(102, "kotlin", "New York", toDate("2020-01-13 00:00"), true, null, RepeatRule.monthly, SOURCE_HABR_COM, 0, 10);
    public static final Task ADMIN_TASK1 = new Task(103, "frontend", "Tolyatti", toDate("2020-02-01 02:00"), true, null, RepeatRule.weekly, SOURCE_SQL_RU, 0, 11);
    public static final Task ADMIN_TASK2 = new Task(104, "javascript", null, toDate("2019-11-28 11:00"), true, null, RepeatRule.manually, SOURCE_HABR_COM, 0, 11);

    public static final Vacancy TASK1_VACANCY1 = new Vacancy(1000, "Java programmer (Moscow)", "url", "Description", toZonedDateTime("2020-01-25 16:12"), false, USER_TASK1.getId());
    public static final Vacancy TASK1_VACANCY2 = new Vacancy(1001, "Fullstack programmer (Samara)", "url", "Description", toZonedDateTime("2020-01-27 13:00"), false, USER_TASK1.getId());
    public static final Vacancy TASK1_VACANCY3 = new Vacancy(1002, "Java programmer (relocation)", "url", "Description", toZonedDateTime("2020-01-27 16:33"), false, USER_TASK1.getId());
    public static final Vacancy TASK1_VACANCY4 = new Vacancy(1003, "Android programmer (full-day)", "url", "Description", toZonedDateTime("2020-01-30 08:12"), false, USER_TASK1.getId());
    public static final Vacancy TASK2_VACANCY1 = new Vacancy(1004, "Java programmer (Moscow)", "url", "Description", toZonedDateTime("2020-01-27 06:00"), false, USER_TASK2.getId());
    public static final Vacancy TASK2_VACANCY2 = new Vacancy(1005, "Scala programmer (visa sponsorship)", "url", "Description", toZonedDateTime("2020-01-28 14:13"), false, USER_TASK2.getId());
    public static final Vacancy TASK2_VACANCY3 = new Vacancy(1006, "Fullstack programmer (Java, Kotlin)", "url", "Description", toZonedDateTime("2020-01-30 07:58"), false, USER_TASK2.getId());
    public static final Vacancy ADMIN_TASK1_VACANCY1 = new Vacancy(1007, "JS-react programmer", "url", "Ajax description", toZonedDateTime("2020-01-30 8:34"), false, ADMIN_TASK1.getId());

    public static final TestMatchers<User> USER_MATCHERS = TestMatchers.useFieldsComparator(User.class, "registered");

    public static final TestMatchers<User> USER_SENSITIVE_MATCHERS = TestMatchers.useFieldsComparator(User.class, "id", "registered", "password");

    public static final TestMatchers<Task> TASK_MATCHERS = TestMatchers.useFieldsComparator(Task.class, "id", "user");

    public static final TestMatchers<ScanSource> SOURCE_MATCHERS = TestMatchers.useFieldsComparator(ScanSource.class);

    public static final TestMatchers<Vacancy> VACANCY_MATCHERS = TestMatchers.useFieldsComparator(Vacancy.class, "id", "task");

    public static User createNewUser() {
        return new User(null, "created", "new_password", new Date(), Role.USER);
    }

    public static Task createNewTask(User user, Date launch, RepeatRule rule) {
        return new Task("new", "new city", new Date(), true, launch, rule, SOURCE_SQL_RU, 0, user);
    }

    public static Vacancy createNewVacancy(Integer taskId) {
        return new Vacancy(null, "new vacancy", "new url", "new description", toZonedDateTime("2020-02-10 16:48"), false, taskId);
    }
}
