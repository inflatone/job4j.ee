const form = $("#form");
let formElement;

$(function () {
    updateTable(true);
    addFormValidator();
});

function updateTable(showAll) {
    $.ajax({
        type: "GET",
        url: "ajax?showAll=" + showAll,
        dataType: 'json'
    }).done(function (data) {
        $.each(data, function (i, item) {
            addRow(item);
        });
    })
}

function addFormValidator() {
    formElement = document.getElementById("form");
    document.getElementById("formButton").addEventListener("click", function (event) {
        if (formElement.checkValidity() === false) {
            event.preventDefault();
            event.stopPropagation();
            formElement.classList.add('was-validated');
        } else {
            send();
            formElement.reset();
            formElement.classList.remove('was-validated');
        }
    }, false);
}

function addItem() {
    $("#modalTitle").html("Add note");
    form.find(":input").val("");
    $("#modalForm").modal();
}

function editItem(id) {
    $("#modalTitle").html("Edit note");
    $.get("ajax?id=" + id, function (data) {
        var item = JSON.parse(data);
        $.each(item, function (key, value) {
            form.find("input[name='" + key + "']").val(value);
        });

        $("#modalForm").modal();
    });
}

function deleteItem(id) {
    $.ajax({
        type: 'DELETE',
        url: 'ajax?id=' + id
    }).done(function () {
        clearTable();
        updateTable($('#checker').is(":checked"));
    })
}

function send() {
    var item = {
        id: $('#id').val(),
        description: $('#description').val(),
        created: $('#created').val(),
        done: $('#done').val()
    };
    $.ajax({
        type: "POST",
        url: "ajax",
        dataType: 'json',
        data: JSON.stringify(item),
        contentType: 'application/json'

    }).done(function () {
        $("#modalForm").modal('hide');
        clearTable();
        updateTable($('#checker').is(":checked"));
    })
}

function dismiss() {
    formElement.classList.remove('was-validated');
}

function clearTable() {
    $('#table').find('tr:gt(1)').remove();
}

function addRow(item) {
    $('#table tr:last').after('<tr itemCompleted="' + item.done + '">' +
        '<td class="align-middle">' + item.description + '</td>' +
        '<td class="align-middle">' + item.created + '</td>' +
        '<td class="align-middle">' + '<input type="checkbox" ' + (item.done === true ? ' checked' : '') +' onclick="complete($(this), ' + item.id +  ')">' + '</td>' +
        '<td class="align-middle">' + '<button class="btn btn-secondary my-1" onclick="editItem(' + item.id + ')"><span class="fa fa-pencil"></span></button>' + '</td>' +
        '<td class="align-middle">' + '<button class="btn btn-danger my-1" onclick="deleteItem(' + item.id + ')"><span class="fa fa-remove"></span></a>' + '</td>' +
        '</tr>');
}

function filter() {
    const checked = $('#checker').is(":checked");
    clearTable();
    updateTable(checked);
}

function complete(checkbox, id) {
    const checked = checkbox.is(":checked");
    var item = {
        id: id,
        done: checked
    };
    //  https://stackoverflow.com/a/22213543/548473
    $.ajax({
        url: 'ajax',
        type: "PATCH",
        contentType: 'application/json',
        data: JSON.stringify(item)
    }).done(function () {
        checkbox.closest("tr").attr("itemCompleted", checked);
    }).fail(function (jqXHR, textStatus, errorThrown) {
        $(checkbox).prop("checked", !checked);
    })
}