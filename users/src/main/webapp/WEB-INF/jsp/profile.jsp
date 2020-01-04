<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<jsp:include page="fragments/header.jsp"/>
<body>

<script type="text/javascript" src="resources/js/common.js" defer></script>
<script type="text/javascript" src="resources/js/users.form.js" defer></script>
<script type="text/javascript" src="resources/js/users.profile.js" defer></script>

<div class="jumbotron bg-light pt-4">
    <div class="container">
        <h2 class="text-center">Profile</h2>
        <div class="btn-group" role="group">
            <button id="editButton" class="btn btn-primary">
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
                    <img class="card-img-top" id="image" src="" alt="userpic" >
                    <div class="card-body">
                        <form id="imageForm" enctype="multipart/form-data">
                            <div class="form-group mb-2">
                                <div class="custom-file">
                                    <input id="imageFile" name="image" type="file" class="custom-file-input">
                                    <label class="custom-file-label" for="imageFile">Choose file</label>
                                </div>
                            </div>
                            <button class="btn btn-secondary mb-2" type="button" onclick="saveImage()">Upload image</button>
                            <button class="btn btn-danger mb-2" type="button" onclick="deleteImage()">Delete image</button>
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
                                <td id="username"></td>
                            </tr>
                            <tr>
                                <td class=""><b>Created:</b></td>
                                <td id="profileCreated"></td>
                            </tr>
                            <tr>
                                <td class=""><b>Role:</b></td>
                                <td id="profileRole"></td>
                            </tr>
                            <tr>
                                <td class=""><b>Enabled:</b></td>
                                <td id="profileEnabled"></td>
                            </tr>
                            <tr>
                                <td class=""><b>City:</b></td>
                                <td id="profileCity"></td>
                            </tr>
                            <tr>
                                <td class=""><b>Country:</b></td>
                                <td id="profileCountry"></td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    const profileId = "${param.id}";
    const isAdmin = "${sessionScope.authUser.role}" === "ADMIN";
</script>

<jsp:include page="fragments/form.jsp"/>

</body>
</html>