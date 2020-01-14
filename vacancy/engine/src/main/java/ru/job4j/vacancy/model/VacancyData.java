package ru.job4j.vacancy.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;


/**
 * Vacancy model class
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-07-30
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class VacancyData {
    private final String title;
    private final String url;
    private final String description;
    private final ZonedDateTime dateTime;

    @Override
    public String toString() {
        return title + ": " + dateTime;
    }
}
