package ru.job4j.auto.service;

import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Service;
import ru.job4j.auto.model.*;
import ru.job4j.auto.repository.*;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;

import static ru.job4j.auto.model.Role.ADMIN;
import static ru.job4j.auto.util.ValidationHelper.checkNotFound;

@Service
@RequiredArgsConstructor
public class DataService {
    private final VendorRepository vendorRepository;

    private final BodyRepository bodyRepository;

    private final EngineRepository engineRepository;

    private final TransmissionRepository transmissionRepository;

    /**
     * Returns the map (ordinal:entity) of available roles depending on auth user role
     *
     * @param role auth role
     * @return role map
     */
    public Map<String, Role> findAvailableRoles(Role role) {
        checkNotFound(role, "Role must not be null");
        return asMap(role == ADMIN ? EnumSet.allOf(Role.class) : EnumSet.of(role), Role::getAuthority);
    }

    /**
     * Returns the map (id:entity) of available car vendors
     *
     * @return vendor map
     */
    public Map<Integer, Vendor> findAvailableVendors() {
        return findAll(vendorRepository);
    }

    /**
     * Returns the map (id:entity) of available car bodies
     *
     * @return body map
     */
    public Map<Integer, Body> findAvailableCarBodies() {
        return findAll(bodyRepository);
    }

    /**
     * Returns the map (id:entity) of available car engines
     *
     * @return engine map
     */
    public Map<Integer, Engine> findAvailableCarEngines() {
        return findAll(engineRepository);
    }

    /**
     * Returns the map (id:entity) of available car transmissions
     *
     * @return transmission map
     */
    public Map<Integer, Transmission> findAvailableCarTransmissions() {
        return findAll(transmissionRepository);
    }

    private static <E extends BaseEntity> Map<Integer, E> findAll(BaseEntityRepository<E> repository) {
        return modelAsMap(repository.findAll());
    }

    private static <E extends BaseEntity> Map<Integer, E> modelAsMap(Collection<E> elements) {
        return asMap(elements, BaseEntity::getId);
    }

    private static <K, V> Map<K, V> asMap(Collection<V> elements, Function<V, K> keyMapper) {
        return StreamEx.of(elements).toMap(keyMapper, Function.identity());
    }
}
