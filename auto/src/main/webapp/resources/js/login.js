const profileId = "";

$(function () {
    extendContext({
        url: profileAjaxUrl + "registration",
        afterUserDataModified: () => loginAs(userForm.find('input[name=login]').val(), userForm.find('input[name=password]').val()),
        addUserFormTitle: 'Registration'
    });
    if (alertMessage) {
        alertNoty(alertMessage);
    }
});

function loginAs(username, password) {
    $('#authLogin').val(username);
    $('#authPassword').val(password);
    login();
}

function login() {
    $('#login_form').submit()
}