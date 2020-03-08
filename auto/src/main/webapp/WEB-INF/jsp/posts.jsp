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

<script type="text/javascript" src="static/js/common/post.js" defer></script>
<script type="text/javascript" src="static/js/listPost.js" defer></script>

<div class="container-fluid container-page">
    <jsp:include page="common/header.jsp"/>

    <div class="listing-row__title">
        <strong>Posts</strong>
    </div>

    <div class="row content">
        <div class="col-sm-4 sidenav">
            <div class="card" id="user-data">
                <div class="data-body">
                    <table id="table-profile" class="table table-hover">
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

                    <div class="btn-group special btn-group-sm btn-block header-group">
                        <button id="editButton" onclick="openUserEditForm()" class="btn btn-outline-dark">
                            <span class="fa fa-refresh"></span>
                            Filter
                        </button>
                        <button class="btn btn-outline-danger button_profile-delete"
                                onclick="doDelete()">
                            <span class="fa fa-undo"></span> Reset
                        </button>
                    </div>

                </div>
            </div>
        </div>
        <div class="col-sm-8" style="width: 100%">
            <div class="row flex-row" style="width: 100%">
                <table class="table table-hover" id="postTable">
                    <thead class="thead-middle">
                    <tr>
                        <th>Preview</th>
                        <th>Review</th>
                    </tr>
                    </thead>
                </table>

            </div>
        </div>
    </div>
</div>

<div class="modal fade" tabindex="-1" id="modalPostCard">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-body"></div>
        </div>
    </div>
</div>

<jsp:include page="common/formPost.jsp"/>

</body>
</html>