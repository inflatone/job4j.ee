package ru.job4j.jobseeker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.jdbi.v3.core.mapper.Nested;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

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
    private int amount;

    @JsonProperty(access = WRITE_ONLY)
    private User user;

    public Task(Integer id) {
        super(id);
    }

    public Task(Task task) {
        this(task.keyword, task.city, task.limit, task.active, task.launch, task.rule, task.source, task.amount, task.user);
        setId(task.getId());
    }

    @JdbiConstructor
    public Task(@ColumnName("id") Integer id,
                @ColumnName("keyword") String keyword,
                @ColumnName("city") String city,
                @ColumnName("scan_limit") Date limit,
                @ColumnName("active") boolean active,
                @ColumnName("next_launch") Date launch,
                @ColumnName("repeat_rule") RepeatRule rule,
                @Nested("scan_source_") ScanSource source,
                @ColumnName("amount") int amount,
                @ColumnName("user_id") Integer userId) {
        this(keyword, city, limit, active, launch, rule, source, amount, new User(userId));
        setId(id);
    }

    @JsonProperty
    public Integer getRuleOrdinal() {
        return rule.ordinal();
    }
}
