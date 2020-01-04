package ru.job4j.ee.store.repository.dbi;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMappers;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import ru.job4j.ee.store.model.City;
import ru.job4j.ee.store.model.Country;

import java.util.List;

/**
 * City DAO shell
 * Contains methods to complete CRUD operations with {@link City} objects
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-13
 */
@RegisterConstructorMappers({@RegisterConstructorMapper(City.class), @RegisterConstructorMapper(Country.class)})
public interface CityDao {
    String SELECT_ALL = "SELECT c.*, cc.name country_name FROM city c LEFT OUTER JOIN country cc on c.country_id = cc.id";

    default int insertAndReturnId(City city) {
        return insertAndReturnId(city.getName(), city.getCountry().getId());
    }

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO city (name, country_id) VALUES (:name, :countryId)")
    int insertAndReturnId(@Bind("name") String name, @Bind("countryId") int countryId);

    @SqlUpdate("DELETE FROM city WHERE id=:id")
    int delete(@Bind(value = "id") int id);

    @SqlQuery(SELECT_ALL + " WHERE c.id=:id")
    City find(@Bind(value = "id") int id);

    @SqlQuery(SELECT_ALL + " WHERE c.country_id=:countryId ORDER BY name")
    List<City> findByCountryId(@Bind(value = "countryId") int id);


    @SqlBatch("INSERT INTO country (name) VALUES (:name) ON CONFLICT (name) DO NOTHING")
    int[] insertCountriesAndReturnStatuses(@BindBean List<Country> countries);

    @SqlUpdate("DELETE FROM country cc WHERE cc.id NOT IN (SELECT c.country_id FROM city c)")
    int clearUnusedCountries();

    @SqlQuery("SELECT * FROM country ORDER BY name")
    List<Country> findAllCountries();
}