package ru.job4j.auto.web.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.job4j.auto.model.User;
import ru.job4j.auto.service.UserService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = AdminController.URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController extends AbstractUserController {
    public static final String URL = "/ajax/admin/users";

    public AdminController(UserService service) {
        super(service);
    }

    @Override
    @GetMapping("/{id}")
    public User find(@PathVariable int id) {
        return super.find(id);
    }

    @Override
    @GetMapping
    public List<User> findAll() {
        return super.findAll();
    }

    @PostMapping
    public ResponseEntity<String> createOrUpdate(@Valid User user) {
        if (user.isNew()) {
            User created = super.create(user);
            URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path((URL + "/{id}"))
                    .buildAndExpand(created.getId())
                    .toUri();
            return ResponseEntity.created(uriOfNewResource).build();
        }
        super.update(user, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enable(@PathVariable int id, @RequestParam boolean enabled) {
        super.enable(id, enabled);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }
}
