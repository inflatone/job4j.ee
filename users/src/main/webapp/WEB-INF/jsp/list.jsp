<%@ page import="ru.job4j.ee.store.model.User" %>
<%@ page import="static ru.job4j.ee.store.util.ServletUtil.FORMATTER" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<jsp:include page="fragments/header.jsp"/>
<body>
<section>
    <h2>All users</h2>
    <a href="<%=request.getContextPath()%>/users?action=create">Add user</a>
    <br/><br/>
    <table>
        <thead>
        <tr>
            <th>Login</th>
            <th>Name</th>
            <th>Password</th>
            <th>Created</th>
            <th></th>
            <th></th>
        </tr>
        </thead>
        <% for (User user : (List<User>) request.getAttribute("users")) {%>
        <tr>
            <td><%=user.getLogin() %></td>
            <td><%=user.getName() %></td>
            <td><%=user.getPassword() %></td>
            <td><%=FORMATTER.get().format(user.getCreated())%></td>
            <td>
                <form action="<%=request.getContextPath()%>/users" method="get">
                    <input type="hidden" name="id" value="<%=user.getId()%>"/>
                    <input type="hidden" name="action" value="update" />
                    <button type="submit">Edit</button>
                </form>
            </td>
            <td>
                <form action="<%=request.getContextPath()%>/users" method="post">
                    <input type="hidden" name="id" value="<%=user.getId()%>"/>
                    <input type="hidden" name="action" value="delete" />
                    <button type="submit">Remove</button>
                </form>
            </td>
        </tr>
        <%}%>
    </table>

</section>
</body>
</html>