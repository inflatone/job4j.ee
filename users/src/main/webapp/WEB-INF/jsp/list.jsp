<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://junior.job4j.ru/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<jsp:include page="fragments/header.jsp"/>
<body>
<section>
    <h2>All users</h2>
    <a href="users?action=create">Add user</a>
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
        <jsp:useBean id="users" scope="request" type="java.util.List"/>
        <c:forEach items="${users}" var="user">
            <jsp:useBean id="user" type="ru.job4j.ee.store.model.User" scope="page"/>
            <tr>
                <td>${user.login}</td>
                <td>${user.name}</td>
                <td>${user.password}</td>
                <td>${fn:formatDate(user.created)}</td>
                <td>
                    <form action="" method="get">
                        <input type="hidden" name="id" value="${user.id}"/>
                        <input type="hidden" name="action" value="update"/>
                        <button type="submit">Edit</button>
                    </form>
                </td>
                <td>
                    <form action="" method="post">
                        <input type="hidden" name="id" value="${user.id}"/>
                        <input type="hidden" name="action" value="delete"/>
                        <button type="submit">Remove</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>

</section>
</body>
</html>