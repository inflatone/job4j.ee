$(function () {
    setContext({
        url: profileUrl + "registration",
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
    $('#login_form').submit()
}