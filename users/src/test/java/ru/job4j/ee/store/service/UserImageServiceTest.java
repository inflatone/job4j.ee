package ru.job4j.ee.store.service;

import com.google.inject.Inject;
import com.google.inject.Module;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.job4j.ee.store.repository.UserImageRepository;
import ru.job4j.ee.store.util.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static ru.job4j.ee.store.AssertionUtil.*;

public abstract class UserImageServiceTest extends AbstractServiceTest {
    public UserImageServiceTest(Module repositoryModule) {
        super(repositoryModule);
    }

    @Inject
    private UserImageService imageService;

    @Inject
    private UserImageRepository userImageRepository;

    @Test
    void save() throws Exception {
        var image = createUserImageModel();

        imageService.save(image, USER.getId());
        var id = image.getId();

        assertNotNull(id); // repository have pasted id in model

        assertEquals(0, image.getData().available()); // repository have read all data
        Mockito.verify(image.getData()).close(); // repository have closed IS after using

        assertMatch(imageService.find(id), image);
    }

    @Test
    void saveExisted() throws Exception {
        var image = createUserImageModel();
        userImageRepository.save(image);
        assertFalse(image.isNew());
        var savedImage = userImageRepository.find(image.getId());
        assertMatch(savedImage, image);

        imageService.save(savedImage, USER.getId()); // only bind image id with user entry
        // lazy initialization: only image id retrieving
        assertEquals(savedImage.getId(), userRepository.find(USER.getId()).getImage().getId());
    }

    @Test
    void saveNotExistedUserId() throws Exception {
        var image = createUserImageModel();

        assertThrows(NotFoundException.class, () -> imageService.save(image, 0));
        Mockito.verify(image.getData()).close();
        assertNull(image.getId());
    }

    @Test
    void bindExistedImgToNotExistedUserId() throws Exception {
        var image = createUserImageModel();
        userImageRepository.save(image);

        assertFalse(image.isNew());
        Mockito.verify(image.getData()).close();
        assertThrows(NotFoundException.class, () -> imageService.save(image, 0));

        var saved = imageService.find(image.getId());
        assertMatch(saved, image);
    }

    @Test
    void repeatableSaving() throws Exception {
        var image = createUserImageModel();
        userImageRepository.save(image);
        var firstId = image.getId(); // first save(): new id must be set

        assertEquals(0, image.getData().available());

        userImageRepository.save(image);

        var secondId = image.getId(); // second save(): do nothing, id must stay the same
        Mockito.verify(image.getData()).close(); // repository have closed IS after using and only one time
        assertEquals(secondId.intValue(), firstId.intValue());
    }

    @Test
    void findNotFound() {
        assertNull(imageService.find(0));
    }

    @Test
    void findAll() {
        assertThrows(UnsupportedOperationException.class, userImageRepository::findAll);
    }

    @Test
    void delete() throws Exception {
        var image = createUserImageModel();

        imageService.save(image, USER.getId());
        var id = image.getId();

        assertMatch(imageService.find(id), image);

        imageService.delete(id, USER.getId());
        assertNull(imageService.find(id));
    }

    @Test
    void deleteNotBound() throws Exception {
        var image = createUserImageModel();
        userImageRepository.save(image);
        assertFalse(image.isNew());

        Mockito.verify(image.getData()).close(); // repository have closed IS after using
        assertTrue(userImageRepository.delete(image.getId()));
        assertNull(imageService.find(image.getId()));
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> imageService.delete(0, 0));
    }

    @Test
    void deleteNotOwn() {
        assertThrows(NotFoundException.class, () -> imageService.delete(0, 0));
    }
}
