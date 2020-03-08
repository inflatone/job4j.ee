<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<jsp:include page="common/resources.jsp"/>
<body>

<script type="text/javascript" src="static/js/common/basic.js" defer></script>
<script type="text/javascript" src="static/js/common/form.js" defer></script>

<script type="text/javascript" src="static/js/login.js" defer></script>
<script type="text/javascript" src="static/js/formUser.js" defer></script>

<div class="container h-100">
    <div class="row h-100 justify-content-center align-items-center">
        <form:form id="login_form" action="security" method="post" class="border border-light w-50">
            <div class="row">
                <div class="col-sm-4">
                    <div class="listing-row__title"><strong>Login</strong></div>
                </div>
                <div class="col-sm-8">
                    <small class="text-muted tip-text">Enter your credentials below</small>
                </div>
            </div>

            <div class="form-group">
                <label for="authLogin"></label>
                <input type="text" id="authLogin" class="form-control" placeholder="Username" name="username">
            </div>

            <div class="form-group">
                <label for="authPassword"></label>
                <input type="password" id="authPassword" class="form-control" placeholder="Password" name="password">
            </div>
            <button class="btn btn-outline-success btn-block my-1" type="submit" id="loginButton">Get in</button>
            <button class="btn btn-outline-secondary btn-block my-1" type="button" onclick="openUserCreateForm()">Sign
                up
            </button>
            <div class="header-group btn-group d-flex" role="group">
                <button class="btn btn-outline-dark btn-sm" type="button" onclick="loginAs('user', 'password')">
                    Try as User
                </button>
                <button class="btn btn-outline-danger btn-sm" type="button" onclick="loginAs('dealer', 'dealer')">
                    Try as Admin
                </button>
            </div>
        </form:form>
    </div>
</div>

<jsp:include page="common/formUser.jsp"/>

<script type="text/javascript">
    const alertMessage = '${param.error}' ? '${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}' : undefined;
</script>

</body>
</html>