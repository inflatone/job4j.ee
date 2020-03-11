package ru.job4j.vacancy.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Vacancy model class
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-07-30
 */
public class VacancyData {
    private final String title;
    private final String url;
    private final String description;
    private final LocalDateTime dateTime;

    public VacancyData(String title, String url, String description, LocalDateTime dateTime) {
        this.title = title;
        this.url = url;
        this.description = description;
        this.dateTime = dateTime;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VacancyData vacancy = (VacancyData) o;
        return title.equals(vacancy.title)
                && url.equals(vacancy.url)
                && description.equals(vacancy.description)
                && dateTime.equals(vacancy.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, url, description, dateTime);
    }

    @Override
    public String toString() {
        return title + ":" + dateTime + ":" + url + ":  " + description;
    }
}
