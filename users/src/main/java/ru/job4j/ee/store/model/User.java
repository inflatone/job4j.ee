package ru.job4j.ee.store.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jdbi.v3.core.mapper.Nested;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

/**
 * Model to transfer user data layer-to-layer
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-05
 */
public class User extends BaseNamedEntity {
    private String login;

    // https://stackoverflow.com/a/12505165/548473
    @JsonProperty(access = WRITE_ONLY)
    private String password;

    private Date created;
    private boolean enabled = true;
    private Role role;
    private City city;
    private UserImage image;

    public User() {
    }

    public User(User user) {
        this(user.getId(), user.getName(), user.login, user.password, user.created, user.role, user.enabled,
                user.city == null ? null : new City(user.city),
                user.image == null ? null : new UserImage(user.image));
    }

    @JdbiConstructor
    public User(@ColumnName("id") Integer id,
                @ColumnName("name") String name,
                @ColumnName("login") String login,
                @ColumnName("password") String password,
                @ColumnName("created") Date created,
                @ColumnName("role") Role role,
                @ColumnName("enabled") boolean enabled,
                @Nested("city_") City city,
                @Nested("image_") UserImage image) {
        super(id, name);
        this.login = login;
        this.password = password;
        this.created = created;
        this.role = role;
        this.enabled = enabled;
        this.city = city;
        this.image = image;
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

    public Role getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public UserImage getImage() {
        return image;
    }

    public City getCity() {
        return city;
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

    public void setRole(Role role) {
        this.role = role;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setImage(UserImage image) {
        this.image = image;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "User{"
                + "id=" + getId()
                + ", name='" + getName() + '\''
                + ", login='" + login + '\''
                + ", password='" + password + '\''
                + ", created=" + created
                + ", image=" + image
                + ", enabled=" + enabled
                + ", role=" + role
                + ", city=" + city
                + '}';
    }
}
