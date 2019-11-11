<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<jsp:include page="fragments/header.jsp"/>
<body>
<jsp:include page="fragments/bodyHeader.jsp"/>
<section>
    <a href="profile?action=create">Sign up</a>
    <h2>Sign in</h2>
    <form action="" method="post">
        <dl>
            <dt>Username:</dt>
            <dd><input type="text" size="40" name="login"/></dd>
        </dl>
        <dl>
            <dt>Password:</dt>
            <dd><input type="password" size="40" name="password"/></dd>
        </dl>
        <button type="submit">Sign in</button>
        <button type="button" onclick="window.history.back()">Cancel</button>
    </form>
</section>
</body>
</html>