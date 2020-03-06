package ru.job4j.auto.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static ru.job4j.auto.util.ValidationHelper.checkNotFound;

public class SecurityHelper {
    private SecurityHelper() {
    }

    public static AuthorizedUser safeGet() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        return (principal instanceof AuthorizedUser) ? (AuthorizedUser) principal : null;
    }

    public static AuthorizedUser get() {
        return checkNotFound(safeGet(), "No authorized user found");
    }

    public static void clearAuth() {
        SecurityContextHolder.clearContext();
    }

    public static int authUserId() {
        return get().extract().id();
    }
}