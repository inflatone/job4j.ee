let context, form, formElement;

const ajaxUrl = "ajax";

function loadFormResources() {
    $.get(ajaxUrl + '?form=user', function (data) {
        rolePlaceholder = fillSelectField('role', "Role", data.roles);
    });
}


function addFormValidator(formEl, buttonId, onSubmit) {
    document.getElementById(buttonId).addEventListener("click", function (event) {
        if (formEl.checkValidity() === false) {
            event.preventDefault();
            event.stopPropagation();
            formEl.classList.add('was-validated');
        } else if (onSubmit) {
            onSubmit(event);
        }

    }, false);
}

function resetForm(formElement) {
    if (formElement.classList.contains('was-validated')) {
        formElement.classList.remove('was-validated');
    }
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
    hideFormField(fieldId, hidden, true);
    return hidden ? keys[0] : '';
}
