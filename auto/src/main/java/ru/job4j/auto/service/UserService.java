package ru.job4j.auto.service;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.auto.model.User;
import ru.job4j.auto.repository.UserRepository;
import ru.job4j.auto.web.AuthorizedUser;

import java.util.List;

import static ru.job4j.auto.util.ValidationHelper.checkNotFoundEntityWithId;

@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Service("userService")
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository repository;

    /**
     * Asks the store to create the given entity
     *
     * @param user user entity
     * @return resulting entity returned from the store after insert
     */
    public User create(User user) {
        return repository.save(user);
    }

    /**
     * Asks the store  to find the entity associated with the given id, checks not-nullity of returned one
     *
     * @param id id
     * @return found entity
     */
    public User find(int id) {
        return checkNotFoundEntityWithId(repository.find(id), id);
    }
    
    /**
     * Asks the store  to find the entity associated with the given id, with a set of their posts
     *
     * @param id id
     * @return found entity
     */
    public User findWithPosts(int id) {
        return checkNotFoundEntityWithId(repository.findWithPosts(id), id);
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        var user = repository.findByLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException("User " + login + " is not found");
        }
        return new AuthorizedUser(user);
    }

    /**
     * Asks the store to update the given entity
     *
     * @param user user entity
     */
    @Transactional
    public void update(User user) {
        var id = user.getId();
        var persisted = checkNotFoundEntityWithId(find(id), id);
        persisted.setLogin(user.getLogin());
        persisted.setName(user.getName());
        persisted.setRole(user.getRole());
        if (!Strings.isNullOrEmpty(user.getPassword())) {
            persisted.setPassword(user.getPassword());
        }
    }

    /**
     * Asks the store to delete the entity associated with the given id
     *
     * @param id id
     */
    public void delete(int id) {
        repository.delete(id);
    }

    /**
     * Asks the store to enable/disable the given entity, checks if successful of the executed operation
     *
     * @param id      id
     * @param enabled enable/disable point
     */
    @Transactional
    public void enable(int id, boolean enabled) {
        var user = checkNotFoundEntityWithId(find(id), id);
        user.setEnabled(enabled);
    }

    /**
     * Asks the store to give all existing entities
     *
     * @return list of entities
     */
    public List<User> findAll() {
        return repository.findAll();
    }
}
