package ru.job4j.auto.web.post;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.service.PostService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = AdminPostController.URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminPostController extends AbstractPostController {
    public static final String URL = "/ajax/admin/users/{profileId}/posts";

    public AdminPostController(PostService service) {
        super(service);
    }

    @Override
    @GetMapping("/{id}")
    public Post find(@PathVariable int id, @PathVariable int profileId) {
        return super.find(id, profileId);
    }

    @Override
    @GetMapping
    public List<Post> findAll(@PathVariable int profileId) {
        return super.findAll(profileId);
    }

    @PostMapping
    public ResponseEntity<String> createOrUpdate(@Valid Post post, @PathVariable int profileId) {
        if (post.isNew()) {
            Post created = super.create(post, profileId);
            URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(URL + "/{id}")
                    .buildAndExpand(profileId, created.getId())
                    .toUri();
            return ResponseEntity.created(uriOfNewResource).build();
        }
        super.update(post, post.getId(), profileId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void complete(@PathVariable int id, @PathVariable int profileId, @RequestParam boolean completed) {
        super.complete(id, profileId, completed);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id, @PathVariable int profileId) {
        super.delete(id, profileId);
    }
}
