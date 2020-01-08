package ru.job4j.ee.store.repository;

import com.google.inject.Inject;
import ru.job4j.ee.store.model.City;
import ru.job4j.ee.store.model.Country;
import ru.job4j.ee.store.repository.dbi.CityDao;

import java.util.List;

/**
 * Represents city DB storage accessor
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-13
 */
public class JdbiCityRepository implements CityRepository {
    @Inject
    private CityDao dao;

    @Override
    public boolean save(City city) {
        if (city.isNew()) {
            city.setId(dao.insertAndReturnId(city));
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        return dao.delete(id) != 0;
    }

    @Override
    public City find(int id) {
        return dao.find(id);
    }

    @Override
    public List<City> findByCountryId(int id) {
        return dao.findByCountryId(id);
    }

    @Override
    public int clear() {
        return dao.clearUnusedCountries();
    }

    @Override
    public List<Country> saveAllCountries(List<Country> countries) {
        int[] countryId = dao.insertCountriesAndReturnStatuses(countries);
        for (int i = countryId.length - 1; i >= 0; i--) {
            if (countryId[i] == 0) {
                countries.remove(i);
            }
        }
        return countries;
    }

    @Override
    public List<Country> findAllCountries() {
        return dao.findAllCountries();
    }
}