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
    <form action="users" method="post">
        <input type="hidden" name="id" value="${user.id}"/>
        <input type="hidden" name="action" value="${param.action}"/>
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
        <button type="submit">Save</button>
        <button type="button" onclick="window.history.back()">Cancel</button>
    </form>
</section>
</body>
</html>