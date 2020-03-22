<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<jsp:include page="parts/header.jsp"/>
<body>

<script type="text/javascript" src="resources/js/common.js" defer></script>
<script type="text/javascript" src="resources/js/list.js" defer></script>
<script type="text/javascript" src="resources/js/task.js" defer></script>

<div class="jumbotron bg-light pt-4">
    <div class="container">
        <h2 class="text-center">Vacancy list</h2>
        <div class="btn-group" role="group">
            <a id="allUsersButton" class="btn btn-info" href="users" hidden>
                <span class="fa fa-list"></span>
                All users
            </a>
            <a class="btn btn-secondary" href="profile">
                <span class="fa fa-user"></span>
                Profile
            </a>
            <button id="editButton" onclick="openEditForm()" class="btn btn-primary">
                <span class="fa fa-pencil"></span>
                Edit task
            </button>
            <button class="btn btn-secondary" onclick="alert('Clear all data');">
                <span class="fa fa-warning"></span>
                Clear all data
            </button>
            <button class="btn btn-primary" onclick="doDelete()">
                <span class="fa fa-remove"></span>
                Remove task
            </button>
            <a class="btn btn-danger" href="logout">
                <span class="fa fa-sign-out"></span>
                Sign out
            </a>
        </div>
        <br/>
        <br/>
        <div class="row align-items-start">
            <div class="col-6">
                <table class="table table-hover">
                    <tr>
                        <td class=""><b>Source:</b></td>
                        <td id="source"></td>
                    </tr>
                    <tr>
                        <td class=""><b>Keyword:</b></td>
                        <td id="keyword"></td>
                    </tr>
                    <tr>
                        <td class=""><b>City:</b></td>
                        <td id="city"></td>
                    </tr>
                    <tr>
                        <td class=""><b>Last scan:</b></td>
                        <td id="lastScan"></td>
                    </tr>
                </table>
            </div>
        </div>


        <table class="table table-hover" id="table">
            <thead class="thead-light">
            <tr>
                <th>Title</th>
                <th>Description</th>
                <th>Date</th>
                <th></th>
                <th></th>
            </tr>
            </thead>
        </table>
    </div>
</div>

<script type="text/javascript">
    const taskId = "${param.id}";
    const profileId = "${param.userId}"
</script>


</body>
</html>