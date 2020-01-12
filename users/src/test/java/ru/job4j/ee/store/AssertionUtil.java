package ru.job4j.ee.store;

import com.google.common.base.Strings;
import one.util.streamex.StreamEx;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import ru.job4j.ee.store.model.*;
import ru.job4j.ee.store.repository.CityRepository;
import ru.job4j.ee.store.repository.UserRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.job4j.ee.store.web.auth.AuthUtil.getRootCause;
import static ru.job4j.ee.store.web.json.JsonUtil.asJsonAdditional;

public class AssertionUtil {
    public static final Country RUSSIA;
    public static final Country UKRAINE;
    public static final Country USA;

    public static final City MOSCOW;
    public static final City SAINT_PETERSBURG;
    public static final City KIEV;
    public static final City NEW_CITY;

    public static final User USER;
    public static final User ADMIN;
    public static final User UNREGISTERED;
    public static final User BANNED;

    public static final byte[] TEST_BYTES = "ImAgE_ByTeS_000".getBytes();

    static {
        // all ends with '.' to prevent duplicate insert error on real DB
        RUSSIA = new Country("Russian Federation.");
        UKRAINE = new Country("Ukraine.");
        USA = new Country("USA.");

        MOSCOW = new City("Moscow", RUSSIA);
        SAINT_PETERSBURG = new City("Saint Petersburg", RUSSIA);
        KIEV = new City("Kiev", UKRAINE);
        NEW_CITY = new City("NYC", USA);

        ADMIN = createUserModel("admin.", "supervisor", Role.ADMIN, SAINT_PETERSBURG);
        USER = createUserModel("user.", "User", Role.USER, KIEV);
        UNREGISTERED = createUserModel("unregistered.", "password", Role.USER, MOSCOW);

        BANNED = createUserModel("banned.", "banned", Role.USER, MOSCOW);
        BANNED.setEnabled(false);
    }

    public static void fillData(UserRepository userRepository, CityRepository cityRepository) {
        List<Country> countries = List.of(RUSSIA, UKRAINE, USA);
        List<Country> saved = cityRepository.saveAllCountries(countries);

        assertEquals(countries.size(), saved.size());

        Map<String, Country> persisted = StreamEx.of(cityRepository.findAllCountries()).toMap(BaseNamedEntity::getName, identity());
        countries.forEach(c -> c.setId(persisted.get(c.getName()).getId()));

        cityRepository.save(MOSCOW);
        cityRepository.save(SAINT_PETERSBURG);
        cityRepository.save(KIEV);

        userRepository.save(USER);
        userRepository.save(ADMIN);
        userRepository.save(BANNED);
    }

    public static void cleanData() {
        // to be stored again as new
        MOSCOW.setId(null);
        SAINT_PETERSBURG.setId(null);
        KIEV.setId(null);

        USER.setId(null);
        ADMIN.setId(null);
        BANNED.setId(null);
    }

    public static String asJsonWithPassword(User user) {
        return asJsonAdditional(user, "password", user.getPassword());
    }

    public static User createUserModel(String login, String password, Role role, City city) {
        var name = Strings.isNullOrEmpty(login) ? login : login.substring(0, 1).toUpperCase() + login.substring(1);
        return createUserModel(name, login, password, role, city);
    }

    private static User createUserModel(String name, String login, String password, Role role, City city) {
        return new User(null, name, login, password, new Date(), role, true, city, new UserImage(0));
    }

    public static UserImage createUserImageModel() {
        var data = Mockito.spy(new ByteArrayInputStream(TEST_BYTES));
        return new UserImage("name", "type", TEST_BYTES.length, data);
    }

    public static void assertMatch(User actual, User expected) {
        assertThat(actual).isEqualToIgnoringGivenFields(expected, "id", "created", "image", "city");
        assertThat(actual.getCity()).isEqualToIgnoringGivenFields(expected.getCity(), "id");
    }

    /**
     * Matches models comparing them field-by-field, provides closing actual model' input stream (to prevent memory leaks)
     */
    public static void assertMatch(UserImage actual, UserImage expected) throws IOException {
        assertThat(actual).isEqualToIgnoringGivenFields(expected, "id", "oid", "data");
        try (var in = actual.getData()) {
            byte[] actualBytes = in.readAllBytes();
            assertArrayEquals(TEST_BYTES, actualBytes);
        }
    }

    public static void assertMatch(Iterable<City> actual, City... expected) {
        assertThat(actual).usingFieldByFieldElementComparator().isEqualTo(List.of(expected));
    }

    public static void assertMatch(City actual, City expected) {
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    public static void assertThrows(Class<? extends Throwable> expectedType, String expectedMessage, Executable task) {
        var thrown = Assertions.assertThrows(expectedType, () -> {
            try {
                task.execute();
            } catch (Throwable t) {
                throw getRootCause(t);
            }
        });
        if (expectedMessage != null) {
            assertEquals(expectedMessage, thrown.getMessage());
        }
    }

    public static void assertThrows(Class<? extends Throwable> expectedType, Executable executable) {
        assertThrows(expectedType, null, executable);
    }
}
