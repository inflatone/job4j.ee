const profileUrl = "profile";
let userId;

$(function () {
    fillUserData();
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
    $('#profilePassword').html(userData.password);
    $('#profileRegistered').html(userData.registered);
    $('#profileRole').html(userData.role);
}

function fillUserData() {
    $.get(profileUrl + '?action=find' + (profileId ? '&id=' + profileId : ''), function (data) {
        fillProfile(data);
        $('#id').attr('value', data.id);
        $('#profileRegistered').html(data.registered);
        $('#editButton').attr('onclick', 'openEditForm(' + data.id + ')'); //onclick="openEditForm(${user.id})"
        $('#deleteButton').attr('onclick', 'doDelete(' + data.id + ')'); //onclick="doDelete(${user.id})"
        userId = data.id;
    });
}

function doDelete(id) {
    if (confirm("Are you sure?")) {
        $.ajax({
            url: context.url + '?action=delete' + (id ? '&id=' + id : ''),
            type: 'POST'
        }).done(function () {
            window.location.href = "login";
        })
    }
}

