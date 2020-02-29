package ru.job4j.ee.store.web.view;

import ru.job4j.ee.store.model.User;
import ru.job4j.ee.store.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.job4j.ee.store.service.UserService.getUserService;
import static ru.job4j.ee.store.util.ServletUtil.getRequiredId;

/**
 * Represents view layer of app that is responsible for rendering of user update page
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-06
 */
public class UserUpdateServlet extends HttpServlet {
    private final UserService service = getUserService();

    /**
     * Redirects the given request to the action dispatching servlet appending the UPDATE action
     *
     * @param request  request
     * @param response response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/users?action=update").forward(request, response);
    }

    /**
     * Composes USER UPDATE html page code, then prints it to the response object
     *
     * @param request  request
     * @param response response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = getRequiredId(request);
        User user = service.find(id);
        StringBuilder html = new StringBuilder()
                .append("<html>")
                .append("<head>")
                .append("<meta charset=\"UTF-8\">")
                .append("<title>User List</title>")
                .append("</head>")
                .append("<body>")
                .append("<h3>User Update</h3>");
        addUserForm(html, request.getContextPath() + "/edit", user);
        html.append("</body>")
                .append("</html>");
        response.getWriter().println(html.toString());
    }

    /**
     * Appends to the given html string builder an USER UPDATE form html code
     *
     * @param html given html code builder
     * @param link link where the form has to be send
     */
    private void addUserForm(StringBuilder html, String link, User user) {
        html
                .append("<form action=\"").append(link).append("\" method=\"post\">")
                .append("    <input type=\"hidden\" name=\"id\" value=\"").append(user.getId()).append("\"/>")
                .append("    <input type=\"hidden\" name=\"date\" value=\"").append(user.getCreated()).append("\"/>")
                .append("    </dl>")
                .append("        <dt>Username:</dt>")
                .append("        <dd><input type=\"text\" size=\"40\" name=\"login\" value=\"").append(user.getLogin()).append("\"/></dd>")
                .append("    </dl>")
                .append("    <dl>")
                .append("        <dt>Name:</dt>")
                .append("        <dd><input type=\"text\" size=\"40\" name=\"name\" value=\"").append(user.getName()).append("\"/></dd>")
                .append("    </dl>")
                .append("    <dl>")
                .append("        <dt>Password:</dt>")
                .append("        <dd><input type=\"password\" size=\"40\" name=\"password\" value=\"").append(user.getPassword()).append("\"/></dd>")
                .append("    </dl>")
                .append("    <button type=\"submit\">Save</button>")
                .append("    <button type=\"button\" onclick=\"window.history.back()\">Cancel</button>")
                .append("</form>");
    }
}