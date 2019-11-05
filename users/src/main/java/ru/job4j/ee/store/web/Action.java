package ru.job4j.ee.store.web;

import com.google.common.base.Strings;

/**
 * Represents supported request actions enumeration
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-05
 */
public enum Action {
    create,
    update,
    delete,
    all;

    /**
     * Represents 'customized' version of default enum method valueOf(String name)
     * Returns default action if the given name is null or empty
     *
     * @param action action by name
     * @return enum instance with the given name
     */
    public static Action defineAction(String action) {
        return Strings.isNullOrEmpty(action) ? all : Action.valueOf(action);
    }
}
