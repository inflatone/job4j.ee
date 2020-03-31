function setFormSubmitAction(form, submit) {
    form.modal.find('button.form-submit').on('click', submit);
}

function openModal(modal, title, fillFields) {
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
    $.get(url, data => fillSelectFieldValuesByData(data, field, valueMapper));
}

function fillSelectFieldValuesByData(data, field, valueMapper) {
    field.find('option').remove();
    $.each(data, (k, v) => field.append('<option value="' + k + '" ' + '>' + (valueMapper ? valueMapper(v) : v) + '</option>'));

    const options = field.find('option');
    const shown = options.length !== 1;
    const fieldName = field.closest('.form-group').attr('title');
    if (shown) {
        field.append('<option value selected disabled hidden>' + fieldName + '</option>'); // placeholder
    }
    field.closest('.form-group').prop('hidden', !shown);
    context['default' + fieldName] = shown ? '' : options.val();
}

function doDeleteItem(url, afterDataModified) {
    confirmNoty(() => {
        $.ajax({
            url: url,
            type: 'DELETE'
        }).done(function () {
            afterDataModified();
            successNoty('Record deleted');
        })
    });
}

function sendForm(form) {
    console.log('icon');
    const icon = turnOnProcessIcon(form.closest('.modal-content').find('.form-submit'), 'fa-check');
    $.ajax({
        type: 'POST',
        url: form.find('[name=urlToModify]').val(),
        data: form.serialize()
    }).done(function () {
        form.modal.modal('hide');
        form.afterDataModified();
        successNoty('Record saved');
    }).fail(function (jqXHR) {
        showErrors(form, jqXHR.responseJSON.details);
    }).always(() => turnOffProcessIcon(icon));
}

function turnOnProcessIcon(submitButton, iconClass) {
    const icon = submitButton.find(`.${iconClass}`).removeClass(iconClass).addClass('fa-spinner fa-pulse');
    icon.defaultClass = iconClass;
    return icon;
}

function turnOffProcessIcon(icon) {
    icon.removeClass('fa-spinner fa-pulse').addClass(icon.defaultClass);
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
    } else {
        invalidFeedback.html(feedback);
        invalidFeedback.hide().show();
    }
}

function findFormField(form, name) {
    const formField = form.find('[name="' + name + '"].form-control');
    return formField.length ? formField : form.find('[name="' + name + '.id"].form-control')
}

function buildEditDeleteButtonGroup(appendTo, onclickEdit, onclickDelete) {
    const buttonGroup
        = ' <div class="btn-group btn-group-sm btn-block header-group">'
        + '     <button class="btn btn-outline-dark button-edit btn-twin">'
        + '         <span class="fa fa-pencil"></span> Edit'
        + '     </button>'
        + '     <button class="btn btn-outline-danger button_delete btn-twin">'
        + '         <span class="fa fa-remove"></span> Remove'
        + '     </button>'
        + ' </div>'
    appendTo.append(buttonGroup);
    $(appendTo).find('.button-edit').click(onclickEdit);
    $(appendTo).find('.button_delete').click(onclickDelete);
}