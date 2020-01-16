package ru.job4j.jobseeker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

/**
 * Task launch log entity
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-16
 */
@Getter
@AllArgsConstructor
public class LaunchLog extends BaseEntity {
    private final Date dateTime;

    private final Status status;

    private final int foundAmount;

    private final int addedAmount;

    private final Task task;

    public enum Status {
        OK,
        FAIL
    }
}
