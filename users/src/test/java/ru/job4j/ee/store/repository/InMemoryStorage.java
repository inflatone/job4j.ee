package ru.job4j.ee.store.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

abstract class InMemoryStorage<T> {
    static final AtomicInteger SEQ = new AtomicInteger(0);

    final Map<Integer, T> storage = new ConcurrentHashMap<>();
}
