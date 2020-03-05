let userId, userImageId;

$(function () {
    fillUserData();
    setUploadPhotoListener();
    setContext({
        url: profileUrl,
        afterSuccess: function () {
            $.get(profileUrl + profileId, fillProfile)
        },
        idRequired: (!!profileId),
        addUser: '',
        editUser: 'Edit profile'
    });
    $('#formUserImage').attr('hidden', true);
});

function fillProfile(userData) {
    $('#profileLogin').html(userData.login);
    $('#profileName').html(userData.name);
    $('#profileRole').html(userData.role);
}

function fillUserData() {
    $.get(profileUrl + profileId, function (userData) {
        fillProfile(userData);
        $('#id').attr('value', userData.id);
        $('#profileRegistered').html(timestampAsFormattedDate(userData.registered, true, true));
        $('#profileEnabled').html('<input type="checkbox" ' + (userData.enabled ? 'checked' : '') + ' disabled>');
        userId = userData.id;
        userImageId = userData.image ? userData.image.id : '';
        updateImgSrc();
    });
}

function updateImgSrc() {
    $('#image').attr('src', imageUrl + userImageId );
}

function doDelete() {
    if (confirm("Are you sure?")) {
        $.ajax({
            url: profileUrl + profileId,
            type: 'DELETE'
        }).done(function () {
            window.location.href = "users";
        })
    }
}

function saveImage() {
    if (!$(".custom-file-label").hasClass('selected')) {
        alertNoty('Image is not selected');
    } else {
        $.ajax({
            url: imageUrl + 'drivers/' + profileId,
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