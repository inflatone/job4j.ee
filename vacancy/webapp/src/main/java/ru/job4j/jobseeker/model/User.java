package ru.job4j.jobseeker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
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

    @JsonProperty(access = WRITE_ONLY)
    private String password;

    private Date registered;

    private Role role;

    public User(User user) {
        this(user.getId(), user.login, user.password, user.registered, user.role);
    }

    @JdbiConstructor
    public User(@ColumnName("id") Integer id,
                @ColumnName("login") String login,
                @ColumnName("password") String password,
                @ColumnName("registered") Date registered,
                @ColumnName("role") Role role) {
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
