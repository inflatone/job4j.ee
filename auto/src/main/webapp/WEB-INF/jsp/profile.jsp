<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<jsp:include page="common/header.jsp"/>
<body>

<script type="text/javascript" src="resources/js/common.js" defer></script>
<script type="text/javascript" src="resources/js/form.js" defer></script>
<script type="text/javascript" src="resources/js/formUser.js" defer></script>
<script type="text/javascript" src="resources/js/profile.js" defer></script>

<div class="jumbotron bg-light pt-4">
    <div class="container">
        <h2 class="text-center">Profile</h2>
        <div class="btn-group" role="group">
            <sec:authorize access="hasAuthority('ADMIN')">
                <a class="btn btn-info" href="users">
                    <span class="fa fa-list"></span>
                    All users
                </a>
            </sec:authorize>
            <button id="editButton" onclick="openEditForm()" class="btn btn-primary">
                <span class="fa fa-pencil"></span>
                Edit profile
            </button>
            <button class="btn btn-secondary" onclick="doDelete()">
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
            <div class="col">
                <div class="card">
                    <img class="card-img-top" id="image" src="" alt="userpic">
                    <div class="card-body">
                        <form id="imageForm" enctype="multipart/form-data">
                            <div class="form-group mb-2">
                                <div class="custom-file">
                                    <input id="imageFile" name="userImage" type="file" class="custom-file-input">
                                    <label class="custom-file-label" for="imageFile">Choose file</label>
                                </div>
                            </div>
                            <button class="btn btn-secondary mb-2" type="button" onclick="saveImage()">Upload image
                            </button>
                            <button class="btn btn-danger mb-2" type="button" onclick="deleteImage()">Delete image
                            </button>
                        </form>
                    </div>
                </div>
            </div>

            <div class="col-6">

                <div class="card">
                    <div class="card-body">
                        <table class="table table-hover">
                            <tr>
                                <td class=""><b>Login:</b></td>
                                <td id="profileLogin"></td>

                            </tr>
                            <tr>
                                <td class=""><b>Name:</b></td>
                                <td id="profileName"></td>
                            </tr>
                            <tr>
                                <td class=""><b>Registered:</b></td>
                                <td id="profileRegistered"></td>
                            </tr>
                            <tr>
                                <td class=""><b>Role:</b></td>
                                <td id="profileRole"></td>
                            </tr>
                            <tr>
                                <td class=""><b>Enabled:</b></td>
                                <td id="profileEnabled"></td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="common/formUser.jsp"/>

<script type="text/javascript">
    const profileId = "${profileId}";
</script>

</body>
</html>