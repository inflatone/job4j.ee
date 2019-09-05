package ru.job4j.todo.repository;

import ru.job4j.todo.model.Item;

import java.util.List;

public interface ItemStore {
    Item save(Item item);

    boolean delete(int id);

    Item find(int id);

    List<Item> findAll();

    List<Item> findUncompleted();

    boolean complete(int id, boolean complete);
}
