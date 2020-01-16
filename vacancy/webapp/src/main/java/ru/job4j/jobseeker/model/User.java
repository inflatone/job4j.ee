package ru.job4j.jobseeker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

import static ru.job4j.vacancy.util.ExceptionUtil.nullSafely;

/**
 * User model
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-16
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class User extends BaseEntity {
    private String login;

    private String password;

    private Date registered;

    private Role role;

    public User(User user) {
        this(user.getId(), user.login, user.password, user.registered, user.role);
    }

    public User(Integer id, String login, String password, Date registered, Role role) {
        super(id);
        this.login = login;
        this.password = password;
        this.registered = registered;
        this.role = role;
    }

    @JsonProperty
    public Integer getRoleOrdinal() {
        return role.ordinal();
    }

    @JsonProperty
    public void setRoleOrdinal(Integer id) {
        setRole(nullSafely(id, i -> Role.values()[id]));
    }
}
