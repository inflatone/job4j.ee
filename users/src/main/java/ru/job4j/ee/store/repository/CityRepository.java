package ru.job4j.ee.store.repository;

import ru.job4j.ee.store.model.City;
import ru.job4j.ee.store.model.Country;

import java.util.List;

/**
 * Represents interface to implement city data storing classes
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-13
 */
public interface CityRepository extends Repository<City> {

    /**
     * Finds the entities associated with the given country in the store
     *
     * @param id country id
     * @return found entities
     */
    List<City> findByCountryId(int id);

    @Override
    default List<City> findAll() { // senselessly
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * Stores the given entities to DB
     *
     * @param countries countries to save
     * @return saved countries
     */
    List<Country> saveAllCountries(List<Country> countries);

    /**
     * Finds all the countries stored in DB
     *
     * @return countries
     */
    List<Country> findAllCountries();
}
