package ru.job4j.ee.store.model;

import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

/**
 * Model to transfer country data layer-to-layer
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-13
 */
public class Country extends BaseNamedEntity {
    public Country() {
    }

    public Country(Country country) {
        this(country.getId(), country.getName());
    }

    public Country(Integer id) {
        super(id);
    }

    public Country(String name) {
        super(null, name);
    }

    @JdbiConstructor
    public Country(@ColumnName("id") Integer id, @ColumnName("name") String name) {
        super(id, name);
    }

    @Override
    public String toString() {
        return "Country{"
                + "id=" + getId()
                + "name='" + getName() + '\''
                + '}';
    }
}
