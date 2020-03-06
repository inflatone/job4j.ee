<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://junior.job4j.ru/functions" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<c:if test="${fn:isAdmin(pageContext.request)}">
    <h3><a href="users">Users</a></h3>
    <hr/>
</c:if>
<div class="error"><h2>${requestScope.error}</h2></div>