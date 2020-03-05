package ru.job4j.auto.web.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
        var authorizedUser = SecurityHelper.get();
        return super.find(authorizedUser.getId());
    }

    @PostMapping
    public ResponseEntity<String> update(User user) {
        var authorizedUser = SecurityHelper.get();
        super.update(user, authorizedUser.getId());
        authorizedUser.update(user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete() {
        var authorizedUser = SecurityHelper.get();
        super.delete(authorizedUser.getId());
        SecurityHelper.clearAuth();
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
