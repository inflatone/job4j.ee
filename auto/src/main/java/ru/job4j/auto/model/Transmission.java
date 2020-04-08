package ru.job4j.auto.model;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@BatchSize(size = 10)
@Entity
@Table(name = "transmission", uniqueConstraints = {@UniqueConstraint(columnNames = "type", name = "transmission_unique_type_idx")})
@NoArgsConstructor
public class Transmission extends BaseTypedEntity {
    public Transmission(Integer id, String type) {
        super(id, type);
    }
}
