package ru.job4j.ee.store.model;

import java.util.Date;

/**
 * Model to transfer user data layer-to-layer
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-05
 */
public class User extends BaseEntity {
    private String name;
    private String login;
    private String password;
    private Date created;

    public User(User user) {
        this(user.getId(), user.name, user.login, user.password, user.created);
    }

    public User(Integer id, String name, String login, String password, Date created) {
        super(id);
        this.name = name;
        this.login = login;
        this.password = password;
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Date getCreated() {
        return created;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "User{"
                + "id=" + getId()
                + ", name='" + name + '\''
                + ", login='" + login + '\''
                + ", password='" + password + '\''
                + ", created=" + created
                + '}';
    }
}
