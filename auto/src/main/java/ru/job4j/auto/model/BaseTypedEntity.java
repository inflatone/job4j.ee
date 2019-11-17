package ru.job4j.auto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@BatchSize(size = 10)
@NoArgsConstructor
public abstract class BaseTypedEntity extends BaseEntity {
    @Column(name = "type", nullable = false)
    @Getter
    @Setter
    protected String type;

    protected BaseTypedEntity(Integer id, String type) {
        super(id);
        this.type = type;
    }
}
