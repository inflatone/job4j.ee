package ru.job4j.auto.repository.env;

import com.google.inject.persist.UnitOfWork;
import org.hibernate.Session;
import ru.job4j.auto.util.ExceptionHandler.ConsumerEx;
import ru.job4j.auto.util.ExceptionHandler.FunctionEx;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import java.sql.Connection;
import java.util.function.Consumer;
import java.util.function.Function;

public class JpaManager {
    private final TransactionManager tm;

    private final Provider<EntityManager> em;

    private final UnitOfWork unit;

    @Inject
    public JpaManager(TransactionManager tm, UnitOfWork unit, Provider<EntityManager> em) {
        this.tm = tm;
        this.em = em;
        this.unit = unit;
    }

    public void transactionalExecute(Consumer<EntityManager> command) {
        transactionalRetrieve(m -> {
            command.accept(m);
            return null;
        });
    }

    public <R> R transactionalRetrieve(Function<EntityManager, R> command) {
        try {
            UserTransaction tx = tm.lookupUserTransaction();
            if (tx.getStatus() == Status.STATUS_ACTIVE) {
                return command.apply(em.get());
            }
            tx.begin();
            var result = command.apply(em.get());
            tx.commit();
            unit.end(); // force close entity manager by Guice
            return result;
        } catch (Exception e) {
            tm.rollback();
            throw new RuntimeException(e);
        }
    }

    public <R> R sessionRetrieve(FunctionEx<Session, R> command) {
        return transactionalRetrieve(m -> command.apply(m.unwrap(Session.class)));
    }

    public void sessionExecute(ConsumerEx<Session> command) {
        sessionRetrieve(s -> {
            command.accept(s);
            return null;
        });
    }

    public <R> R connectionRetrieve(FunctionEx<Connection, R> command) {
        return sessionRetrieve(s -> s.doReturningWork(command::apply));
    }

    public void connectionExecute(ConsumerEx<Connection> command) {
        sessionExecute(s -> s.doWork(command::accept));
    }
}
