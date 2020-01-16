let rolePlaceholder;

$(function () {
    formElement = document.getElementById('form');
    addFormValidator(formElement, "submitButton", sendUserForm);
    loadFormResources();
});

function setContext(ctx) {
    context = ctx;
    form = $("#form");
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
    resetForm(formElement);
    enablePasswordSwitcher(!data);
    if (!data) {
        form.find(':input').val('');
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
    hideFormField('password', !enabled, true);
}

function enablePasswordChange(checkbox) {
    const checked = checkbox.is(":checked");
    hideFormField('password', !checked, true);
}

function grabUser() {
    console.log($('#form').serialize());
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