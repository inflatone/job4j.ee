<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<jsp:include page="common/header.jsp"/>
<body>

<script type="text/javascript" src="resources/js/common.js" defer></script>
<script type="text/javascript" src="resources/js/form.js" defer></script>
<script type="text/javascript" src="resources/js/formUser.js" defer></script>
<script type="text/javascript" src="resources/js/list.js" defer></script>
<script type="text/javascript" src="resources/js/listUser.js" defer></script>

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
        <table class="table table-hover" id="listTable">
            <thead class="thead-light">
            <tr>
                <th></th>
                <th>Login</th>
                <th>Name</th>
                <th>Created</th>
                <th>Role</th>
                <th>Enabled</th>
                <th></th>
                <th></th>
                <th></th>
            </tr>
            </thead>
        </table>
    </div>
</div>

<jsp:include page="common/formUser.jsp"/>

<script type="text/javascript">
    const profileId = ""; // temporary stub
</script>

</body>
</html>