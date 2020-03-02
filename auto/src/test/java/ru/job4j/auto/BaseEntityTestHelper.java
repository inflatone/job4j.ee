package ru.job4j.auto;

import ru.job4j.auto.model.BaseEntity;

public abstract class BaseEntityTestHelper<T extends BaseEntity> extends EntityTestHelper<T> {
    public BaseEntityTestHelper(boolean checkRecursively, String... fieldsToIgnore) {
        super(checkRecursively, fieldsToIgnore);
    }
}
