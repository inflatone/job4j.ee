package ru.job4j.auto.service;

import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Service;
import ru.job4j.auto.model.Role;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;

import static ru.job4j.auto.model.Role.ADMIN;
import static ru.job4j.auto.util.ValidationHelper.checkNotFound;

@Service
@RequiredArgsConstructor
public class DataService {
    /**
     * Returns the map (ordinal:entity) of available roles depending on auth user role
     *
     * @param role auth role
     * @return role map
     */
    public Map<String, Role> findAvailableRoles(Role role) {
        checkNotFound(role, "Role must not be null");
        return asMap(role == ADMIN ? EnumSet.allOf(Role.class) : EnumSet.of(role));
    }

    private static <E> Map<String, E> asMap(Collection<E> elements) {
        return StreamEx.of(elements).toMap(Object::toString, Function.identity());
    }
}
