package ru.job4j.vacancy.model;

import lombok.AllArgsConstructor;

/**
 * Available scan source title enumeration
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-14
 */
@AllArgsConstructor
public enum SourceTitle {
    hh_ru("hh.ru"),
    habr_com("habr.com"),
    sql_ru("sql.ru");

    private final String code;

    public String title() {
        return code;
    }
}
