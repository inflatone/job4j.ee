const loginForm = $('#login_form');

$(function () {
    $.get(dataUrl, data => loginPageInit(data.login));
    if (alertMessage) {
        alertNoty(alertMessage);
    }
    userForm.afterDataModified = () => loginAs(userForm.find('input[name=login]').val(), userForm.find('input[name=password]').val())
});

function loginPageInit(url) {
    $.get(url, data => {
        userFormInit(data.urlToRoles);
        loginForm.attr('action', data.urlToLogin);
        extendContext({
            addUserFormTitle: 'Registration',
            urlToAddUser: data.urlToRegister,
        });
    })
}

function loginAs(username, password) {
    $('#authLogin').val(username);
    $('#authPassword').val(password);
    loginForm.submit();
}