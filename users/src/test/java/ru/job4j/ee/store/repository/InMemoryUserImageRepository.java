package ru.job4j.ee.store.repository;

import com.google.inject.Inject;
import one.util.streamex.StreamEx;
import ru.job4j.ee.store.model.BaseEntity;
import ru.job4j.ee.store.model.UserImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import static ru.job4j.ee.store.repository.InMemoryUserImageRepository.ImageData;

/**
 * Represents user image in-memory storage
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-17
 */
public class InMemoryUserImageRepository extends InMemoryStorage<ImageData> implements UserImageRepository {
    @Inject
    private UserRepository userRepository;

    @Override
    public boolean save(UserImage image) {
        if (image.isNew()) {
            int id = SEQ.incrementAndGet();
            image.setId(id);
            image.setOid(id); // stub oid = id

            var data = readAllData(image);
            image.setSize(data.length);
            var imageData = new ImageData(image, data);
            storage.put(image.getId(), imageData);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        return storage.remove(id) != null;
    }

    @Override
    public boolean deleteFromUser(int id, int userId) {
        var user = userRepository.find(userId);
        if (user != null && Objects.equals(getIdSafely(user.getImage()), id)) {
            user.setImage(null);
            delete(id);
            return userRepository.save(user);
        }
        return false;
    }


    @Override
    public boolean addToUser(UserImage image, int userId) {
        var user = userRepository.find(userId);
        if (user != null) {
            save(image);
            user.setImage(image);
            return userRepository.save(user);
        }
        readAllData(image); // simulate db storage behavior, need read all data and close input in any case
        return false;
    }

    @Override
    public UserImage find(int id) {
        var imageData = storage.get(id);
        return imageData == null ? null : composeUserImageModel(imageData);
    }

    @Override
    public int clear() {
        var imageIds = StreamEx.of(userRepository.findAll())
                .filter(u -> u.getImage() != null)
                .map(u -> u.getImage().getId())
                .toSet();
        int result = storage.size();
        storage.keySet().retainAll(imageIds);
        return result - storage.size();
    }

    private byte[] readAllData(UserImage image) {
        try (var in = image.getData();
             var buffer = new ByteArrayOutputStream()
        ) {
            in.transferTo(buffer);
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("cannot retrieve image data from input stream", e);
        }
    }

    private UserImage composeUserImageModel(ImageData imageData) {
        var result = new UserImage(imageData.image);
        result.setData(new ByteArrayInputStream(imageData.data));
        return result;
    }

    static class ImageData {
        private UserImage image;
        private byte[] data;

        ImageData(UserImage image, byte[] data) {
            this.image = new UserImage(image);
            this.data = data;
        }
    }

    /**
     * @return ID or null (if entity is null)
     */
    private <T extends BaseEntity> Integer getIdSafely(T entity) {
        return entity == null ? null : entity.getId();
    }
}