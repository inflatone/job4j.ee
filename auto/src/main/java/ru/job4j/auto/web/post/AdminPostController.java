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
import ru.job4j.auto.web.converter.ModelConverter;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = AdminPostController.URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminPostController extends AbstractPostController {
    public static final String URL = DataController.AJAX_URL + "/admin/users/{profileId}/posts";

    private final ModelConverter converter;

    public AdminPostController(PostService service, ModelConverter converter) {
        super(service);
        this.converter = converter;
    }

    @GetMapping("/{id}")
    public PostTo find(@PathVariable int id, @PathVariable int profileId, @AuthenticationPrincipal AuthorizedUser auth) {
        return converter.asPostTo(super.find(id, profileId), auth);
    }

    @Override
    @GetMapping
    public List<Post> findAll(@PathVariable int profileId) {
        return super.findAll(profileId);
    }

    @PostMapping
    public ResponseEntity<PostTo> create(@Valid Post post, @PathVariable int profileId,
                                         @AuthenticationPrincipal AuthorizedUser auth) {
        Post created = super.create(post, profileId);
        PostTo to = converter.asPostTo(created, auth);
        return ResponseEntity.created(to.getUrl()).contentType(MediaType.APPLICATION_JSON).body(to);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable int id, @PathVariable int profileId, @Valid Post post) {
        super.update(post, id, profileId);
    }

    @Override
    @PutMapping("/{id}/completed")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void complete(@PathVariable int id, @PathVariable int profileId, @RequestBody boolean completed) {
        super.complete(id, profileId, completed);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id, @PathVariable int profileId) {
        super.delete(id, profileId);
    }
}
