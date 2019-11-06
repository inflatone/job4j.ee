package ru.job4j.ee.store.web.view;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Represents view layer of app that is responsible for rendering of user create page
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-06
 */
public class UserCreateServlet extends HttpServlet {

    /**
     * Redirects the given request to the action dispatching servlet appending the CREATE action
     *
     * @param request  request
     * @param response response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/users?action=create").forward(request, response);
    }

    /**
     * Composes USER CREATE html page code, then prints it to the response object
     *
     * @param request  request
     * @param response response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder html = new StringBuilder()
                .append("<html>")
                .append("<head>")
                .append("<meta charset=\"UTF-8\">")
                .append("<title>User List</title>")
                .append("</head>")
                .append("<body>")
                .append("<h3>User Create</h3>");
        addUserForm(html, request.getContextPath() + "/create");
        html.append("</body>")
                .append("</html>");
        response.getWriter().println(html.toString());
    }

    /**
     * Appends to the given html string builder an USER CREATE form html code
     *
     * @param html given html code builder
     * @param link link where the form has to be send
     */
    private void addUserForm(StringBuilder html, String link) {
        html
                .append("<form action=\"").append(link).append("\" method=\"post\">")
                .append("    <dl>")
                .append("        <dt>Username:</dt>")
                .append("        <dd><input type=\"text\" size=\"40\" name=\"login\"/></dd>")
                .append("    </dl>")
                .append("    <dl>")
                .append("        <dt>Name:</dt>")
                .append("        <dd><input type=\"text\" size=\"40\" name=\"name\"/></dd>")
                .append("    </dl>")
                .append("    <dl>")
                .append("        <dt>Password:</dt>")
                .append("        <dd><input type=\"password\" size=\"40\" name=\"password\"/></dd>")
                .append("    </dl>")
                .append("    <button type=\"submit\">Save</button>")
                .append("    <button type=\"button\" onclick=\"window.history.back()\">Cancel</button>")
                .append("</form>");
    }
}