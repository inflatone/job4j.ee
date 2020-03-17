<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<jsp:include page="parts/header.jsp"/>
<body>

<script type="text/javascript" src="resources/js/common.js" defer></script>
<script type="text/javascript" src="resources/js/form.js" defer></script>
<script type="text/javascript" src="resources/js/formUser.js" defer></script>
<script type="text/javascript" src="resources/js/profile.js" defer></script>

<div class="jumbotron bg-light pt-4">
    <div class="container">
        <h2 class="text-center">Profile</h2>
        <div class="btn-group" role="group">
            <a id="allUsersButton" class="btn btn-info" href="users" hidden>
                <span class="fa fa-list"></span>
                All users
            </a>
            <button id="editButton" class="btn btn-primary">
                <span class="fa fa-pencil"></span>
                Edit profile
            </button>
            <button class="btn btn-secondary" onclick="doDelete(${param.id})">
                <span class="fa fa-remove"></span>
                Remove profile
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
                <div class="card">
                    <div class="card-body">
                        <table class="table table-hover">
                            <tr>
                                <td class=""><b>Login:</b></td>
                                <td id="profileLogin"></td>
                            </tr>
                            <tr>
                                <td class=""><b>Password:</b></td>
                                <td id="profilePassword"></td>
                            </tr>
                            <tr>
                                <td class=""><b>Registered:</b></td>
                                <td id="profileRegistered"></td>
                            </tr>
                            <tr>
                                <td class=""><b>Role:</b></td>
                                <td id="profileRole"></td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>

            <div class="col">
                <div class="card">
                    <table class="table table-hover" id="tasks">
                        <thead class="thead-light">
                        <tr>
                            <th>Keyword</th>
                            <th>City</th>
                            <th>Last scan</th>
                            <th>Rule</th>
                            <th></th>
                            <th></th>
                            <th></th>
                        </tr>
                        </thead>
                        <tr>
                            <td>java</td>
                            <td>Tolyatti</td>
                            <td>2020-01-10 6:37:13</td>
                            <td>everyday (12:00)</td>
                            <td>hh</td>
                            <td>mk</td>
                            <td>sql</td>
                        </tr>
                    </table>

                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    const profileId = "${param.id}";
    const isAdmin = "${requestScope.role}" === "ADMIN";
</script>

<jsp:include page="parts/form.jsp"/>

</body>
</html>