package ru.job4j.ee.store.service;

import com.google.inject.Inject;
import ru.job4j.ee.store.model.UserImage;
import ru.job4j.ee.store.repository.UserImageRepository;

import static java.util.Objects.requireNonNull;
import static ru.job4j.ee.store.util.ValidationUtil.checkNotFound;
import static ru.job4j.ee.store.util.ValidationUtil.checkNotFoundEntityWithId;

/**
 * Represents service layer of the app (validates the given user image data from the store, then transfer them to the web)
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-10
 */
public class UserImageService {
    @Inject
    private UserImageRepository repository;

    /**
     * Asks the store to find the image entity associated with the given user id
     *
     * @param id id
     * @return user image (or null if does not exist)
     */
    public UserImage find(int id) {
        return repository.find(id);
    }

    /**
     * Asks the store to unbind the image entity associated with the given image id from the user entity associated with the given user id,
     * checks if the executed operation was successful (otherwise it throws {@link NullPointerException})
     *
     * @param id     id
     * @param userId user id
     */
    public void delete(int id, int userId) {
        checkNotFoundEntityWithId(repository.deleteFromUser(id, userId), id);
    }

    /**
     * Asks the image store to persist the given image entity, then binds it with the suggested user id in the user store
     *
     * @param userImage user image
     */
    public void save(UserImage userImage, int userId) {
        requireNonNull(userImage, "Image must not be null");
        checkNotFound(repository.addToUser(userImage, userId), "Cannot upload image of user with id=" + userId);
    }

    /**
     * Asks the store to clear all the unused images
     *
     * @return deleted images amount
     */
    public int clear() {
        return repository.clear();
    }
}