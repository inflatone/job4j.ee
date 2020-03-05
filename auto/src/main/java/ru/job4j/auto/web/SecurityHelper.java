package ru.job4j.auto.web;

import ru.job4j.auto.model.Role;
import ru.job4j.auto.model.User;

import static ru.job4j.auto.util.ValidationHelper.checkNotFound;

public class SecurityHelper {
    private static AuthorizedUser auth = new AuthorizedUser(new User(1, "User", "user", "password", Role.USER));

    private SecurityHelper() {
    }

    public static AuthorizedUser safeGet() {
        return auth;
    }

    public static AuthorizedUser get() {
        return checkNotFound(safeGet(), "No authorized user found");
    }

    public static void setAuth(User user) {
        auth = new AuthorizedUser(user);
    }

    public static void clearAuth() {
        auth = null;
    }

    public static int authUserId() {
        return get().extract().id();
    }
}