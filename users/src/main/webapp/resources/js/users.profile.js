const profileUrl = "profile";
let userId;
let userImageId;

$(function () {
    if (isAdmin) {
        addUsersButton();
    }
    fillUserData();
    setUploadPhotoListener();
    setContext({
        url: profileUrl,
        afterSuccess: function () {
            $.get(profileUrl + '?action=find', fillProfile)
        },
        idRequired: (!!profileId),
        addUser: '',
        editUser: 'Edit profile'
    });
    $('#formUserImage').attr('hidden', true);
});

function fillProfile(userData) {
    $('#profileLogin').html(userData.login);
    $('#username').html(userData.name);
    $('#profileRole').html(userData.role);
    $('#profileCity').html(userData.city.name);
    $('#profileCountry').html(userData.city.country.name)
}

function fillUserData() {
    $.get(profileUrl + '?action=find&id=' + (profileId ? profileId : ''), function (userData) {
        fillProfile(userData);
        $('#id').attr('value', userData.id);
        $('#profileCreated').html(userData.created);
        $('#profileEnabled').html('<input type="checkbox" ' + (userData.enabled ? 'checked' : '') + ' disabled>');
        $('#editButton').attr('onclick', 'openEditForm(' + userData.id + ')'); //onclick="openEditForm(${user.id})"
        userId = userData.id;
        userImageId = userData.image.id;
        updateImgSrc();
    });
}

function updateImgSrc() {
    $('#image').attr('src', imageUrl + '?id=' + (userImageId ? userImageId : ''));
}

function addUsersButton() {
    $('#editButton').before('<a class="btn btn-info" href="users"><span class="fa fa-list"></span> All users</a>')
}

function doDelete() {
    if (confirm("Are you sure?")) {
        $.ajax({
            url: context.url + '?action=delete' + (profileId ? '&id=' + profileId : ''),
            type: 'POST'
        }).done(function () {
            window.location.href = "login";
        })
    }
}

function saveImage() {
    if (!$(".custom-file-label").hasClass('selected')) {
        alertNoty('Image is not selected');
    } else {
        $.ajax({
            url: imageUrl + '?action=save&userId=' + userId,
            type: 'POST',
            encType: "multipart/form-data",
            data: new FormData(document.getElementById("imageForm")),
            cache: false,
            processData: false,
            contentType: false
        }).done(function (data) {
            userImageId = data.id;
            imageSuccess();
            successNoty('Image updated');
        });
    }
}

function imageSuccess() {
    // https://stackoverflow.com/a/3228167
    updateImgSrc();
    $('#imageFile').val('');
    $(".custom-file-label").removeClass('selected').html('Choose file');
}

function deleteImage() {
    if (userImageId == undefined) {
        alertNoty('User has no picture');
    } else {
        $.ajax({
            url: imageUrl + '?action=delete&id=' + userImageId + '&userId=' + userId,
            type: 'POST'
        }).done(function () {
            userImageId = undefined;
            imageSuccess();
            successNoty('Image deleted');
        })
    }
}

// https://www.w3schools.com/bootstrap4/bootstrap_forms_custom.asp
function setUploadPhotoListener() {
    $(".custom-file-input").on("change", function () {
        var fileName = $(this).val().split("\\").pop();
        $(this).siblings(".custom-file-label").addClass("selected").html(fileName);
    });
}