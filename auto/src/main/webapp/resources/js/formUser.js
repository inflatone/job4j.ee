let rolePlaceholder, userFormValidator;

$(function () {
    form = $("#formUser");
    userFormValidator = addFormValidator(sendUserForm, form,
        {
            login: "required",
            password: "required",
            name: "required",
            role: "required",
        },
        {
            login: "Please fill out login field",
            password: "Please fill out password field",
            name: "Please fill out  name field",
            role: "Please choose role"
        });
    loadFormResources();
});

function setContext(ctx) {
    context = ctx;
}

function loadFormResources() {
    $.get(dataAjaxUrl + 'roles', function (roles) {
        rolePlaceholder = fillSelectField('role', "Role", roles);
    });
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
    $.get(context.url + (id ? id : ''), prepareFields);
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
    form.find('[name="' + key + '"]').val(value);
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

function sendUserForm() {
    $.ajax({
        type: 'POST',
        url: context.url + profileId,
        data: $("#formUser").serialize()
    }).done(function () {
        $("#modalForm").modal('hide');
        context.afterSuccess();
        successNoty('Data saved');
    });
}