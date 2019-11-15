package ru.job4j.auto.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.persist.jpa.JpaPersistModule;
import ru.job4j.auto.repository.*;
import ru.job4j.auto.repository.env.JpaManager;
import ru.job4j.auto.repository.env.TransactionManager;

import java.util.Map;

public abstract class AbstractRepositoryModule extends AbstractModule {
    public static final String TEST_UNIT_NAME = "autoTest";

    @Override
    protected void configure() {
        install(new JpaPersistModule(TEST_UNIT_NAME)
                .properties(persistProperties()));

        bind(BodyRepository.class).in(Scopes.SINGLETON);
        bind(EngineRepository.class).in(Scopes.SINGLETON);
        bind(TransmissionRepository.class).in(Scopes.SINGLETON);

        bind(JpaManager.class).in(Scopes.SINGLETON);

        bind(CarRepository.class).in(Scopes.SINGLETON);
        bind(UserRepository.class).in(Scopes.SINGLETON);

        bind(TransactionManager.class).in(Scopes.SINGLETON);
    }

    protected abstract Map<?, ?> persistProperties();
}