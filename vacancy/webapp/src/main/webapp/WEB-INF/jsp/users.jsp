<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<jsp:include page="parts/header.jsp"/>
<body>

<script type="text/javascript" src="resources/js/common.js" defer></script>
<script type="text/javascript" src="resources/js/form.js" defer></script>
<script type="text/javascript" src="resources/js/formUser.js" defer></script>
<script type="text/javascript" src="resources/js/users.js" defer></script>

<div class="jumbotron bg-light pt-4">
    <div class="container">
        <h2 class="text-center">All users</h2>
        <div class="btn-group" role="group">
            <button class="btn btn-primary" onclick="openCreateForm()">
                <span class="fa fa-plus"></span>
                Add user
            </button>
            <a class="btn btn-secondary" href="profile">
                <span class="fa fa-user"></span>
                Profile
            </a>
        </div>
        <table class="table table-hover" id="table">
            <thead class="thead-light">
            <tr>
                <th>Login</th>
                <th>Password</th>
                <th>Created</th>
                <th>Role</th>
                <th></th>
                <th></th>
                <th></th>
            </tr>
            </thead>
        </table>
    </div>
</div>

<jsp:include page="parts/form.jsp"/>

</body>
</html>