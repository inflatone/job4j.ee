package ru.job4j.ee.store.service;

import one.util.streamex.StreamEx;
import ru.job4j.ee.store.model.City;
import ru.job4j.ee.store.model.Country;
import ru.job4j.ee.store.repository.CityRepository;
import ru.job4j.ee.store.web.AjaxServlet.CountryTo;

import java.util.List;

import static ru.job4j.ee.store.repository.JdbiCityRepository.getCityRepository;

/**
 * Represents service layer of the app (validates the given city data from the store, then transfer them to the web)
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-13
 */
public class CityService {
    private static final CityService INSTANCE_HOLDER = new CityService();

    public static CityService getCityService() {
        return INSTANCE_HOLDER;
    }

    private final CityRepository repository = getCityRepository();

    /**
     * Asks the store to find the entities associated with the given country
     *
     * @param id country id
     */
    public List<City> findByCountryId(int id) {
        return repository.findByCountryId(id);
    }

    /**
     * Asks the store to find all the countries stored in DB
     *
     * @return country list ordered by name
     */
    public List<Country> findAllCountries() {
        return repository.findAllCountries();
    }

    /**
     * Asks the store to save all the given countries
     *
     * @param countriesTo countries to save
     */
    public List<Country> saveAllCountries(List<CountryTo> countriesTo) {
        var countries = StreamEx.of(countriesTo).map(CountryTo::fromTo).toList();
        return repository.saveAllCountries(countries);
    }

    /**
     * Asks the store to clear all the unused countries
     *
     * @return deleted countries amount
     */
    public int clearCountries() {
        return repository.clear();
    }
}