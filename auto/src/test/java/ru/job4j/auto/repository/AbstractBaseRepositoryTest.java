package ru.job4j.auto.repository;

import com.google.inject.persist.PersistService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.job4j.auto.repository.env.TransactionManager;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import java.io.IOException;

@Slf4j
@AllArgsConstructor
@ExtendWith(GuiceExtension.class) // https://github.com/JeffreyFalgout/junit5-extensions/tree/master/guice-extension
abstract class AbstractBaseRepositoryTest {
    @BeforeAll
    public static void init(PersistService persistService, TransactionManager tm) throws IOException {
        tm.init();
        persistService.start();
    }

    @BeforeEach
    public void start(TransactionManager tm) throws SystemException, NotSupportedException {
        log.info("TransactionManager: start test transaction");
        tm.getUserTransaction().begin();
    }

    @AfterEach
    public void finish(TransactionManager tm) {
        tm.rollback();
        log.info("TransactionManager: finish and rollback test transaction");
    }

    @AfterAll
    public static void drop(PersistService persistService, TransactionManager tm) {
        persistService.stop();
        tm.stop();
    }
}
