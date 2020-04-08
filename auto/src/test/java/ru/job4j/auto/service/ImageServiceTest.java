package ru.job4j.auto.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.job4j.auto.config.helper.ImageEntityTestHelper;
import ru.job4j.auto.model.Image;
import ru.job4j.auto.model.User;
import ru.job4j.auto.util.exception.IllegalRequestDataException;
import ru.job4j.auto.util.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static ru.job4j.auto.TestModelData.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ImageServiceTest extends AbstractServiceTest {
    private final ImageService service;

    private final UserService userService;

    private final ImageEntityTestHelper testHelper;

    @Test
    void findDefault() {
        Image image = service.find(null);
        testHelper.assertDefault(image);
    }

    @Test
    void uploadToUser() {
        Image imageToUpload = testHelper.newEntity();
        Image saved = service.uploadToUser(USER.getId(), testHelper.copy(imageToUpload));

        var newId = saved.getId();
        imageToUpload.setId(newId);

        testHelper.assertMatch(saved, imageToUpload);
        testHelper.assertMatch(service.find(newId), imageToUpload);
        assertEquals(newId, userService.find(USER.getId()).getImage().getId());
    }

    @Test
    void uploadToUserWhenAlreadyExists() {
        Image uploaded = service.uploadToUser(DEALER.getId(), testHelper.newEntity());
        Image overUploaded = service.uploadToUser(DEALER.getId(), testHelper.newEntity());

        int newId = userService.find(DEALER.getId()).getImage().getId();

        assertEquals(overUploaded.getId(), newId);
        testHelper.assertMatch(overUploaded, service.find(newId));

        testHelper.assertDefault(service.find(uploaded.getId()));
    }

    @Test
    void uploadToPost() {
        Image imageToUpload = testHelper.newEntity();
        Image saved = service.uploadToPost(POST_MAZDA3.getId(), DEALER.getId(), imageToUpload);

        var newId = saved.getId();
        imageToUpload.setId(newId);

        testHelper.assertMatch(saved, imageToUpload);
        testHelper.assertMatch(service.find(newId), imageToUpload);

        User persistedUser = userService.findWithPosts(DEALER.getId());
        assertEquals(newId, persistedUser.getPosts().iterator().next().getImage().getId());
    }

    @Test
    void uploadToAnotherUserPost() {
        Image imageToUpload = testHelper.newEntity();
        var thrown = assertThrows(IllegalRequestDataException.class, () -> service.uploadToPost(POST_MAZDA3.getId(), USER.getId(), imageToUpload));
        assertEquals("Wrong entity(id=" + DEALER.getId() + "), must be with id=" + USER.getId(), thrown.getMessage());
    }

    @Test
    void deleteFromUser() {
        Image imageToUpload = testHelper.newEntity();
        Integer savedId = service.uploadToUser(USER.getId(), imageToUpload).getId();
        assertNotNull(savedId, "Image id must be set");

        service.deleteFromUser(USER.getId());

        testHelper.assertDefault(service.find(savedId));
        assertNull(userService.find(USER.getId()).getImage(), "Image must absent");
    }

    @Test
    void deleteNotExisted() {
        var thrown = assertThrows(NotFoundException.class, () -> service.deleteFromUser(0));
        assertEquals("Not found entity with id=0", thrown.getMessage());
    }

    @Test
    void deleteFromPost() {
        Image imageToUpload = testHelper.newEntity();
        Integer savedId = service.uploadToPost(POST_MAZDA3.getId(), DEALER.getId(), imageToUpload).getId();
        assertNotNull(savedId, "Image id must be set");

        service.deleteFromPost(POST_MAZDA3.getId(), DEALER.getId());

        User persistedUser = userService.findWithPosts(DEALER.getId());
        assertNull(persistedUser.getPosts().iterator().next().getImage());
    }

    @Test
    void deleteFromAnotherUserPost() {
        Image imageToUpload = testHelper.newEntity();
        Integer savedId = service.uploadToPost(POST_MAZDA3.getId(), DEALER.getId(), imageToUpload).getId();
        assertNotNull(savedId, "Image id must be set");

        var thrown = assertThrows(IllegalRequestDataException.class, () -> service.deleteFromPost(POST_MAZDA3.getId(), USER.getId()));
        assertEquals("Wrong entity(id=" + DEALER.getId() + "), must be with id=" + USER.getId(), thrown.getMessage());
    }
}
