package ru.job4j.auto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public abstract class BaseTypedEntity extends BaseEntity {
    @Getter
    @Setter
    protected String type;

    protected BaseTypedEntity(Integer id, String type) {
        super(id);
        this.type = type;
    }
}
