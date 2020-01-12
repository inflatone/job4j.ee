package ru.job4j.ee.store.web;

import com.google.inject.Inject;
import org.junit.jupiter.api.Test;
import ru.job4j.ee.store.repository.UserImageRepository;
import ru.job4j.ee.store.web.mock.WebMock;

import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static ru.job4j.ee.store.AssertionUtil.*;
import static ru.job4j.ee.store.web.json.JsonUtil.asJson;
import static ru.job4j.ee.store.web.mock.WebMock.GET_METHOD;
import static ru.job4j.ee.store.web.mock.WebMock.POST_METHOD;

class UserImageServletTest extends AbstractServletTest {
    private static final String IMAGES_URL = "/images";

    @Inject
    private UserImageRepository userImageRepository;

    @Test
    void doSave() throws Exception {
        var image = createUserImageModel();
        var mock = WebMock.create()
                .setUp(injector)
                .withUrl(IMAGES_URL)
                .withAuth(USER)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestImage(image)
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_CREATED);

        var imageId = userRepository.find(USER.getId()).getImage().getId();
        var persisted = userImageRepository.find(imageId);

        assertMatch(persisted, image);

        image.setId(imageId);
        mock.checkResponseBody(asJson(image))
                .terminate();
    }

    @Test
    void doSaveAsAdmin() throws Exception {
        var image = createUserImageModel();
        var mock = WebMock.create()
                .setUp(injector)
                .withUrl(IMAGES_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestParameter("userId", USER.getId())
                .withRequestImage(image)
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_CREATED);

        var imageId = userRepository.find(USER.getId()).getImage().getId();
        var persisted = userImageRepository.find(imageId);

        assertMatch(persisted, image);

        image.setId(imageId);
        mock.checkResponseBody(asJson(image))
                .terminate();
    }

    @Test
    void doSaveEmpty() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(IMAGES_URL)
                .withAuth(USER)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestParameter("userId", USER.getId())
                .withRequestImage(null)
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_FORBIDDEN)
                .checkResponseBody(asJson("error", "Image must not be null"))
                .terminate();
    }

    @Test
    void doSaveAnotherFieldName() throws Exception {
        var image = createUserImageModel();
        WebMock.create()
                .setUp(injector)
                .withUrl(IMAGES_URL)
                .withAuth(USER)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestImage(image, "field")
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_FORBIDDEN)
                .checkResponseBody(asJson("error", "Image must not be null"))
                .terminate();
    }

    @Test
    void doPostWithExceptionHandling() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(IMAGES_URL)
                .withAuth(USER)
                .withMethod(POST_METHOD)
                .withAction("save")
                .withRequestParameter("userId", USER.getId())
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_FORBIDDEN)
                .checkResponseBody(asJson("error", "the request doesn't contain a multipart/form-data or multipart/mixed stream, content type header is null"))
                .terminate();
    }

    @Test
    void doGet() throws Exception {
        var image = createUserImageModel();
        userImageRepository.save(image);
        WebMock.create()
                .setUp(injector)
                .withUrl(IMAGES_URL)
                .withAuth(ADMIN)
                .withMethod(GET_METHOD)
                .withRequestParameter("id", image.getId())
                .withBinaryAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseHeader("Content-Type", image.getContentType())
                .checkResponseHeader("Content_Length", image.getSize())
                .checkResponseHeader("Content-Disposition", image.getName())
                .checkResponseBinaryBody(TEST_BYTES)
                .terminate();
    }

    @Test
    void doGetNoImage() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(IMAGES_URL)
                .withAuth(ADMIN)
                .withMethod(GET_METHOD)
                .withRequestParameter("id", 0)
                .withForwardExpected()
                .act()
                .checkForwardPath("resources/images/noImage.jpg")
                .terminate();
    }

    @Test
    void doGetWithoutId() throws Exception {
        WebMock.create()
                .setUp(injector)
                .withUrl(IMAGES_URL)
                .withAuth(ADMIN)
                .withMethod(GET_METHOD)
                .withForwardExpected()
                .act()
                .checkForwardPath("resources/images/noImage.jpg")
                .terminate();
    }

    @Test
    void doRemove() throws Exception {
        var image = createUserImageModel();
        assertTrue(userImageRepository.addToUser(image, USER.getId()));
        WebMock.create()
                .setUp(injector)
                .withUrl(IMAGES_URL)
                .withAuth(USER)
                .withMethod(POST_METHOD)
                .withAction("delete")
                .withRequestParameter("id", image.getId())
                .withRequestParameter("userId", USER.getId())
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT)
                .terminate();
        assertNull(userImageRepository.find(image.getId()));
    }

    @Test
    void doRemoveAsAdmin() throws Exception {
        var image = createUserImageModel();
        assertTrue(userImageRepository.addToUser(image, USER.getId()));

        WebMock.create()
                .setUp(injector)
                .withUrl(IMAGES_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("delete")
                .withRequestParameter("id", image.getId())
                .withRequestParameter("userId", USER.getId())
                .act()
                .checkResponseStatus(HttpServletResponse.SC_NO_CONTENT)
                .terminate();
        assertNull(userImageRepository.find(image.getId()));
    }

    @Test
    void clearCountries() throws Exception {
        var image = createUserImageModel();
        userImageRepository.save(image);

        var stored = userImageRepository.find(image.getId());
        assertNotNull(stored);
        stored.getData().close();

        WebMock.create()
                .setUp(injector)
                .withUrl(IMAGES_URL)
                .withAuth(ADMIN)
                .withMethod(POST_METHOD)
                .withAction("clear")
                .withAnswerExpected()
                .act()
                .checkResponseStatus(HttpServletResponse.SC_OK)
                .checkResponseBody(asJson("message", "1 images removed"))
                .terminate();

        stored = userImageRepository.find(image.getId());
        assertNull(stored);
    }
}
