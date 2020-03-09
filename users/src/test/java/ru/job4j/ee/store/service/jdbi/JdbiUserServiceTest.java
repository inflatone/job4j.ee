package ru.job4j.ee.store.service.jdbi;

import ru.job4j.ee.store.service.UserServiceTest;
import ru.job4j.ee.store.web.inject.JdbiRepositoryModule;

class JdbiUserServiceTest extends UserServiceTest implements JdbiConnectable {
    JdbiUserServiceTest() {
        super(new JdbiRepositoryModule());
    }
}
