package ru.job4j.auto.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Engine extends BaseTypedEntity {
    public Engine(Integer id, String type) {
        super(id, type);
    }
}
