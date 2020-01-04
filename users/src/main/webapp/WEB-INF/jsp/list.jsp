<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<jsp:include page="fragments/header.jsp"/>
<body>

<script type="text/javascript" src="resources/js/common.js" defer></script>
<script type="text/javascript" src="resources/js/users.form.js" defer></script>
<script type="text/javascript" src="resources/js/users.list.js" defer></script>

<button type="button" onclick="updateCountries()">Update countries</button>
<button type="button" onclick="clearCountries()">Clear countries</button>
<button type="button" onclick="clearImages()">Clear images</button>

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
            <a class="btn btn-danger" href="logout">
                <span class="fa fa-sign-out"></span>
                Sign out
            </a>
        </div>
        <table class="table table-hover" id="table">
            <thead class="thead-light">
            <tr>
                <th>Userpic</th>
                <th>Login</th>
                <th>Name</th>
                <th>Created</th>
                <th>Role</th>
                <th>Enabled</th>
                <th>City</th>
                <th>Country</th>
                <th></th>
                <th></th>
                <th></th>

            </tr>
            </thead>
        </table>
    </div>
</div>

<jsp:include page="fragments/form.jsp"/>

</body>
</html>