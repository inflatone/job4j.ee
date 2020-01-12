package ru.job4j.ee.store.service.inmemory;

import ru.job4j.ee.store.service.CityServiceTest;
import ru.job4j.ee.store.web.inject.InMemoryRepositoryModule;

class InMemoryCityServiceTest extends CityServiceTest {
    InMemoryCityServiceTest() {
        super(new InMemoryRepositoryModule());
    }
}
