const context = {};

let failNote;

function extendContext(ctx) {
    Object.assign(context, ctx);
}

$(function () {
    // https://stackoverflow.com/a/4303862/548473
    $.ajaxSetup({cache: false});

    $(document).ajaxError(function (event, jqXHR, options, jsExc) {
        failAjaxNoty(jqXHR);
    })

    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");

    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    })
});

function closeNoty() {
    if (failNote) {
        failNote.close();
        failNote = undefined;
    }
}

function showNoty(message, style, timeout) {
    closeNoty();
    return new Noty({
        text: message,
        type: style,
        layout: 'bottomRight',
        theme: 'semanticui',
        timeout: timeout
    }).show();
}

function confirmNoty(todo) {
    const noty = new Noty({
        text: '<span class="fa fa-lg fa-exclamation-circle"></span> &nbsp;Do you want to continue?',
        layout: 'topCenter',
        type: 'error',
        theme: 'semanticui',
        buttons: [
            Noty.button('YES', 'btn btn-danger', () => {
                todo();
                noty.close();
            }, {id: 'button1', 'data-status': 'ok'}),
            Noty.button('<span class="errorText">NO</span>', 'btn btn-error', () => noty.close())
        ]
    });
    noty.show();
}

function successNoty(message) {
    showNoty('<span class="fa fa-lg fa-check"></span> &nbsp;' + message, 'info', 1500);
}

function alertNoty(message) {
    failNote = showNoty('<span class="fa fa-lg fa-exclamation-circle"></span> &nbsp;' + message, 'warning');
}

function failAjaxNoty(jqXHR) {
    alertNoty('Error status: ' + jqXHR.status + (jqXHR.responseJSON ? '<br>' + jqXHR.responseJSON.message   : ''));
}

// https://github.com/phstc/jquery-dateFormat
function timestampAsFormattedDate(timestamp, prettyMode, display) {
    const date = new Date(timestamp);
    return prettyMode ? (display ? $.format.prettyDate(date) : $.format.toBrowserTimeZone(date, 'yyyy-MM-dd HH:mm'))
        : $.format.toBrowserTimeZone(date, display ? 'yyyy-MM-dd' : 'yyyy-MM-dd HH:mm')
}