package ru.job4j.ee.store.service.jdbi;

import ru.job4j.ee.store.service.CityServiceTest;
import ru.job4j.ee.store.web.inject.JdbiRepositoryModule;

class JdbiCityServiceTest extends CityServiceTest implements JdbiConnectable {
    JdbiCityServiceTest() {
        super(new JdbiRepositoryModule());
    }
}
