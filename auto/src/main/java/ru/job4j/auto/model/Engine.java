package ru.job4j.auto.model;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "engine", uniqueConstraints = {@UniqueConstraint(columnNames = "type", name = "engine_unique_type_idx")})
@NoArgsConstructor
public class Engine extends BaseTypedEntity {
    public Engine(Integer id, String type) {
        super(id, type);
    }
}
