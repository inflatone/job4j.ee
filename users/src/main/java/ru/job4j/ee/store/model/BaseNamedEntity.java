package ru.job4j.ee.store.model;

/**
 * Base abstract model class to persist entities with ID and name fields
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-11-30
 */
public abstract class BaseNamedEntity extends BaseEntity {
    private String name;

    public BaseNamedEntity() {
    }

    public BaseNamedEntity(Integer id) {
        super(id);
    }

    public BaseNamedEntity(Integer id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
