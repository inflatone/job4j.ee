package ru.job4j.todo.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Date;

@Getter
@Setter
public class Item {
    private Integer id;
    private String description;
    private Date created = new Date();
    private boolean done;

    public boolean isNew() {
        return id == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !getClass().equals(Hibernate.getClass(o))) {
            return false;
        }
        Item that = (Item) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", created=" + created +
                ", done=" + done +
                '}';
    }
}
