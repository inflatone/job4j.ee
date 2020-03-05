package ru.job4j.auto;

import com.fasterxml.jackson.core.type.TypeReference;
import ru.job4j.auto.model.BaseEntity;
import ru.job4j.auto.web.converter.JsonHelper;

import java.util.Map;
import java.util.function.Function;

public abstract class BaseEntityTestHelper<T extends BaseEntity> extends EntityTestHelper<T, Integer> {
    public BaseEntityTestHelper(JsonHelper jsonHelper, Class<T> clazz, TypeReference<Map<Integer, T>> typeReference, boolean checkRecursively, String... fieldsToIgnore) {
        super(jsonHelper, clazz, typeReference, checkRecursively, fieldsToIgnore);
    }

    @Override
    protected Function<T, Integer> idMapper() {
        return BaseEntity::getId;
    }
}
