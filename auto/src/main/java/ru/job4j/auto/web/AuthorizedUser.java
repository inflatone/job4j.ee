package ru.job4j.auto.web;

import ru.job4j.auto.model.Role;
import ru.job4j.auto.model.User;

import java.util.Collections;

public class AuthorizedUser extends org.springframework.security.core.userdetails.User {
    private static final long serialVersionUID = 1L;

    private User user;

    public AuthorizedUser(User user) {
        super(user.getLogin(), user.getPassword(), user.isEnabled(),
                true, true, true, Collections.singleton(user.getRole()));
        this.user = user;
    }

    public int id() {
        return user.id();
    }

    public boolean isAdmin() {
        return getAuthorities().contains(Role.ADMIN);
    }

    public User extract() {
        return user;
    }

    public void update(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return user.toString();
    }
}
