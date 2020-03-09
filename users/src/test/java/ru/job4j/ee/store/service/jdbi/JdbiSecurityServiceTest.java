package ru.job4j.ee.store.service.jdbi;

import ru.job4j.ee.store.service.SecurityServiceTest;
import ru.job4j.ee.store.web.inject.JdbiRepositoryModule;

class JdbiSecurityServiceTest extends SecurityServiceTest implements JdbiConnectable {
    JdbiSecurityServiceTest() {
        super(new JdbiRepositoryModule());
    }
}
