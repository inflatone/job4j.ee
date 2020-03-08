package ru.job4j.auto.web.post;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.service.PostService;
import ru.job4j.auto.web.SecurityHelper;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = ProfilePostController.URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfilePostController extends AbstractPostController {
    public static final String URL = "/ajax/profile/posts";

    public ProfilePostController(PostService service) {
        super(service);
    }

    @GetMapping("/{id}")
    public Post find(@PathVariable int id) {
        return super.find(id, SecurityHelper.authUserId());
    }

    @Override
    @GetMapping
    public List<Post> findAll() {
        return super.findAll(SecurityHelper.authUserId());
    }

    @PostMapping
    public ResponseEntity<String> createOrUpdate(@Valid Post post) {
        if (post.isNew()) {
            Post created = super.create(post, SecurityHelper.authUserId());
            URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path((URL + "/{id}"))
                    .buildAndExpand(created.getId())
                    .toUri();
            return ResponseEntity.created(uriOfNewResource).build();
        }
        super.update(post, post.getId(), SecurityHelper.authUserId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void complete(@PathVariable int id, @RequestParam boolean completed) {
        super.complete(id, SecurityHelper.authUserId(), completed);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id, SecurityHelper.authUserId());
    }
}
