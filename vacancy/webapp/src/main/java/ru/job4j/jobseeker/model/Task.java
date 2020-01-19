package ru.job4j.jobseeker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

/**
 * Task model
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
public class Task extends BaseEntity {
    private String keyword;
    private String city;
    private Date limit;

    private boolean active;
    private Date launch;
    private RepeatRule rule;

    private ScanSource source;

    @JsonProperty(access = WRITE_ONLY)
    private User user;

    public Task(Task task) {
        this(task.keyword, task.city, task.limit, task.active, task.launch, task.rule, task.source, task.user);
        setId(task.getId());
    }

    public Task(Integer id, String keyword, String city, Date limit, Date launch, RepeatRule rule, ScanSource source, User user) {
        this(keyword, city, limit, true, launch, rule, source, user);
        setId(id);
    }

    @JsonProperty
    public Integer getRuleOrdinal() {
        return rule.ordinal();
    }
}
