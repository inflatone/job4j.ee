package ru.job4j.auto.web.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.auto.model.User;
import ru.job4j.auto.service.UserService;
import ru.job4j.auto.to.UserTo;
import ru.job4j.auto.web.AuthorizedUser;
import ru.job4j.auto.web.DataController;
import ru.job4j.auto.web.converter.ModelConverter;

import javax.validation.Valid;

@RestController
@RequestMapping(value = AdminController.URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController extends AbstractUserController {
    public static final String URL = DataController.AJAX_URL + "/admin/users";

    private final ModelConverter converter;

    public AdminController(UserService service, ModelConverter converter) {
        super(service);
        this.converter = converter;
    }

    @PostMapping
    public ResponseEntity<UserTo> create(@Valid User user, @AuthenticationPrincipal AuthorizedUser auth) {
        User created = super.create(user);
        UserTo to = converter.asUserTo(created, auth);
        return ResponseEntity.created(to.getUrl()).contentType(MediaType.APPLICATION_JSON).body(to);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable int id, @Valid User user) {
        super.update(user, id);
    }

    @Override
    @PutMapping("/{id}/enabled")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enable(@PathVariable int id, @RequestBody boolean enabled) {
        super.enable(id, enabled);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id, @AuthenticationPrincipal AuthorizedUser auth) {
        super.delete(id);
        if (auth.id() == id) {
            SecurityContextHolder.clearContext();
        }
    }
}
