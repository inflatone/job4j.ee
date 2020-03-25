$(function () {
    addFormValidator(
        login,
        $('#loginForm'),
        {
            login: "required",
            password: "required"
        },
        {
            login: "Please fill out login field",
            password: "Please fill out password field"
        });
    setContext({
        url: "profile",
        afterSuccess: function () {
            $('#authLogin').val($('#login').val());
            $('#authPassword').val($('#password').val());
            login();
        },
        idRequired: false,
        addUser: 'Registration',
        editUser: ''
    });
});

function loginAs(username, password) {
    $('#authLogin').val(username);
    $('#authPassword').val(password);
    login();
}

function login() {
    $.ajax({
        type: 'POST',
        url: 'login',
        data: $('#loginForm').serialize(),
        dataType: 'json'
    }).done(function (data) {
        window.location.href = data.redirection;
    })
}