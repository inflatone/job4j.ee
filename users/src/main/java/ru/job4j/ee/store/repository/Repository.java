package ru.job4j.ee.store.repository;

import java.util.List;

/**
 * Represents interface to implement data storing helper classes
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-05
 */
public interface Repository<T> {
    /**
     * Creates (id == null) and updates (id != null) entity data in the store
     *
     * @param entry entity
     * @return entity (with inserted id in CREATE case), or null (if not found in UPDATE case)
     */
    T save(T entry);

    /**
     * Deletes the entity associated with the given id from the store
     *
     * @param id id
     * @return true if successful
     */
    boolean delete(int id);

    /**
     * Finds the entity associated with the given id in the store
     *
     * @param id id
     * @return found entity, or null if not found
     */
    T find(int id);

    /**
     * Returns all the stored entities (empty list
     *
     * @return list of all the stored entities (empty list if not present any)
     */
    List<T> findAll();
}
