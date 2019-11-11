package ru.job4j.ee.store.web;

import org.slf4j.Logger;
import ru.job4j.ee.store.model.Role;
import ru.job4j.ee.store.model.UserImage;
import ru.job4j.ee.store.service.UserImageService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.job4j.ee.store.service.UserImageService.getUserImageService;
import static ru.job4j.ee.store.util.ServletUtil.*;
import static ru.job4j.ee.store.web.Action.delete;
import static ru.job4j.ee.store.web.auth.AuthUtil.getAuthUser;
import static ru.job4j.ee.store.web.auth.AuthUtil.redirectToMain;

/**
 * Represents web layer of the app that serves user image data requests
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-10
 */
public class UserImageServlet extends DispatcherServlet {
    private static final Logger log = getLogger(UserImageServlet.class);

    private final UserImageService service = getUserImageService();

    @Override
    protected void sendRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirectToMain(request, response);
    }

    @Override
    protected void fillPostActions() {
        submitPostAction(delete, this::doRemove);
    }

    @Override
    protected void fillGetActions() {
        // no need to dispatch GET requests, doGet() is overridden here
        // may be extended in future: add showing preview-thumbnails instead of original-sized images all the time
    }

    /**
     * Tries to find the image associated with the given id parameter, returns image data if success,
     * otherwise sends redirect to default 'NO IMAGE' picture
     *
     * @param request  request
     * @param response response
     */
    @Override
    // show image servlet https://o7planning.org/en/11067/displaying-image-from-database-with-java-servlet
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        // id == 0 — repository returns it when no user image binding was found in DB
        return id == null || id == 0 ? null : service.find(id);
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
     * Represents DELETE IMAGE executor
     *
     * @param request request
     */
    void doRemove(HttpServletRequest request) {
        int id = getRequiredId(request);
        int userId = getRequiredUserId(request);
        log.info("Delete image {} of user with id {}", id, userId);
        service.delete(id, userId);
    }

    /**
     * Retrieves the item id parameter from the given request's context if user has ADMIN role,
     * from the authorized user data if user has NO ADMIN role, throws NPE if unauthorized call suspected
     *
     * @param request request
     * @return user id
     */
    private int getRequiredUserId(HttpServletRequest request) {
        var authUser = getAuthUser(request, true);
        return authUser.getRole() == Role.ADMIN ? getRequiredParameter(request, "userId", Integer::valueOf)
                : authUser.getId(); // users can delete only own photos
    }
}