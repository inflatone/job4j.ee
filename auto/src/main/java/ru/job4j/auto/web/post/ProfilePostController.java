package ru.job4j.auto.web.post;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.service.PostService;
import ru.job4j.auto.to.PostTo;
import ru.job4j.auto.web.AuthorizedUser;
import ru.job4j.auto.web.DataController;
import ru.job4j.auto.web.SecurityHelper;
import ru.job4j.auto.web.converter.ModelConverter;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = ProfilePostController.URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfilePostController extends AbstractPostController {
    public static final String URL = DataController.AJAX_URL + "/profile/posts";

    private final ModelConverter converter;

    public ProfilePostController(PostService service, FilterToValidator filterValidator, ModelConverter converter) {
        super(service, filterValidator);
        this.converter = converter;
    }

    @GetMapping("/{id}")
    public PostTo find(@PathVariable int id, @AuthenticationPrincipal AuthorizedUser auth) {
        return converter.asPostTo(super.find(id, auth.id()), auth);
    }

    @Override
    @GetMapping
    public List<Post> findAll() {
        return super.findAll(SecurityHelper.authUserId());
    }

    @PostMapping
    public ResponseEntity<PostTo> create(@Valid Post post, @AuthenticationPrincipal AuthorizedUser auth) {
        Post created = super.create(post, auth.id());
        PostTo to = converter.asPostTo(created, auth);
        return ResponseEntity.created(to.getUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .body(to);
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable int id, @Valid Post post,
                                         @AuthenticationPrincipal AuthorizedUser auth) {
        super.update(post, id, auth.id());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/completed")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void complete(@PathVariable int id, @RequestBody boolean completed) {
        super.complete(id, SecurityHelper.authUserId(), completed);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id, SecurityHelper.authUserId());
    }
}
