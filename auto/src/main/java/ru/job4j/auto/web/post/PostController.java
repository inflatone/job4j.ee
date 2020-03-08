package ru.job4j.auto.web.post;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.auto.model.Car;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.service.PostService;

import javax.annotation.Nullable;
import java.util.List;

@RestController
@RequestMapping(PostController.URL)
public class PostController extends AbstractPostController {
    public static final String URL = "/ajax/posts";

    public PostController(PostService service) {
        super(service);
    }

    @Override
    @GetMapping
    public List<Post> findAll() {
        return super.findAll();
    }

    @GetMapping("/filtered")
    public List<Post> findFiltered(@RequestParam @Nullable Boolean hasImage,
                                   @RequestParam @Nullable Boolean isToday,
                                   @RequestParam @Nullable Car template
    ) {
        return super.findAll();
    }
}
