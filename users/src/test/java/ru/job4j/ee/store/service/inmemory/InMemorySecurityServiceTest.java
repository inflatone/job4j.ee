package ru.job4j.ee.store.service.inmemory;

import ru.job4j.ee.store.service.SecurityServiceTest;
import ru.job4j.ee.store.web.inject.InMemoryRepositoryModule;

class InMemorySecurityServiceTest extends SecurityServiceTest {
    InMemorySecurityServiceTest() {
        super(new InMemoryRepositoryModule());
    }
}
