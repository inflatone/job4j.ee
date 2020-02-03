package ru.job4j.jobseeker.service;

import org.junit.jupiter.api.Test;
import ru.job4j.jobseeker.dao.VacancyDao;
import ru.job4j.jobseeker.exeption.ApplicationException;
import ru.job4j.jobseeker.model.Vacancy;

import javax.inject.Inject;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.job4j.jobseeker.TestHelper.*;

class VacancyServiceTest extends AbstractServiceTest {
    @Inject
    private VacancyService service;

    @Test
    void findAll() {
        List<Vacancy> vacancies = service.findAll(USER_TASK1.getId(), USER.getId());
        VACANCY_MATCHERS.assertMatch(vacancies, TASK1_VACANCY4, TASK1_VACANCY3, TASK1_VACANCY2, TASK1_VACANCY1);
    }

    @Test
    void findAllNotOwn() {
        var thrown = assertThrows(ApplicationException.class, () -> service.findAll(USER_TASK1.getId(), ADMIN.getId()));
        assertEquals("Not found entity with id=" + USER_TASK1.getId(), thrown.getMessage());

    }

    @Test
    void delete() {
        service.delete(TASK1_VACANCY1.getId(), USER_TASK1.getId(), USER.getId());
        List<Vacancy> vacancies = service.findAll(USER_TASK1.getId(), USER.getId());
        VACANCY_MATCHERS.assertMatch(vacancies, TASK1_VACANCY4, TASK1_VACANCY3, TASK1_VACANCY2);
    }

    @Test
    void deleteNotOwn() {
        var thrown = assertThrows(ApplicationException.class, () -> service.delete(TASK1_VACANCY1.getId(), USER_TASK1.getId(), ADMIN.getId()));
        assertEquals("Not found entity with id=" + TASK1_VACANCY1.getId(), thrown.getMessage());
    }

    @Test
    void deleteFromAnotherTask() {
        var thrown = assertThrows(ApplicationException.class, () -> service.delete(TASK1_VACANCY1.getId(), USER_TASK2.getId(), USER.getId()));
        assertEquals("Not found entity with id=" + TASK1_VACANCY1.getId(), thrown.getMessage());
    }


    @Test
    void highlight() {
        service.highlight(TASK1_VACANCY1.getId(), USER_TASK1.getId(), USER.getId(), true);
        List<Vacancy> vacancies = service.findAll(USER_TASK1.getId(), USER.getId());

        var updatedVacancy = new Vacancy(TASK1_VACANCY1);
        updatedVacancy.setHighlighted(true);

        VACANCY_MATCHERS.assertMatch(vacancies, TASK1_VACANCY4, TASK1_VACANCY3, TASK1_VACANCY2, updatedVacancy);

        service.highlight(TASK1_VACANCY1.getId(), USER_TASK1.getId(), USER.getId(), false);
        vacancies = service.findAll(USER_TASK1.getId(), USER.getId());
        VACANCY_MATCHERS.assertMatch(vacancies, TASK1_VACANCY4, TASK1_VACANCY3, TASK1_VACANCY2, TASK1_VACANCY1);
    }

    @Test
    void highlightNotOwn() {
        var thrown = assertThrows(ApplicationException.class, () -> service.highlight(TASK1_VACANCY1.getId(), USER_TASK1.getId(), ADMIN.getId(), true));
        assertEquals("Not found entity with id=" + TASK1_VACANCY1.getId(), thrown.getMessage());
    }

    @Test
    void highlightFromAnotherTask() {
        var thrown = assertThrows(ApplicationException.class, () -> service.highlight(TASK1_VACANCY1.getId(), USER_TASK2.getId(), USER.getId(), true));
        assertEquals("Not found entity with id=" + TASK1_VACANCY1.getId(), thrown.getMessage());
    }

    @Test
    void saveAll(VacancyDao dao) {
        var newVacancy = createNewVacancy(USER_TASK1.getId());
        dao.saveAll(List.of(newVacancy.getData(), TASK1_VACANCY1.getData()), USER_TASK1);

        List<Vacancy> vacancies = service.findAll(USER_TASK1.getId(), USER.getId());
        VACANCY_MATCHERS.assertMatch(vacancies,
                newVacancy, TASK1_VACANCY4, TASK1_VACANCY3, TASK1_VACANCY2, TASK1_VACANCY1);

    }
}
