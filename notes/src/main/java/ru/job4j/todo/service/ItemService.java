package ru.job4j.todo.service;

import ru.job4j.todo.model.Item;
import ru.job4j.todo.repository.ItemStore;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static ru.job4j.todo.repository.HibernateItemStore.getStore;
import static ru.job4j.todo.util.ValidationUtil.checkNotFoundWithId;

public class ItemService {
    private static final ItemService INSTANCE_HOLDER = new ItemService();

    public static ItemService getService() {
        return INSTANCE_HOLDER;
    }

    private final ItemStore store = getStore();

    private ItemService() {

    }

    public Item create(Item item) {
        requireNonNull(item, "item must not be null");
        return store.save(item);
    }

    public void update(Item item) {
        requireNonNull(item, "user must not be null");
        checkNotFoundWithId(store.save(item), item.getId());
    }

    public Item find(int id) {
        return checkNotFoundWithId(store.find(id), id);
    }

    public List<Item> findAll(boolean showAll) {
        return showAll ? store.findAll() : store.findUncompleted();
    }

    public void done(int id, boolean done) {
        checkNotFoundWithId(store.complete(id, done), id);
    }

    public void delete(int id) {
        checkNotFoundWithId(store.delete(id), id);
    }
}
