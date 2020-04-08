package ru.job4j.auto.service;

import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.job4j.auto.model.Image;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.model.User;
import ru.job4j.auto.repository.ImageRepository;
import ru.job4j.auto.repository.PostRepository;
import ru.job4j.auto.repository.UserRepository;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.io.IOException;

import static ru.job4j.auto.util.ValidationHelper.assureIdConsistent;
import static ru.job4j.auto.util.ValidationHelper.checkNotFoundEntityWithId;

@Service
@RequiredArgsConstructor
public class ImageService {
    public static final String NO_IMAGE_RESOURCE = "img/noImage.jpg";

    private Image noImage;

    private final ImageRepository repository;

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    @PostConstruct
    public void prepareDefault() throws IOException {
        try (var in = Resources.getResource(NO_IMAGE_RESOURCE).openStream()) {
            noImage = new Image(null, "noImage.jpg", MediaType.IMAGE_JPEG_VALUE, ByteStreams.toByteArray(in));
        }
    }

    public Image find(@Nullable Integer id) {
        Image image = null;
        if (id != null) {
            image = repository.find(id);
        }
        return image != null ? image : noImage;
    }

    @Transactional
    public Image uploadToUser(int userId, Image image) {
        Assert.notNull(image, "Image has not been selected");
        User user = checkNotFoundEntityWithId(userRepository.find(userId), userId);
        user.setImage(image);
        return image;
    }

    @Transactional
    public Image uploadToPost(int postId, int userId, Image image) {
        Assert.notNull(image, "Image has not been selected");
        Post post = checkNotFoundEntityWithId(postRepository.find(postId), postId);
        assureIdConsistent(post.getUser(), userId);
        post.setImage(image);
        return image;
    }

    @Transactional
    public void deleteFromUser(int userId) {
        User user = checkNotFoundEntityWithId(userRepository.find(userId), userId);
        user.setImage(null);
    }

    @Transactional
    public void deleteFromPost(int postId, int userId) {
        Post post = checkNotFoundEntityWithId(postRepository.find(postId), postId);
        assureIdConsistent(post.getUser(), userId);
        post.setImage(null);
    }
}
