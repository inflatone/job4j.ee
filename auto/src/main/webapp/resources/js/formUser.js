const userForm = $("#formUser");

$(function () {
    userForm.modal = $('#modalUserForm');
    ajaxForm(userForm, sendUserForm);
    // load and fill roles into <select>
    fillSelectFieldValues(dataAjaxUrl + 'roles', userForm.find('#role'));
});

function sendUserForm() {
    sendForm(userForm, context.url + profileId, context.afterUserDataModified);
}

function openUserCreateForm() {
    openForm(userForm.modal, context.addUserFormTitle, prepareUserFields);
}

function openUserEditForm(id) {
    const url = context.url + (id ? id : profileId);
    openForm(userForm.modal, context.editUserFormTitle, () => $.get(url, prepareUserFields));
}

function prepareUserFields(data) {
    enableFieldHiding($('#password-field'), !!data);
    refillForm(userForm, data ? data : {role: context['defaultRole']});
}