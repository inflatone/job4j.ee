package ru.job4j.auto.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Body extends BaseTypedEntity {
    public Body(Integer id, String name) {
        super(id, name);
    }
}