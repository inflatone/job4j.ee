let rolePlaceholder, userFormValidator;

$(function () {
    form = $("#form");
    userFormValidator = addFormValidator(sendUserForm, form,
        {
            login: "required",
            password: "required",
            role: "required"
        },
        {
            login: "Please fill out login field",
            password: "Please fill out password field",
            role: "Please choose role"
        });
    loadFormResources();
});

function setContext(ctx) {
    context = ctx;
}


function openCreateForm() {
    $("#modalTitle").html(context.addUser);
    prepareFields();
    $("#modalForm").modal();
}

function openEditForm(id) {
    $("#modalTitle").html(context.editUser);
    // get data and fill fields
    id = id ? id : profileId;
    $.get(context.url + '?action=find' + (id ? '&id=' + id : ''), prepareFields);
    $("#modalForm").modal();
}

function prepareFields(data) {
    resetForm(form, userFormValidator);
    enablePasswordSwitcher(!data);
    form.find(':input').val('');
    if (!data) {
        form.find('select[id="role"]').val(rolePlaceholder);
    } else {
        $.each(data, pastePropertyInForm)
    }
}

function pastePropertyInForm(key, value) {
    if (key === 'roleOrdinal') {
        form.find("select[name='role']").val(value);
    } else {
        form.find("input[name='" + key + "']").val(value)
    }
}

function enablePasswordSwitcher(enabled) {
    $('#passwordSwitcherLine').prop('hidden', enabled);
    const switcher = $('#passwordSwitcher');
    switcher.prop('disabled', enabled);
    switcher.prop('checked', enabled);
    hideFormField('password', !enabled, false);
}

function enablePasswordChange(checkbox) {
    const checked = checkbox.is(":checked");
    hideFormField('password', !checked, true);
}

function grabUser() {
    return {
        id: context.idRequired ? $('#id').val() : null,
        login: $('#login').val(),
        password: $('#password').val(),
        role: $('#role').val(),
    };
}

function sendUserForm() {
    $.ajax({
        type: 'POST',
        url: context.url + "?action=save",
        data: JSON.stringify(grabUser()),
        contentType: 'application/json'
    }).done(function () {
        $("#modalForm").modal('hide');
        context.afterSuccess();
        successNoty('Data saved');
    });
}