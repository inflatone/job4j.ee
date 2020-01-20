package ru.job4j.jobseeker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;
import ru.job4j.vacancy.model.VacancyData;

import java.time.ZonedDateTime;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

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
@AllArgsConstructor
public class Vacancy extends BaseEntity {
    private VacancyData data;

    private boolean highlighted;

    @JsonProperty(access = WRITE_ONLY)
    private Task task;

    public Vacancy(Vacancy vacancy) {
        this(vacancy.getId(), vacancy.data.getTitle(), vacancy.data.getUrl(), vacancy.data.getDescription(),
                vacancy.data.getDateTime(), vacancy.highlighted, vacancy.task.getId());
    }

    @JdbiConstructor
    public Vacancy(@ColumnName("id") Integer id,
                   @ColumnName("title") String title,
                   @ColumnName("url") String url,
                   @ColumnName("description") String description,
                   @ColumnName("date") ZonedDateTime dateTime,
                   @ColumnName("highlighted") boolean highlighted,
                   @ColumnName("task_id") Integer taskId) {
        this(new VacancyData(title, url, description, dateTime), highlighted, new Task(taskId));
        setId(id);
    }
}