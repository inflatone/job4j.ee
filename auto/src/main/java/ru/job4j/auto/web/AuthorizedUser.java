package ru.job4j.auto.web;

import lombok.AllArgsConstructor;
import ru.job4j.auto.model.User;

@AllArgsConstructor
public class AuthorizedUser {
    private User user;

    public int getId() {
        return user.getId();
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
