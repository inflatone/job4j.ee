package ru.job4j.auto.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Transmission extends BaseTypedEntity {
    public Transmission(Integer id, String type) {
        super(id, type);
    }
}
