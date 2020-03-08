<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<jsp:include page="common/resources.jsp"/>
<body>

<script type="text/javascript" src="static/js/common/basic.js" defer></script>
<script type="text/javascript" src="static/js/common/form.js" defer></script>
<script type="text/javascript" src="static/js/common/list.js" defer></script>
<script type="text/javascript" src="static/js/common/image.js" defer></script>

<script type="text/javascript" src="static/js/profile.js" defer></script>
<script type="text/javascript" src="static/js/formUser.js" defer></script>

<div class="container-fluid container-page">
    <jsp:include page="common/header.jsp"/>

    <div class="listing-row__title">
        <strong>Profile</strong>
    </div>

    <div class="row content">
        <div class="col-md-4 sidenav">
            <div class="card" id="user-data">
                <div class="data-body">
                    <table id="table-profile" class="table" style="margin-bottom: 0;">
                        <tr>
                            <td><b>Login:</b></td>
                            <td class="login"></td>
                        </tr>
                        <tr>
                            <td><b>Name:</b></td>
                            <td class="name"></td>
                        </tr>
                        <tr>
                            <td><b>Registered:</b></td>
                            <td class="registered"></td>
                        </tr>
                        <tr>
                            <td><b>Role:</b></td>
                            <td class="role"></td>
                        </tr>
                        <tr>
                            <td><b>Enabled:</b></td>
                            <td class="enabled" style="vertical-align: center"></td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
        <div class="col-md-8" style="width: 100%">
            <div class="row flex-row" style="width: 100%">
                <table class="table table-hover" id="postTable">
                    <thead style="border: black; border-radius: 5px;">
                    <tr>
                        <th><div class="h3 listing-row__title" style="margin-left: 5%"><strong>Posts</strong></div>
                        </th>
                    </tr>
                    </thead>
                    <tbody></tbody>
                </table>
            </div>

        </div>
    </div>
</div>

<jsp:include page="common/formUser.jsp"/>

</body>
</html>