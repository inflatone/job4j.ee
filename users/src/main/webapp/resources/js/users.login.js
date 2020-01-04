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
    })
});

function submitLogin(event) {
    event.preventDefault();
    event.stopPropagation();
    login();
}

function loginAs(username, password) {
    $('#authLogin').val(username);
    $('#authPassword').val(password);
    login();
}

function login() {
    const credentials = {
        'login' : $('#authLogin').val(),
        'password' : $('#authPassword').val()
    };
    $.ajax({
        type: 'POST',
        url: 'login',
        data: JSON.stringify(credentials),
        contentType: 'application/json',
        dataType: 'json'
    }).done(function (data) {
        window.location.href = data.redirection;
    })
}