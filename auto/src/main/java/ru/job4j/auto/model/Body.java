package ru.job4j.auto.model;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "body", uniqueConstraints = {@UniqueConstraint(columnNames = "type", name = "body_unique_type_idx")})
@NoArgsConstructor
public class Body extends BaseTypedEntity {
    public Body(Integer id, String name) {
        super(id, name);
    }
}