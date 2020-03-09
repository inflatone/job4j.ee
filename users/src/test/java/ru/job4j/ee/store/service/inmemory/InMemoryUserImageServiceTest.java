package ru.job4j.ee.store.service.inmemory;

import ru.job4j.ee.store.service.UserImageServiceTest;
import ru.job4j.ee.store.web.inject.InMemoryRepositoryModule;

class InMemoryUserImageServiceTest extends UserImageServiceTest {
    InMemoryUserImageServiceTest() {
        super(new InMemoryRepositoryModule());
    }
}
