package ru.job4j.auto.inject;

import java.util.Map;

public class BasicRepositoryModule extends AbstractRepositoryModule {
    @Override
    protected Map<?, ?> persistProperties() {
        return Map.of("hibernate.hbm2ddl.import_files", "db/populate_basic.sql");
    }
}
