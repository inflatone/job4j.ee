package ru.job4j.jobseeker.model;

import lombok.*;

import java.util.Date;
import java.util.Set;

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
    private User user;

    public Task(Task task) {
        this(task.keyword, task.city, task.limit, task.active, task.launch, task.rule, task.source, task.user);
        setId(task.getId());
    }
}
