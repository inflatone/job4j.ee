const userForm = $("#formUser");

function userFormInit(roleUrl) {
    userForm.modal = $('#modalUserForm');

    setFormSubmitAction(userForm, sendUserForm);
    // load and fill roles into <select>
    fillSelectFieldValues(roleUrl, userForm.find('#role'));
}

function sendUserForm() {
    sendForm(userForm);
}

function openUserCreateForm() {
    openModal(userForm.modal, context.addUserFormTitle, prepareUserFields);
}

function openUserEditForm(url) {
    openModal(userForm.modal, context.editUserFormTitle, () => $.get(url, prepareUserFields));
}

function prepareUserFields(data) {
    enableFieldHiding($('#password-field'), !!data);
    refillForm(userForm, data ? data : {role: context['defaultRole'], urlToModify: context['urlToAddUser']});
}