package ru.job4j.auto.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * Role model
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-12-09
 */
public enum Role implements GrantedAuthority {
    USER,
    ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}