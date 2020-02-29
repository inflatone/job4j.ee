package ru.job4j.ee.store.web.view;

import ru.job4j.ee.store.model.User;
import ru.job4j.ee.store.service.UserService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import static ru.job4j.ee.store.service.UserService.getUserService;

/**
 * Represents view layer of app that is responsible for rendering of user list page
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-06
 */
public class UserListServlet extends HttpServlet {
    public static final ThreadLocal<SimpleDateFormat> FORMATTER
            = ThreadLocal.withInitial(() -> new SimpleDateFormat("d.MM.yyyy H:mm:ss"));

    private final UserService service = getUserService();

    /**
     * Composes USER LIST html page code, then prints it to the response object
     *
     * @param request  request
     * @param response response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<User> users = service.findAll();
        StringBuilder html = new StringBuilder()
                .append("<html>")
                .append("<head>")
                .append("<meta charset=\"UTF-8\">")
                .append("<title>User List</title>")
                .append("</head>")
                .append("<body>")
                .append("<h3>User List</h3>")
                .append("<a href=\"").append(request.getContextPath()).append("/create\">Add new user</a>")
                .append("<br/><br/>")
                .append("<table border=\"1\" cellpadding=\"8\" cellspacing=\"0\">")
                .append("<thead>")
                .append("<tr>")
                .append("<th>Login</th>")
                .append("<th>Name</th>")
                .append("<th>Password</th>")
                .append("<th>Created</th>")
                .append("<th></th>")
                .append("<th></th>")
                .append("</tr>")
                .append("</thead>");
        users.forEach(u -> addUserRow(html, u, request.getContextPath()));
        html.append("</table>");
        html.append("</body>")
                .append("</html>");
        response.getWriter().println(html.toString());
    }

    /**
     * Appends to the given html string builder a single user info table row
     *
     * @param html    given html code builder
     * @param user    appending user model
     * @param ctxPath servlet context path
     */
    private void addUserRow(StringBuilder html, User user, String ctxPath) {
        html
                .append("<tr>")
                .append("<td>").append(user.getLogin()).append("</td>")
                .append("<td>").append(user.getName()).append("</td>")
                .append("<td>").append(user.getPassword()).append("</td>")
                .append("<td>").append(FORMATTER.get().format(user.getCreated())).append("</td>");
        appendEdit(html, user.getId(), ctxPath);
        appendRemove(html, user.getId(), ctxPath);
        html.append("</tr>");
    }

    /**
     * Appends to the given html string builder a button html code of edit entity associated with the given id
     *
     * @param html    given html code builder
     * @param id      entity id
     * @param ctxPath servlet context path
     */
    private void appendEdit(StringBuilder html, int id, String ctxPath) {
        appendItemButton(html, id, ctxPath + "/edit", "Edit", "", "get");
    }

    /**
     * Appends to the given html string builder a button html code of remove entity associated with the given id
     *
     * @param html    given html code builder
     * @param id      entity id
     * @param ctxPath servlet context path
     */
    private void appendRemove(StringBuilder html, int id, String ctxPath) {
        appendItemButton(html, id, ctxPath + "/users", "Delete", "delete", "post");
    }

    /**
     * Appends to the given html string builder a button html code of edit/remove entity associated with the given id
     *
     * @param html        given html code builder
     * @param id          entity id
     * @param link        link where the button click have to send
     * @param description text signed on the button
     * @param action      action that have to be appended to the given button link path
     * @param method      method of the server request of the button click
     */
    private void appendItemButton(StringBuilder html, int id, String link, String description, String action, String method) {
        html
                .append("<td>")
                .append("<form action=\"").append(link).append("\" method=\"").append(method).append("\">")
                .append("  <input type=\"hidden\" name=\"id\" value=\"").append(id).append("\"/>")
                .append("  <input type=\"hidden\" name=\"action\" value=\"").append(action).append("\"/>")
                .append("  <button type=\"submit\">").append(description).append("</button>")
                .append("</form>")
                .append("</td>");
    }
}