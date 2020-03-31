package ru.job4j.auto.config.helper;

import lombok.Getter;
import one.util.streamex.StreamEx;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.job4j.auto.model.BaseEntity;
import ru.job4j.auto.model.Role;
import ru.job4j.auto.model.User;
import ru.job4j.auto.to.BaseTo;
import ru.job4j.auto.web.converter.JsonHelper;
import ru.job4j.auto.web.converter.ModelConverter;

import java.util.List;
import java.util.Objects;

public abstract class BaseToTestHelper<TO extends BaseTo, E extends BaseEntity> extends AbstractEntityTestHelper<TO> {
    protected final ModelConverter converter;

    public BaseToTestHelper(JsonHelper jsonHelper, Class<TO> clazz,
                            boolean checkRecursively, ModelConverter converter, String... fieldsToIgnore) {
        super(jsonHelper, clazz, checkRecursively, fieldsToIgnore);
        this.converter = converter;
    }

    @Getter
    private User auth; // need to create links into TOs

    public BaseToTestHelper<TO, E> forAuth(User auth) {
        this.auth = auth;
        return this;
    }

    protected boolean adminAccess() {
        return auth != null && auth.getRole() == Role.ADMIN;
    }

    protected boolean selfAccess(int id) {
        Objects.requireNonNull(auth, "Set auth before this method called");
        return id == auth.getId();
    }

    public abstract TO entityToTo(E entity);

    public ResultMatcher contentJson(E expected) {
        return super.contentJson(entityToTo(expected));
    }

    @SafeVarargs
    public final ResultMatcher contentJson(E... expected) {
        return contentJson(List.of(expected));
    }

    public final ResultMatcher contentJson(List<E> expected) {
        return super.contentJson(StreamEx.of(expected).map(this::entityToTo).toList());
    }

    // only matchers needed

    @Override
    protected TO doCopy(TO entity) {
        return null;
    }

    @Override
    public TO newEntity() {
        return null;
    }

    // only matchers needed
    @Override
    public TO editedEntity(TO t) {
        return null;
    }
}
