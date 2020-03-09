package ru.job4j.ee.store.service.jdbi;

import ru.job4j.ee.store.service.UserImageServiceTest;
import ru.job4j.ee.store.web.inject.JdbiRepositoryModule;

class JdbiUserImageServiceTest extends UserImageServiceTest implements JdbiConnectable {
    JdbiUserImageServiceTest() {
        super(new JdbiRepositoryModule());
    }
}
