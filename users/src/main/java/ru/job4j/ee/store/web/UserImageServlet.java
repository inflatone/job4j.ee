package ru.job4j.ee.store.web;

import com.google.inject.Inject;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import ru.job4j.ee.store.model.UserImage;
import ru.job4j.ee.store.service.UserImageService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.job4j.ee.store.util.ServletUtil.getId;
import static ru.job4j.ee.store.util.ServletUtil.getRequiredId;
import static ru.job4j.ee.store.web.Action.*;
import static ru.job4j.ee.store.web.auth.AuthUtil.getAuthUser;
import static ru.job4j.ee.store.web.auth.AuthUtil.retrieveIfAdminOrCheckSession;
import static ru.job4j.ee.store.web.json.JsonUtil.asJsonToResponse;

/**
 * Represents web layer of the app that serves user image data requests
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-10
 */
public class UserImageServlet extends ActionDispatcherServlet {
    private static final Logger log = getLogger(UserImageServlet.class);

    @Inject
    private UserImageService service;

    @Override
    protected void fillPostActions() {
        submitPostAction(delete, this::doRemove);
        submitPostAction(save, this::doSave);
        submitPostAction(clear, this::doClear);
    }

    @Override
    protected void fillGetActions() {
        submitGetAction(empty, this::doFind);
        // may be extended in future: add showing preview-thumbnails instead of original-sized images all the time
    }

    /**
     * Tries to find the image associated with the given id parameter, returns image data if success,
     * otherwise sends redirect to default 'NO IMAGE' picture
     *
     * @param request  request
     * @param response response
     */
    // show image servlet https://o7planning.org/en/11067/displaying-image-from-database-with-java-servlet
    private void doFind(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var image = findImage(request);
        if (image == null) {
            sendRedirectToEmptyImage(request, response);
        } else {
            writeImageToResponse(response, image);
        }
    }

    /**
     * Retrieves the user id parameter from the request parameter map,
     * then requests the image associated with it from image service
     *
     * @param request request
     * @return user image data, or {@code null} if not found
     */
    private UserImage findImage(HttpServletRequest request) {
        Integer id = getId(request, false);
        return id == null ? null : service.find(id);
    }

    /**
     * Writes the given image data to the response object
     *
     * @param response response
     * @param image    image data
     */
    private void writeImageToResponse(HttpServletResponse response, UserImage image) throws IOException {
        try (var in = image.getData()) {
            response.setHeader("Content-Type", image.getContentType());
            response.setHeader("Content_Length", String.valueOf(image.getSize()));
            response.setHeader("Content-Disposition", "inline; filename=\"" + image.getName() + '"');
            in.transferTo(response.getOutputStream());
        }
    }

    /**
     * Includes the logic of redirecting the given request to empty image data
     *
     * @param request  request
     * @param response response
     */
    private void sendRedirectToEmptyImage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("resources/images/noImage.jpg").forward(request, response);
    }

    /**
     * Represents CLEAR UNUSED executor
     *
     * @param request  request
     * @param response response
     */
    private void doClear(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("Clear unused images");
        int amount = service.clear();
        asJsonToResponse(response, "message", amount + " images removed");
    }

    /**
     * Represents DELETE IMAGE executor
     *
     * @param request request
     */
    void doRemove(HttpServletRequest request, HttpServletResponse response) {
        int id = getRequiredId(request);
        int userId = getRequiredUserId(request);
        log.info("Delete image {} of user with id {}", id, userId);
        service.delete(id, userId);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    /**
     * Represents CREATE/UPDATE USER IMAGE executor
     *
     * @param request  request
     * @param response response
     */
    void doSave(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var userId = getRequiredUserId(request);
        var image = extractUserImage(request);
        log.info("Save new image of user with id {}", userId);
        service.save(image, userId);
        response.setStatus(HttpServletResponse.SC_CREATED);
        asJsonToResponse(response, image);
    }

    /**
     * Retrieves image data from the given request
     *
     * @param request request
     * @return image data
     */
    private UserImage extractUserImage(HttpServletRequest request) throws IOException {
        final var upload = new ServletFileUpload();
        // https://commons.apache.org/proper/commons-fileupload/streaming.html
        try {
            final var itemIterator = upload.getItemIterator(request);
            while (itemIterator.hasNext()) {
                var fileItemStream = itemIterator.next();
                if (!fileItemStream.isFormField() && "image".equals(fileItemStream.getFieldName())) {
                    return new UserImage(fileItemStream.getName(), fileItemStream.getContentType(), 0, fileItemStream.openStream());
                }
            }
        } catch (FileUploadException e) {
            throw new IllegalStateException("Cannot upload image", e);
        }
        return null;
    }

    /**
     * Retrieves the item id parameter from the given request's context if user has ADMIN role,
     * from the authorized user data if user has NO ADMIN role, throws NPE if unauthorized call suspected
     *
     * @param request request
     * @return user id
     */
    private int getRequiredUserId(HttpServletRequest request) {
        Integer userId = retrieveIfAdminOrCheckSession(request, "userId", Integer::parseInt);
        return userId != null ? userId : getAuthUser(request, true).getId(); // users can delete only own photos
    }
}