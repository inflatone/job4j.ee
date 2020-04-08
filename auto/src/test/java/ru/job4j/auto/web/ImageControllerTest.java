package ru.job4j.auto.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.auto.config.helper.ImageEntityTestHelper;
import ru.job4j.auto.config.helper.ToTestHelpers.ImageToTestHelper;
import ru.job4j.auto.model.Image;
import ru.job4j.auto.repository.ImageRepository;
import ru.job4j.auto.service.ImageService;
import ru.job4j.auto.service.PostService;
import ru.job4j.auto.service.UserService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.job4j.auto.TestModelData.*;
import static ru.job4j.auto.web.AbstractControllerTest.RequestWrapper.wrap;

class ImageControllerTest extends AbstractControllerTest {
    private final ImageEntityTestHelper testHelper;

    private final ImageToTestHelper toTestHelper;

    private final ImageService service;

    private final UserService userService;

    private final PostService postService;

    @Autowired
    public ImageControllerTest(ImageEntityTestHelper testHelper, ImageToTestHelper toTestHelper, ImageService service,
                               UserService userService, PostService postService) {
        super(ImageController.URL);
        this.testHelper = testHelper;
        this.toTestHelper = toTestHelper;
        this.service = service;
        this.userService = userService;
        this.postService = postService;
    }

    @Test
    void find(@Autowired ImageRepository repository) throws Exception {
        Image newImage = repository.save(testHelper.newEntity());
        perform(doGet(newImage.getId()).auth(USER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJpg())
                .andExpect(testHelper.contentImage(newImage));
    }

    @Test
    void findDefault() throws Exception {
        perform(doGet().auth(USER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJpg())
                .andExpect(testHelper.contentDefaultImage());

    }

    @Test // need to flush user entity for its image id to be inserted
    @Transactional(propagation = Propagation.NEVER)
    void uploadToUser() throws Exception {
        Image newImage = testHelper.newEntity();
        perform(userMultipart().auth(USER).attachImage("userPhoto", newImage))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(contentTypeIsJson())
                .andExpect(toTestHelper.forAuth(USER).contentJson(newImage));

        assertNotNull(userService.find(USER.getId()).getImage().getId(), "User image must be set");
        service.deleteFromUser(USER.getId()); // clean the test dirt
    }

    @Test
    void uploadEmpty() throws Exception {
        Image newImage = testHelper.newEntity();
        perform(userMultipart().auth(USER).attachImage("anotherPhoto", newImage))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        assertNull(userService.find(USER.getId()).getImage(), "Image must not be set");
    }

    @Test
    void uploadToAnotherUser() throws Exception {
        Image newImage = testHelper.newEntity();
        perform(userMultipart(DEALER.getId()).auth(USER).attachImage("userPhoto", newImage))
                .andDo(print())
                .andExpect(status().isForbidden());
        assertNull(userService.find(DEALER.getId()).getImage(), "Image must not be set");
    }

    @Test // need to flush user entity for its image id to be inserted
    @Transactional(propagation = Propagation.NEVER)
    void uploadToAnotherUserAsAdmin() throws Exception {
        Image newImage = testHelper.newEntity();
        perform(userMultipart(USER.getId()).auth(DEALER).attachImage("userPhoto", newImage))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(contentTypeIsJson())
                .andExpect(toTestHelper.forAuth(DEALER).contentJson(newImage, USER.getId()));

        assertNotNull(userService.find(USER.getId()).getImage(), "User image must be set");
        service.deleteFromUser(USER.getId()); // clean the test dirt
    }


    @Test // need to flush post entity for its image id to be inserted
    @Transactional(propagation = Propagation.NEVER)
    void uploadToPost() throws Exception {
        Image newImage = testHelper.newEntity();
        perform(postMultipart(POST_BMW.getId()).auth(USER).attachImage("postPhoto", newImage))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(contentTypeIsJson())
                .andExpect(toTestHelper.forAuth(USER).contentJson(newImage, USER.getId(), POST_BMW.getId()));

        assertNotNull(postService.find(POST_BMW.getId(), USER.getId()).getImage(), "Post image must be set");
        service.deleteFromPost(POST_BMW.getId(), USER.getId()); // clean the test dirt
    }

    @Test
    void uploadToAnotherUserPost() throws Exception {
        Image newImage = testHelper.newEntity();
        perform(postMultipart(DEALER.getId(), POST_MAZDA3.getId()).auth(USER).attachImage("postPhoto", newImage))
                .andDo(print())
                .andExpect(status().isForbidden());
        assertNull(postService.find(POST_MAZDA3.getId(), DEALER.getId()).getImage(), "Image must not be set");
    }

    @Test // need to flush post entity for its image id to be inserted
    @Transactional(propagation = Propagation.NEVER)
    void uploadToAnotherUserPostAsAdmin() throws Exception {
        Image newImage = testHelper.newEntity();
        perform(postMultipart(USER.getId(), POST_BMW.getId()).auth(DEALER).attachImage("postPhoto", newImage))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(contentTypeIsJson())
                .andExpect(toTestHelper.forAuth(DEALER).contentJson(newImage, USER.getId(), POST_BMW.getId()));

        assertNotNull(postService.find(POST_BMW.getId(), USER.getId()).getImage(), "Post image must be set");
        service.deleteFromPost(POST_BMW.getId(), USER.getId()); // clean the test dirt
    }

    @Test
    void deleteFromUser() throws Exception {
        service.uploadToUser(USER.getId(), testHelper.newEntity());

        perform(userDelete().auth(USER))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertNull(userService.find(USER.getId()).getImage(), "User image must not be set");
    }

    @Test
    void deleteFromAnotherUser() throws Exception {
        service.uploadToUser(DEALER.getId(), testHelper.newEntity());

        perform(userDelete(DEALER.getId()).auth(USER))
                .andDo(print())
                .andExpect(status().isForbidden());
        assertNotNull(userService.find(DEALER.getId()).getImage(), "User image must be set");
    }

    @Test
    void deleteFromAnotherUserAsAdmin() throws Exception {
        service.uploadToUser(USER.getId(), testHelper.newEntity());

        perform(userDelete(USER.getId()).auth(DEALER))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertNull(userService.find(USER.getId()).getImage(), "User image must not be set");
    }

    @Test
    void deleteFromPost() throws Exception {
        service.uploadToPost(POST_BMW.getId(), USER.getId(), testHelper.newEntity());

        perform(postDelete(POST_BMW.getId()).auth(USER))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertNull(postService.find(POST_BMW.getId(), USER.getId()).getImage(), "User image must not be set");
    }

    @Test
    void deleteFromAnotherUserPost() throws Exception {
        service.uploadToPost(POST_MAZDA3.getId(), DEALER.getId(), testHelper.newEntity());

        perform(postDelete(DEALER.getId(), POST_MAZDA3.getId()).auth(USER))
                .andDo(print())
                .andExpect(status().isForbidden());
        assertNotNull(postService.find(POST_MAZDA3.getId(), DEALER.getId()).getImage(), "Post image must be set");
    }

    @Test
    void deleteFromAnotherUserPostAsAdmin() throws Exception {
        service.uploadToPost(POST_BMW.getId(), USER.getId(), testHelper.newEntity());

        perform(postDelete(USER.getId(), POST_BMW.getId()).auth(DEALER))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertNull(postService.find(POST_BMW.getId(), USER.getId()).getImage(), "User image must not be set");
    }

    protected RequestWrapper userDelete() {
        return doDelete("profile");
    }

    protected RequestWrapper userDelete(int id) {
        return doDelete("users/{id}", id);
    }

    protected RequestWrapper postDelete(int id) {
        return doDelete("profile/posts/{id}", id);
    }

    protected RequestWrapper postDelete(int rootId, int id) {
        return doDelete("users/{rootId}/posts/{id}", rootId, id);
    }

    protected RequestWrapper userMultipart() {
        return wrap(MockMvcRequestBuilders.multipart(url + "profile"));
    }

    protected RequestWrapper userMultipart(int id) {
        return wrap(MockMvcRequestBuilders.multipart(url + "users/{id}", id));
    }

    protected RequestWrapper postMultipart(int id) {
        return wrap(MockMvcRequestBuilders.multipart(url + "profile/posts/{id}", id));
    }

    protected RequestWrapper postMultipart(int rootId, int id) {
        return wrap(MockMvcRequestBuilders.multipart(url + "users/{rootId}/posts/{id}", rootId, id));
    }
}
