package ru.job4j.ee.store.repository;

import one.util.streamex.StreamEx;
import ru.job4j.ee.store.model.BaseNamedEntity;
import ru.job4j.ee.store.model.City;
import ru.job4j.ee.store.model.Country;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import static java.util.function.Function.identity;

/**
 * Represents city in-memory storage
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-17
 */
public class InMemoryCityRepository extends InMemoryStorage<City> implements CityRepository {
    private Map<String, Country> countryStorage = new ConcurrentSkipListMap<>();

    @Override
    public boolean save(City city) {
        if (city.isNew()) {
            var country = countryStorage.get(city.getCountry().getName());
            if (country != null) {
                city.setId(SEQ.incrementAndGet());
                storage.put(city.getId(), new City(city.getId(), city.getName(), country));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        return storage.remove(id) != null;
    }

    @Override
    public City find(int id) {
        var city = storage.get(id);
        return city == null ? null : new City(city);
    }

    @Override
    public List<City> findByCountryId(int id) {
        return StreamEx.of(storage.values())
                .filterBy(c -> c.getCountry().getId(), id)
                .map(City::new)
                .sortedBy(City::getName)
                .toList();
    }

    @Override
    public int clear() {
        Map<String, Country> buffer = StreamEx.of(storage.values())
                .map(City::getCountry)
                .toMap(BaseNamedEntity::getName, identity(), (one, two) -> one);
        int result = countryStorage.size() - buffer.size();
        countryStorage.clear();
        countryStorage.putAll(buffer);
        return result;
    }

    @Override
    public List<Country> saveAllCountries(List<Country> countries) {
        return StreamEx.of(countries).filter(
                c -> {
                    int id = SEQ.incrementAndGet();
                    c.setId(id);
                    return countryStorage.put(c.getName(), new Country(c)) == null; // new one
                }).toList();
    }

    @Override
    public List<Country> findAllCountries() {
        return List.copyOf(countryStorage.values());
    }
}