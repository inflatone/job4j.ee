package ru.job4j.auto.web.post;

import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.job4j.auto.model.Car;
import ru.job4j.auto.service.PostService;
import ru.job4j.auto.to.FilterTo;
import ru.job4j.auto.to.PostTo;
import ru.job4j.auto.web.AuthorizedUser;
import ru.job4j.auto.web.DataController;
import ru.job4j.auto.web.converter.ModelConverter;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(PostController.URL)
public class PostController extends AbstractPostController {
    public static final String URL = DataController.AJAX_URL + "/posts";

    private final ModelConverter converter;

    public PostController(PostService service, ModelConverter converter) {
        super(service);
        this.converter = converter;
    }

    @GetMapping
    public List<PostTo> findAll(@AuthenticationPrincipal AuthorizedUser auth) {
        return converter.asPostTo(super.findAll(), auth, true);
    }

    @GetMapping("/filter")
    public List<PostTo> findFiltered(@Valid FilterTo filter,
                                     @AuthenticationPrincipal AuthorizedUser auth) {
        return converter.asPostTo(super.findFiltered(filter), auth, true);
    }

    @GetMapping("/{id}")
    public PostTo find(@PathVariable int id, @AuthenticationPrincipal AuthorizedUser auth) {
        return converter.asPostTo(super.findFully(id), auth, true);
    }

    @GetMapping("/filtered") // https://habr.com/ru/sandbox/95575/
    public List<PostTo> findFiltered(@RequestParam @Nullable Boolean hasImage,
                                     @RequestParam @Nullable Boolean isToday,
                                     @RequestParam @Nullable Car template,
                                     @AuthenticationPrincipal AuthorizedUser auth) {
        return converter.asPostTo(super.findAll(), auth);
    }
}
