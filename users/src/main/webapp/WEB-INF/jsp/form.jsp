<%@ page import="ru.job4j.ee.store.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<jsp:include page="fragments/header.jsp"/>
<body>
<section>
    <jsp:include page="fragments/bodyHeader.jsp"/>
    <%
        User user = (User) request.getAttribute("user");
        String action = request.getParameter("action");
    %>
    <h2><%="create".equals(action) ? "Create" : "Update"%> user</h2>

    <form action="<%=request.getContextPath()%>/users" method="post">
        <input type="hidden" name="id" value="<%=user.getId()%>"/>
        <input type="hidden" name="action" value="<%=action%>"/>
        <dl>
            <dt>Username:</dt>
            <dd><input type="text" size="40" name="login" value="<%=user.getLogin()%>"/></dd>
        </dl>
        <dl>
            <dt>Name:</dt>
            <dd><input type="text" size="40" name="name" value="<%=user.getName()%>"/></dd>
        </dl>
        <dl>
            <dt>Password:</dt>
            <dd><input type="password" size="40" name="password" value="<%=user.getPassword()%>"/></dd>
        </dl>
        <button type="submit">Save</button>
        <button type="button" onclick="window.history.back()">Cancel</button>
    </form>
</section>
</body>
</html>