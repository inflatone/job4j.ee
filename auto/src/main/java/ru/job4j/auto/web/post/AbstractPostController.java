package ru.job4j.auto.web.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.service.PostService;
import ru.job4j.auto.to.filter.PostFilterTo;

import java.util.List;

import static ru.job4j.auto.util.ValidationHelper.assureIdConsistent;

@Slf4j
@RequiredArgsConstructor
public class AbstractPostController {
    private final PostService service;

    private final FilterToValidator filterValidator;

    @InitBinder("PostFilterTo")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(filterValidator);
    }

    protected Post find(int id) {
        log.info("Find {} ", id);
        return service.find(id);
    }

    protected Post findFully(int id) {
        log.info("Find {} with all details", id);
        return service.findFully(id);
    }

    protected Post find(int id, int profileId) {
        log.info("Find {} for user with id={}", id, profileId);
        return service.find(id, profileId);
    }

    protected List<Post> findAll() {
        log.info("Find all posts of all users");
        return service.findAll();
    }

    protected List<Post> findAll(int profileId) {
        log.info("Find all posts for user with id={}", profileId);
        return service.findAll(profileId);
    }

    protected List<Post> findFiltered(PostFilterTo filter) {
        log.info("Find filtered ones fit {}", filter);
        return service.findFiltered(filter);

//        var res= StreamEx.of(service.findAll())
//                .filter(p -> post.getUser() == null || post.getUser().getId().equals(p.getUser().getId()))
//                .filter(p -> post.getPosted() == null || post.getPosted().isBefore(p.getPosted()))
//                .filter(p -> post.getPrice() == null || (p.getPrice() != null && post.getPrice().compareTo(p.getPrice()) > 0))
//                .filter(p -> post.getCar() == null || post.getCar().getBody() == null || post.getCar().getBody().getId().equals(p.getCar().getBody().getId()))
//                .filter(p -> post.getCar() == null || post.getCar().getEngine() == null || post.getCar().getEngine().getId().equals(p.getCar().getEngine().getId()))
//                .filter(p -> post.getCar() == null || post.getCar().getTransmission() == null || post.getCar().getTransmission().getId().equals(p.getCar().getTransmission().getId()))
//                .filter(p -> post.getCar() == null || post.getCar().getVendor() == null || post.getCar().getVendor().getId().equals(p.getCar().getVendor().getId()))
//                .filter(p -> post.getCar() == null || post.getCar().getYear() == null || post.getCar().getYear().compareTo(p.getCar().getYear()) <= 0)
//                .filter(p -> post.getCar() == null || post.getCar().getMileage() == null || post.getCar().getMileage().compareTo(p.getCar().getMileage()) >= 0)
//                .filter(p -> !withImage || p.getImage() != null)
//                .toList();
//        return StreamEx.of(service.findAll())
//                .filter(p -> filter.getUser() == null || filter.getUser().equals(p.getUser().getId()))
//                .filter(p -> !filter.isWithImage() || p.getImage() != null)
//                .toList();
    }

    protected Post create(Post post, int profileId) {
        log.info("Create {} for user with id={}", post, profileId);
        return service.create(post, profileId);
    }

    protected void update(Post post, int id, int profileId) {
        log.info("Update {} with id={} for user with id={}", post, id, profileId);
        assureIdConsistent(post, id);
        service.update(post, profileId);
    }

    protected void complete(int id, int profileId, boolean completed) {
        service.complete(id, profileId, completed);
    }

    protected void delete(int id, int profileId) {
        log.info("Delete post with id={} for user with id={}", id, profileId);
        service.delete(id, profileId);
    }
}
