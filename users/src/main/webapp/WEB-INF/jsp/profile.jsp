<jsp:useBean id="authUser" scope="session" type="ru.job4j.ee.store.model.User"/>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://junior.job4j.ru/functions" %>
<html>
<jsp:include page="fragments/header.jsp"/>
<body>
<section>
    <jsp:include page="fragments/bodyHeader.jsp"/>
    <jsp:useBean id="user" type="ru.job4j.ee.store.model.User" scope="request"/>
    <h2>Profile</h2>
    <table border="0">
        <thead>
        <tr>
            <th>Key</th>
            <th>Value</th>
        </tr>
        </thead>
        <tr>
            <td>Userpic:</td>
            <td><img src="images?id=${user.image.id}" alt="userpic" width="400"></td>
        </tr>
        <tr>
            <td>Login:</td>
            <td>${user.login}</td>
        </tr>
        <tr>
            <td>Name:</td>
            <td>${user.name}</td>
        </tr>
        <tr>
            <td>Password:</td>
            <td>${user.password}</td>
        </tr>
        <tr>
            <td>Created:</td>
            <td>${fn:formatDate(user.created)}</td>
        </tr>
        <tr>
            <td>Role:</td>
            <td>${user.role}</td>
        </tr>
        <tr>
            <td>Enabled:</td>
            <td><input type="checkbox" ${user.enabled ? 'checked' : ''} disabled></td>
        </tr>
    </table>
    <p>
    <form action="" method="get">
        <input type="hidden" name="id" value="${user.id}"/>
        <input type="hidden" name="action" value="update"/>
        <button type="submit">Edit profile</button>
    </form>

    <form action="" method="post">
        <input type="hidden" name="id" value="${user.id}"/>
        <input type="hidden" name="action" value="delete"/>
        <button type="submit">Remove profile</button>
    </form>

    <button type="button" onclick="window.location.href='logout'">Sign out</button>
    </p>
</section>
</body>
</html>