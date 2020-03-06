<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<jsp:include page="common/header.jsp"/>
<body>

<script type="text/javascript" src="resources/js/common.js" defer></script>
<script type="text/javascript" src="resources/js/login.js" defer></script>
<script type="text/javascript" src="resources/js/form.js" defer></script>
<script type="text/javascript" src="resources/js/formUser.js" defer></script>

<button type="button" onclick="loginAs('user', 'password')">Login as user</button>
<button type="button" onclick="loginAs('dealer', 'dealer')">Login as admin</button>

<div class="container h-100">
    <div class="row h-100 justify-content-center align-items-center">
        <form id="login_form" action="security" method="post" class="border border-light w-50 " novalidate>
            <h3 style="text-align: center">Sign in: </h3>
            <p class="h6" style="text-align: center">Enter your credentials below</p>
            <c:if test="${param.error}">
                <div class="errorMessage">${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}</div>
            </c:if>
            <div class="form-group">
                <label for="authLogin"></label>
                <input type="text" id="authLogin" class="form-control" placeholder="Username" name="username">
            </div>

            <div class="form-group">
                <label for="authPassword"></label>
                <input type="password" id="authPassword" class="form-control" placeholder="Password" name="password">
            </div>

            <button class="btn btn-info btn-block my-1" type="submit" id="loginButton">Get in</button>
            <button class="btn btn-secondary btn-block my-1" type="button" onclick="openCreateForm()">Sign up</button>
        </form>
    </div>
</div>

<jsp:include page="common/formUser.jsp"/>

</body>
</html>