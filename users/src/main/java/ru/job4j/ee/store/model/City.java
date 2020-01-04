package ru.job4j.ee.store.model;

import org.jdbi.v3.core.mapper.Nested;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

/**
 * Model to transfer city data layer-to-layer
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-13
 */
public class City extends BaseNamedEntity {
    private Country country;

    public City() {
    }

    public City(City city) {
        this(city.getId(), city.getName(), city.country);
    }

    @JdbiConstructor
    public City(@ColumnName("id") Integer id, @ColumnName("name") String name, @Nested("country_") Country country) {
        super(id, name);
        this.country = country;
    }

    public City(String name, Country country) {
        this(null, name, country);
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "City{"
                + "id=" + getId()
                + ", name='" + getName() + '\''
                + ", country='" + country + '\''
                + '}';
    }
}