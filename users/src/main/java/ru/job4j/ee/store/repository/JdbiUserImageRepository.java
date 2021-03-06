package ru.job4j.ee.store.repository;

import com.google.inject.Inject;
import ru.job4j.ee.store.model.UserImage;
import ru.job4j.ee.store.repository.dbi.UserImageDao;

/**
 * Represents user image DB storage accessor
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-10
 */
public class JdbiUserImageRepository implements UserImageRepository {
    @Inject
    private UserImageDao dao;

    @Override
    public boolean save(UserImage image) {
        // senselessly to update
        return image.isNew() && dao.save(image, null);
    }

    @Override
    public boolean delete(int id) {
        return dao.delete(id, null);
    }

    @Override
    public boolean deleteFromUser(int id, int userId) {
        return dao.delete(id, userId);
    }

    @Override
    public boolean addToUser(UserImage image, int userId) {
        return image.isNew() ? dao.save(image, userId) : dao.updateBind(image.getId(), userId);
    }

    @Override
    public UserImage find(int id) {
        return dao.findById(id);
    }

    @Override
    public int clear() {
        return dao.clearUnused();
    }
}