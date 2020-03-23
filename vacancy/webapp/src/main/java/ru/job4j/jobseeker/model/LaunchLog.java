package ru.job4j.jobseeker.model;

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
public class LaunchLog extends BaseEntity {
    private final Date dateTime;

    private final Status status;

    private final int foundAmount;

    private final int addedAmount;

    private final Task task;

    private LaunchLog(Integer id, Date dateTime, int foundAmount, int skippedAmount, Task task, Status status) {
        super(id);
        this.dateTime = dateTime;
        this.foundAmount = foundAmount;
        this.addedAmount = foundAmount - skippedAmount;
        this.task = task;
        this.status = status;
    }

    public static LaunchLog success(int foundAmount, int duplicatedAmount, Task task) {
        return new LaunchLog(null, new Date(), foundAmount, duplicatedAmount, task, Status.OK);
    }

    public static LaunchLog fail(Task task) {
        return new LaunchLog(null, new Date(), 0, 0, task, Status.FAIL);
    }

    public enum Status {
        OK,
        FAIL
    }
}
