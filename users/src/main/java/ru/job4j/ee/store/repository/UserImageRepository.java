package ru.job4j.ee.store.repository;

import ru.job4j.ee.store.model.UserImage;

import java.util.List;

/**
 * Represents interface to implement user image data storing classes
 * In future may contain additional methods required to process user image TOs
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-10
 */
public interface UserImageRepository extends Repository<UserImage> {
    /**
     * Unbinds user image data from the user entity data, then delete the image data from the store
     *
     * @param id     image id
     * @param userId user id
     * @return {@code true} if successful
     */
    boolean deleteFromUser(int id, int userId);

    @Override
    default List<UserImage> findAll() {
        throw new UnsupportedOperationException("Not implemented");
    }
}