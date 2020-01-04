const imageUrl = "images";

let failNote;

$(function () {
    // https://stackoverflow.com/a/4303862/548473
    $.ajaxSetup({cache: false});

    $(document).ajaxError(function (event, jqXHR, options, jsExc) {
        failNoty(jqXHR);
    })
});

function closeNoty() {
    if (failNote) {
        failNote.close();
        failNote = undefined;
    }
}

function successNoty(message) {
    closeNoty();
    new Noty({
        text: '<span class="fa fa-lg fa-check"></span> &nbsp;' + message,
        type: 'success',
        layout: 'bottomRight',
        timeout: 1500
    }).show();
}

function failNoty(jqXHR) {
    closeNoty();
    failNote = new Noty({
        text: '<span class="fa fa-lg fa-exclamation-circle"></span> &nbsp;' + 'Error status: ' + jqXHR.status + (jqXHR.responseJSON ? '<br>' + jqXHR.responseJSON.error : ''),
        type: 'error',
        layout: 'bottomRight'
    }).show();
}

function alertNoty(message) {
    closeNoty();
    failNote = new Noty({
        text: message,
        type: 'error',
        layout: 'bottomRight'
    }).show();
}