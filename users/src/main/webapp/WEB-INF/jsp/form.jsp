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

    <h2>${param.action == 'create' ? 'Registration' : ' Profile '}</h2>
    <c:if test="${user.id != null}">
        <img src="images?id=${user.image.id}" alt="userpic" width="400">
        <form action="images" method="post">
            <input type="hidden" name="id" value="${user.image.id}">
            <input type="hidden" name="userId" value="${user.id}">
            <input type="hidden" name="action" value="delete">
            <button type="submit" ${user.image.id == null ? "disabled" : ""}>Remove</button>
        </form>
    </c:if>

    <form action="${fn:isAdmin(pageContext.request) ? 'users' : 'profile'}" method="post" enctype="multipart/form-data">
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
            <dt>Role:</dt>
            <dd>
                <select name="role" ${fn:isAdmin(pageContext.request) ? '' : 'disabled'}>
                    <jsp:useBean id="roles" scope="request" type="java.util.Set"/>
                    <c:forEach items="${roles}" var="role">
                        <jsp:useBean id="role" type="ru.job4j.ee.store.model.Role" scope="page"/>
                        <option value="${role}" ${role == user.role ? "selected" : ""} >${role}</option>
                    </c:forEach>
                </select>
            </dd>
        </dl>
        <dl>
            <dt>Userpic:</dt>
            <dd><input type="file" name="image"/></dd>
        </dl>
        <button type="submit">Save</button>
        <button type="reset">Reset</button>
        <button type="button" onclick="window.history.back()">Cancel</button>
    </form>
</section>
</body>
</html>