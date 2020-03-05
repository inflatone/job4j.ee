const adminUrl = "ajax/admin/users/";
const profileUrl = "ajax/profile/";

const dataAjaxUrl = "ajax/data/"

const imageUrl = "images/";

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

function alertNoty(message) {
    closeNoty();
    failNote = new Noty({
        text: '<span class="fa fa-lg fa-exclamation-circle"></span> &nbsp;' + message,
        type: 'error',
        layout: 'bottomRight'
    }).show();
}

function failNoty(jqXHR) {
    closeNoty();
    failNote = new Noty({
        text: '<span class="fa fa-lg fa-exclamation-circle"></span> &nbsp;' + 'Error status: ' + jqXHR.status + (jqXHR.responseJSON ? '<br>' + jqXHR.responseJSON : ''),
        type: 'error',
        layout: 'bottomRight'
    }).show();
}

// https://github.com/phstc/jquery-dateFormat
function timestampAsFormattedDate(timestamp, prettyMode, display) {
    const date = new Date(timestamp);
    return prettyMode ? (display ? $.format.prettyDate(date) : $.format.toBrowserTimeZone(date, 'yyyy-MM-dd HH:mm'))
        : $.format.toBrowserTimeZone(date, display ? 'yyyy-MM-dd' : 'yyyy-MM-dd HH:mm')
}