package ru.job4j.vacancy.jsoup;

import ru.job4j.vacancy.model.SourceTitle;
import ru.job4j.vacancy.model.VacancyData;

import java.util.List;

/**
 * Represents basic interface of jsoup parser
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-07-30
 */
@FunctionalInterface
public interface JsoupProcessor {
    /**
     * Parses vacancies from site to list
     *
     * @param params parse params
     * @return parsed vacancies
     */
    List<VacancyData> parseVacancies(ParseParameters params);

    /**
     * Returns the source vacancy scan process from
     * Needs to identify processor among another ones
     * null by default = parser has no binding to the specific source (e.g. in tests)
     *
     * @return scan source
     */
    default SourceTitle getSourceTitle() {
        return null;
    }
}
