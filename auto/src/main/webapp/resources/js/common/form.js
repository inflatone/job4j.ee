function ajaxForm(form, submit) {
    form.modal.find('button.form-submit').on('click', submit);
}

function openForm(modal, title, fillFields) {
    modal.find('.modal-title').html(title);
    fillFields();
    modal.modal();
}

function refillForm(form, data, nestedFields) {
    resetValidation(form);
    form.find('.form-control').val('');
    fillForm(form, '', data, nestedFields);
}

function resetValidation(form) {
    $(form).find(".is-invalid").removeClass("is-invalid");
    $(form).find(".is-valid").removeClass("is-valid");
    $(form).find(".invalid-feedback").html('');
}

function fillForm(form, prefix, data, nestedFields) {
    $.each(data, (k, v) => {
        k = prefix + k;
        if (nestedFields && nestedFields.includes(k)) {
            fillForm(form, k + '.', v, nestedFields);
        } else {
            form.find('.form-control[name="' + k + '"]').val(v)
        }
    });
}

function enableFieldHiding(hideableField, enabled, shown) {
    const divSwitcher = hideableField.find('.hideable-field-switcher');
    divSwitcher.prop('hidden', !enabled);
    const checkbox = divSwitcher.find('input[type="checkbox"]');
    checkbox.prop('disabled', !enabled);

    const shownByDefault = enabled && !shown;
    checkbox.prop('checked', !shownByDefault);
    hideableField.find('.form-group').prop('hidden', shownByDefault);
}

function showField(checkbox, defaultValue) {
    const checked = checkbox.is(":checked");
    let hideableField = checkbox.closest('.hideable-field');
    hideableField.find('.form-control').val(defaultValue);
    hideableField.find('.form-group').prop('hidden', !checked);
}

function fillSelectFieldValues(url, field, valueMapper) {
    const fieldName = field.closest('.form-group').attr('title');
    $.get(url, values => {
        field.find('option').remove();
        $.each(values, (k, v) => field.append('<option value="' + k + '" ' + '>' + (valueMapper ? valueMapper(v) : v) + '</option>'));

        const options = field.find('option');
        const shown = options.length !== 1;
        if (shown) {
            field.append('<option value selected disabled hidden>' + fieldName + '</option>'); // placeholder
        }
        field.closest('.form-group').prop('hidden', false);
        context['default' + fieldName] = shown ? '' : options.val();
    })
}

function sendForm(form, url, afterDataModified) {
    $.ajax({
        type: 'POST',
        url: url,
        data: form.serialize()
    }).done(function () {
        form.modal.modal('hide');
        afterDataModified();
        successNoty('Record saved');
    }).fail(function (jqXHR) {
        showErrors(form, jqXHR.responseJSON.details);
    })
}

function buildEnableCheckbox(url, checked, title, key, reversed) {
    // https://stackoverflow.com/a/53920217
    return `<label class="custom-control custom-checkbox" title="${title}">
                <input class="custom-control-input"
                       type="checkbox" ${checked ? 'checked' : ''}
                       onclick="doEnableItem($(this), '${url}', '${key}', ${reversed})">
                <span class="custom-control-indicator"></span>
                <span class="custom-control-label"></span>
            </label>`
}

function showErrors(form, errorDetails) {
    resetValidation(form);
    $(form).find(".form-control").addClass("is-valid");
    $.each(errorDetails, (k, v) => pasteInvalidFeedback(findFormField(form, k).removeClass('is-valid').addClass('is-invalid'), v));
}

function pasteInvalidFeedback(field, feedback) {
    const invalidFeedback = field.closest('.form-group').find('.invalid-feedback');
    if (!invalidFeedback.length) {
        const error = $("<div>")
            .addClass("invalid-feedback")
            .addClass("error")
            .html(feedback);
        field.closest('.form-group').append(error);
        error.hide().show();
        console.log(field.closest('.form-group')[0]);
    } else {
        invalidFeedback.html(feedback);
        invalidFeedback.hide().show();
    }
}

function findFormField(form, name) {
    const formField = form.find('[name="' + name + '"].form-control');
    return formField.length ? formField : form.find('[name="' + name + '.id"].form-control')
}

function buildEditDeleteButtonGroup(onclickEdit, onclickDelete) {
    return '<div class="btn-group btn-group-sm btn-block header-group">'
        + '         <button onclick="' + onclickEdit + '" class="btn btn-outline-dark btn-twin">'
        + '             <span class="fa fa-pencil"></span> Edit'
        + '         </button>'
        + '         <button class="btn btn-outline-danger button_profile-delete btn-twin"'
        + '                 onclick="' + onclickDelete + '">'
        + '             <span class="fa fa-remove"></span> Remove'
        + '         </button>'
        + '     </div>'
}