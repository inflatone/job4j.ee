package ru.job4j.auto.web.user;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.auto.service.UserService;
import ru.job4j.auto.to.UserTo;
import ru.job4j.auto.web.AuthorizedUser;
import ru.job4j.auto.web.DataController;
import ru.job4j.auto.web.converter.ModelConverter;

import java.util.List;

@RestController
@RequestMapping(value = UserController.URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController extends AbstractUserController {
    public static final String URL = DataController.AJAX_URL + "/users";

    private final ModelConverter converter;

    public UserController(UserService service, ModelConverter converter) {
        super(service);
        this.converter = converter;
    }

    @GetMapping
    public List<UserTo> findAll(@AuthenticationPrincipal AuthorizedUser auth) {
        return converter.asUserTo(super.findAll(), auth);
    }

    @GetMapping("/{id}")
    public UserTo find(@PathVariable int id, @AuthenticationPrincipal AuthorizedUser auth) {
        return converter.asUserTo(super.find(id), auth);
    }

}
