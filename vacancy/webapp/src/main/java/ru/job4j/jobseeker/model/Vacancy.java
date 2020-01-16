package ru.job4j.jobseeker.model;

import lombok.*;
import ru.job4j.vacancy.model.VacancyData;

/**
 * Vacancy model
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-16
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Vacancy extends BaseEntity {
    private VacancyData data;

    private boolean highlighted;

    private Task task;
}