package ru.job4j.auto.web.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.job4j.auto.model.User;
import ru.job4j.auto.service.UserService;
import ru.job4j.auto.web.SecurityHelper;

import javax.validation.Valid;
import java.net.URI;

@Slf4j
@RestController
@RequestMapping(ProfileController.URL)
public class ProfileController extends AbstractUserController {
    public static final String URL = "/ajax/profile";

    public ProfileController(UserService service) {
        super(service);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public User find() {
        return find(SecurityHelper.authUserId());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User find(@PathVariable int id) {
        return super.find(id);
    }

    @PostMapping
    public ResponseEntity<String> update(User user) {
        return update(SecurityHelper.authUserId(), user);
    }

    @PostMapping("/{profileId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> update(@PathVariable int profileId, User user) {
        super.update(user, profileId);
        if (profileId == SecurityHelper.authUserId()) {
            SecurityHelper.get().update(user);
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete() {
        delete(SecurityHelper.authUserId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void delete(@PathVariable int id) {
        super.delete(id);
        if (SecurityHelper.authUserId() == id) {
            SecurityHelper.clearAuth();
        }
    }

    @PostMapping("/registration")
    public ResponseEntity<String> register(@Valid User user) {
        super.create(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(URL)
                .buildAndExpand()
                .toUri();
        return ResponseEntity.created(uriOfNewResource).build();
    }
}
