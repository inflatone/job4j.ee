$(function () {
    addFormValidator(document.getElementById('loginForm'), "loginButton", submitLogin);
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

function submitLogin(event) {
    event.preventDefault();
    event.stopPropagation();
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