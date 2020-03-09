package ru.job4j.ee.store.service;

import com.google.inject.Inject;
import com.google.inject.Module;
import org.junit.jupiter.api.Test;
import ru.job4j.ee.store.model.City;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static ru.job4j.ee.store.AssertionUtil.*;

public abstract class CityServiceTest extends AbstractServiceTest {
    public CityServiceTest(Module repositoryModule) {
        super(repositoryModule);
    }

    @Inject
    private CityService cityService;

    @Test
    void findByCountry() {
        var ruCities = cityService.findByCountryId(RUSSIA.getId());
        assertMatch(ruCities, MOSCOW, SAINT_PETERSBURG);

        var ukCities = cityService.findByCountryId(UKRAINE.getId());
        assertMatch(ukCities, KIEV);
    }

    @Test
    void findAll() {
        assertThrows(UnsupportedOperationException.class, cityRepository::findAll);
    }


    @Test
    void findByCountryEmpty() {
        var notExistedCountryCities = cityService.findByCountryId(0);
        assertTrue(notExistedCountryCities.isEmpty());
    }

    @Test
    void save() {
        var city = new City(NEW_CITY);
        cityRepository.save(city);
        assertMatch(cityRepository.find(city.getId()), city);
    }

    @Test
    void delete() {
        var city = new City(NEW_CITY);
        cityRepository.save(city); // need new city with no bound with any users

        assertTrue(cityRepository.delete(city.getId()));
        assertTrue(cityService.findByCountryId(USA.getId()).isEmpty());
        assertFalse(cityRepository.delete(city.getId()));
    }
}
