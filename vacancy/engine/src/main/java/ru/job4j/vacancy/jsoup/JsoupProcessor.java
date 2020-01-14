package ru.job4j.vacancy.jsoup;

import ru.job4j.vacancy.model.VacancyData;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents basic interface of jsoup parser
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-07-30
 */
public interface JsoupProcessor {
    /**
     * Parses vacancies from site to list
     *
     * @param dateLimit date limiter
     * @return parsed vacancies
     */
    List<VacancyData> parseVacancies(ZonedDateTime dateLimit);

    /**
     * Sets vacancy filter as string predicate
     *
     * @param filter vacancy filter
     */
    void submitSearchFilter(Predicate<String> filter);

    /**
     * Appends in link line the given word
     *
     * @param word word
     */
    void submitSearchWord(String word);
}
