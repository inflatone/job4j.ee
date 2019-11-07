package ru.job4j.ee.store.web;

import com.google.common.base.Strings;
import one.util.streamex.StreamEx;

import java.util.Map;

import static java.util.function.Function.identity;

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
    empty;

    private static final Map<String, Action> ACTION_MAPPER = StreamEx.of(values()).toMap(Enum::name, identity());


    /**
     * Represents 'customized' version of default enum method valueOf(String name)
     * Returns default action if the given name is null or empty
     *
     * @param action action by name
     * @return enum instance with the given name
     */
    public static Action defineAction(String action) {
        return Strings.isNullOrEmpty(action) ? empty : ACTION_MAPPER.get(action);
    }
}
