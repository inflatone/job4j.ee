package ru.job4j.auto.web.post;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.auto.service.PostService;
import ru.job4j.auto.to.PostTo;
import ru.job4j.auto.to.filter.PostFilterTo;
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

    public PostController(PostService service, FilterToValidator filterValidator, ModelConverter converter) {
        super(service, filterValidator);
        this.converter = converter;
    }

    // TODO ONLY NO-COMPLETED POSTS FOR ALL METHODS (service: find(boolean includeCompleted), findAll(boolean includeCompleted)

    @GetMapping
    public List<PostTo> findAll(@AuthenticationPrincipal AuthorizedUser auth) {
        return converter.asPostTo(super.findAll(), auth, true);
    }

    @GetMapping("/filter")
    public List<PostTo> findFiltered(@Valid PostFilterTo filter,
                                     @AuthenticationPrincipal AuthorizedUser auth) {
        return converter.asPostTo(super.findFiltered(filter), auth, true);
    }

    @GetMapping("/{id}")
    public PostTo find(@PathVariable int id, @AuthenticationPrincipal AuthorizedUser auth) {
        return converter.asPostTo(super.findFully(id), auth, true);
    }
}
