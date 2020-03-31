package ru.job4j.auto.web.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.auto.model.Role;
import ru.job4j.auto.model.User;
import ru.job4j.auto.service.UserService;
import ru.job4j.auto.to.UserTo;
import ru.job4j.auto.web.AuthorizedUser;
import ru.job4j.auto.web.DataController;
import ru.job4j.auto.web.converter.ModelConverter;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(ProfileController.URL)
public class ProfileController extends AbstractUserController {
    public static final String URL = DataController.AJAX_URL + "/profile";

    private final ModelConverter converter;

    public ProfileController(UserService service, ModelConverter converter) {
        super(service);
        this.converter = converter;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid User user, @AuthenticationPrincipal AuthorizedUser auth) {
        super.update(user, auth.id());
        auth.update(user);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthorizedUser auth) {
        super.delete(auth.id());
        SecurityContextHolder.clearContext();
    }

    @PostMapping("/registration")
    public ResponseEntity<UserTo> register(@Valid User user) {
        User created = super.create(user);
        UserTo to = converter.asUserTo(created, false, true);
        return ResponseEntity.created(to.getUrl()).contentType(MediaType.APPLICATION_JSON).body(to);
    }
}
