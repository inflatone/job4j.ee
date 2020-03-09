package ru.job4j.ee.store.service.inmemory;

import ru.job4j.ee.store.service.UserServiceTest;
import ru.job4j.ee.store.web.inject.InMemoryRepositoryModule;

class InMemoryUserServiceTest extends UserServiceTest {
    InMemoryUserServiceTest() {
        super(new InMemoryRepositoryModule());
    }
}
