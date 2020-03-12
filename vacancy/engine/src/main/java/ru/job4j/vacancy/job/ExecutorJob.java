package ru.job4j.vacancy.job;

import org.quartz.Job;

import java.util.List;

/**
 * Main interface to implement job classes supported by the job executor
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-15
 */
public interface ExecutorJob extends Job {

    /**
     * Returns the list of the property keys that required to execute this job
     * Must be set to retrieve and validate (if not found) it by executor from the given set of params
     *
     * @return property keys
     */
    List<String> getRequiredKeys();

    /**
     * Returns the list of the property keys that may user additionally to execute this job
     * Must be set to retrieve (if found) it by executor from the given set of params
     *
     * @return property keys
     */
    List<String> getAdditionalKeys();

}
