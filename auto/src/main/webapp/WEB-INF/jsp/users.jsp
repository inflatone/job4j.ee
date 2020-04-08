<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<jsp:include page="common/resources.jsp"/>
<body>

<script type="text/javascript" src="static/js/common/basic.js" defer></script>
<script type="text/javascript" src="static/js/common/form.js" defer></script>
<script type="text/javascript" src="static/js/common/list.js" defer></script>
<script type="text/javascript" src="static/js/common/image.js" defer></script>

<script type="text/javascript" src="static/js/formUser.js" defer></script>
<script type="text/javascript" src="static/js/listUser.js" defer></script>

<div class="container-fluid container-page">
    <jsp:include page="common/header.jsp"/>

    <div class="listing-row__title">
        Users
    </div>

    <div class="row content">
        <div class="col-sm-12">

            <table class="table table-hover table-responsive-md" id="userTable">
                <thead class="thead-middle">
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
</div>

<jsp:include page="common/formUser.jsp"/>

</body>
</html>