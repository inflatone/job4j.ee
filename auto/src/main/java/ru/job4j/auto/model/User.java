package ru.job4j.auto.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {
    private String name;

    private String login;

    private String password;

    private Instant registered;

    private Role role;

    private boolean enabled = true;

    private Set<Post> posts;

    public User(Integer id, String name, String login, String password, Role role) {
        super(id);
        this.name = name;
        this.login = login;
        this.password = password;
        this.role = role;
    }
}
