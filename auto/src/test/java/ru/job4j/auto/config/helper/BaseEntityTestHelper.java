package ru.job4j.auto.config.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.test.web.servlet.MvcResult;
import ru.job4j.auto.config.helper.BaseDetailsTestHelperFacade.JsonMappableTestHelper;
import ru.job4j.auto.model.BaseEntity;
import ru.job4j.auto.model.Role;
import ru.job4j.auto.web.converter.JsonHelper;

import java.util.function.Function;

import static ru.job4j.auto.web.AbstractControllerTest.getCreatedResourceId;

public abstract class BaseEntityTestHelper<T extends BaseEntity> extends AbstractEntityTestHelper<T> {
    public BaseEntityTestHelper(JsonHelper jsonHelper, Class<T> clazz, boolean checkRecursively, String... fieldsToIgnore) {
        super(jsonHelper, clazz, checkRecursively, fieldsToIgnore);
    }

    public static class RoleEntityTestHelper extends JsonMappableTestHelper<String, Role> {
        public RoleEntityTestHelper(JsonHelper jsonHelper) {
            super(jsonHelper, Role.class, new TypeReference<>() {}, false);
        }

        @Override
        protected Function<Role, String> keyFunction() {
            return Role::getAuthority;
        }

        @Override
        protected Role doCopy(Role entity) {
            return entity; // senselessly
        }

        @Override
        public Role newEntity() {
            return Role.USER; // senselessly
        }

        @Override
        public Role editedEntity(Role role) {
            return role;  // senselessly
        }
    }

    public static <T extends BaseEntity> T setIdIfRequired(T entity, MvcResult result) {
        if (entity.isNew()) {
            entity.setId(getCreatedResourceId(result));
        }
        return entity;
    }
}
