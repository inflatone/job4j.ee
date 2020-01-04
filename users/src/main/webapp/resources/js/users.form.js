let context, form, countries, formElement;

const ajaxUrl = "ajax";

$(function () {
    formElement = document.getElementById('form');
    addFormValidator(formElement, "submitButton", sendForm);
    getCountryList();
});

function getCountryList() {
    $.getJSON(ajaxUrl + "?data=countries", function (data) {
        countries = data;
        fillCountries();
    })
}

function setContext(ctx) {
    context = ctx;
    form = $("#form");
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

function resetForm() {
    if (formElement.classList.contains('was-validated')) {
        formElement.classList.remove('was-validated');
    }
    $('#cityPart').prop('hidden', true);
}

function openCreateForm() {
    resetForm();
    $("#modalTitle").html(context.addUser);
    form.find(":input").val("");
    $("#country").val('');

    $('#passwordSwitcher').prop('hidden', true);
    showPasswordField(true);

    hidePasswordField(false);
    fillRoles(null);

    $("#modalForm").modal();
}

function showPasswordField(shown) {
    $('#passwordSwitcherLine').prop('hidden', shown);

    const switcher = $('#passwordSwitcher');
    switcher.prop('disabled', shown);
    switcher.prop('checked', shown);
    hidePasswordField(!shown);
}

function hidePasswordField(hidden) {
    $('#passwordField').prop('hidden', hidden);
    $('#password').prop('required', !hidden);
}

function enablePasswordChange(checkbox) {
    const checked = checkbox.is(":checked");
    hidePasswordField(!checked);
}

function openEditForm(id) {
    resetForm();
    $("#modalTitle").html(context.editUser);

    // fill data form
    $.get(context.url + "?action=find&id=" + id, function (userData) {
        $.each(userData, function (key, value) {
            if (key !== 'role') {
                form.find("input[name='" + key + "']").val(value);
            }
        });
        fillRoles(userData);
        showPasswordField(false);

        $("#country").val(userData.city.country.id);

        updateCitiesOptions(userData.city.country.id, userData.city);
        $("#modalForm").modal();
    })
}

function fillCountries() {
    const country = $("#country");
    $('#country option').remove();

    const placeHolder = '<option value selected disabled hidden>Country</option>';
    country.append(placeHolder);

    $.each(countries, function (index, value) {
        country.append('<option id="' + value.id + '" value="' + value.id + '" ' + '>' + value.name + '</option>');
    });

    country.change(function () {
        updateCitiesOptions(this.options[this.selectedIndex].value);
    })
}

function updateCitiesOptions(selectedCountryId, defaultCity) {
    $('#cityPart').prop('hidden', false);
    const city = $('#city');
    $.get(ajaxUrl + '?data=cities&id=' + selectedCountryId, function (cities) {
        $('#city option').remove();
        const placeHolder = '<option value selected disabled hidden>City</option>';
        city.append(placeHolder);
        switchToCityInput(cities.length === 0);
        $.each(cities, function (index, value) {
            const option = '<option id="' + value.id + '" value="' + value.id + '">' + value.name + "</option>";
            city.append(option);
        });
        city.val(defaultCity ? defaultCity.id : '');
    });
}

function switchToCityInput(enable) {
    const switcher = $('#citySwitcher');
    switcher.prop('checked', enable);
    switcher.prop('disabled', enable);
    switchCityForm(switcher);
}

function switchCityForm(checkbox) {
    const checked = checkbox.is(":checked");
    const cityInput = $("#cityInput");
    const city = $("#city");

    city.prop('disabled', checked);
    city.prop('required', !checked);
    cityInput.prop('hidden', !checked);
    cityInput.prop('required', checked);

    cityInput.val('');
}

function fillRoles(user) {
    const role = $('#role');
    $('#role option').remove();

    const placeHolder = '<option value selected disabled hidden>Role</option>';
    role.append(placeHolder);

    $.get(ajaxUrl + '?data=roles', function (data) {
        role.prop('hidden', data.length < 2);
        $.each(data, function (index, value) {
            role.append('<option id="' + value + '" value="' + value + '" ' + '>' + value + '</option>');
        });
        role.val(user ? user.role : data.length === 1 ? data[0] : '');
    });
}

function sendForm() {
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

function grabUser() {
    return {
        id: context.idRequired ? $('#id').val() : null,
        name: $('#name').val(),
        login: $('#login').val(),
        password: $('#password').val(),
        role: $('#role').val(),
        city: grabCity()
    };
}

function grabCity() {
    return $('#citySwitcher').is(':checked') ? {
        name: $('#cityInput').val(),
        country: {
            id: $('#country').val()
        }
    } : {
        id: $('#city').val()
    };
}