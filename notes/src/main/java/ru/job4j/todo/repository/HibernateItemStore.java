package ru.job4j.todo.repository;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.job4j.todo.model.Item;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.function.Function;

public class HibernateItemStore implements ItemStore {
    private static final ItemStore INSTANCE_HOLDER = new HibernateItemStore();

    public static ItemStore getStore() {
        return INSTANCE_HOLDER;
    }

    private final SessionFactory sessionFactory;

    private HibernateItemStore() {
        this.sessionFactory = new Configuration()
                .configure()
                .buildSessionFactory();
    }

    @Override
    public Item save(Item item) {
        if (!item.isNew()) {
            return transactionalExecute(session -> session.merge(item));
        } else {
            return transactionalExecute(session -> {
                session.persist(item);
                return item;
            });
        }
    }

    @Override
    public boolean delete(int id) {
        return transactionalExecute(session -> session.createQuery("DELETE FROM Item i WHERE i.id=:id")
                .setParameter("id", id)
                .executeUpdate()) != 0;
    }

    @Override
    public Item find(int id) {
        return transactionalExecute(session -> session.find(Item.class, id));
    }

    @Override
    public List<Item> findAll() {
        return transactionalExecute(session -> session.createQuery("FROM Item i ORDER BY i.created DESC ", Item.class)
                .getResultList());
    }

    @Override
    public List<Item> findUncompleted() {
        return transactionalExecute(session -> session.createQuery("FROM Item i WHERE i.done=false ORDER BY i.created DESC ", Item.class)
                .getResultList());
    }


    @Override
    public boolean complete(int id, boolean complete) {
        return transactionalExecute(session -> {
            var item = session.find(Item.class, id);
            if (item == null) {
                return false;
            } else {
                item.setDone(complete);
                return true;
            }
        });
    }

    private <T> T transactionalExecute(Function<EntityManager, T> command) {
        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();
            try {
                var result = command.apply(session);
                transaction.commit();
                return result;
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }
}
