package ru.job4j.auto.web.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.job4j.auto.model.User;
import ru.job4j.auto.service.UserService;

import java.util.List;

import static ru.job4j.auto.util.ValidationHelper.assureIdConsistent;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AbstractUserController {
    protected final UserService service;

    protected User find(int id) {
        log.info("find {}", id);
        return service.find(id);
    }

    protected List<User> findAll() {
        log.info("findAll");
        return service.findAll();
    }

    protected User create(User user) {
        log.info("create {}", user);
        return service.create(user);
    }

    protected void update(User user, int id) {
        log.info("Update {} with id={}", user, id);
        assureIdConsistent(user, id);
        service.update(user);
    }

    protected void enable(int id, boolean enabled) {
        log.info(enabled ? "enable {}" : "disable {}", id);
        service.enable(id, enabled);
    }

    protected void delete(int id) {
        log.info("delete {}", id);
        service.delete(id);
    }
}
