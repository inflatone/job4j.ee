<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<jsp:include page="fragments/header.jsp"/>
<body>
<section>
    <jsp:include page="fragments/bodyHeader.jsp"/>

    <h2>${param.action == 'create' ? 'Create' : 'Edit'} user</h2>
    <jsp:useBean id="user" type="ru.job4j.ee.store.model.User" scope="request"/>
    <img src="images?id=${user.image.id}" alt="userpic" width="400">
    <form action="images" method="post">
        <input type="hidden" name="id" value="${user.image.id}">
        <input type="hidden" name="userId" value="${user.id}">
        <input type="hidden" name="action" value="delete">
        <button type="submit" ${user.image.id == null ? "disabled" : ""}>Remove</button>
    </form>

    <form action="users" method="post" enctype="multipart/form-data">
        <input type="hidden" name="id" value="${user.id}"/>
        <input type="hidden" name="action" value="${param.action}"/>
        <input type="hidden" name="imageId" value="${user.image.id}">
        <dl>
            <dt>Username:</dt>
            <dd><input type="text" size="40" name="login" value="${user.login}"/></dd>
        </dl>
        <dl>
            <dt>Name:</dt>
            <dd><input type="text" size="40" name="name" value="${user.name}"/></dd>
        </dl>
        <dl>
            <dt>Password:</dt>
            <dd><input type="password" size="40" name="password" value="${user.password}"/></dd>
        </dl>
        <dl>
            <dt>Userpic:</dt>
            <dd><input type="file" name="image"/></dd>
        </dl>
        <button type="submit">Save</button>
        <button type="button" onclick="window.history.back()">Cancel</button>
    </form>
</section>
</body>
</html>