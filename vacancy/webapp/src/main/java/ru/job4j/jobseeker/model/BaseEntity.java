package ru.job4j.jobseeker.model;

import lombok.*;

/**
 * Base abstract model class to persist entities with ID
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-01-16
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public abstract class BaseEntity {
    private Integer id;

    public boolean isNew() {
        return id == null;
    }
}