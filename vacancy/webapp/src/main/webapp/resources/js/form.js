let context, form;

const ajaxUrl = "ajax";

function loadFormResources() {
    $.get(ajaxUrl + '?form=user', function (data) {
        rolePlaceholder = fillSelectField('role', "Role", data.roles);
    });
}

function addFormValidator(submit, formEl, rules, messages) {
    return  formEl.validate({
        submitHandler: function () {
            submit();
        },
        rules: rules,
        messages: messages,
        errorElement: "div",
        errorPlacement: function (error, element) {
            error.addClass('invalid-feedback');
            element.closest('.form-group').append(error);
            console.log(element.closest('.form-group').get(0));
        },
        // https://stackoverflow.com/a/52159728/10375242
        highlight: function (element, errorClass, validClass) {
            $(element).addClass('is-invalid').removeClass('is-valid');
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).removeClass('is-invalid').addClass('is-valid');
        }
    });
}

function resetForm(formElement, validator) {
    validator.reset();
    $(formElement).find(".is-invalid").removeClass("is-invalid");
    $(formElement).find(".is-valid").removeClass("is-valid");
}

function hideFormField(fieldId, hidden, checkRequired) {
    $('#' + fieldId + 'Field').prop('hidden', hidden);
    if (checkRequired) {
        $('#' + fieldId).prop('required', !hidden);
    }
}

function fillSelectField(fieldId, placeholderText, options) {
    const field = $('#' + fieldId);
    $('#' + fieldId + ' option').remove();

    field.append('<option value selected disabled hidden>' + placeholderText + '</option>'); // placeholder
    $.each(options, function (key, value) {
        field.append('<option value="' + key + '" ' + '>' + value + '</option>');
    });

    const keys = Object.keys(options);
    const hidden = keys.length < 2;
    hideFormField(fieldId, hidden, false);
    return hidden ? keys[0] : '';
}
