const profileUrl = "profile";

$(function () {
    fillUserData();
    const ctx = {
        url: profileUrl,
        tableUrl: taskUrl,
        idRequired: (!!profileId),
        addUser: '',
        editUser: 'Edit profile'
    };
    ctx.tableUpdateUrl = ctx.tableUrl + '?action=find' + (profileId ? '&userId=' + profileId : '');
    ctx.afterSuccess = function () {
        $.get(ctx.url + '?action=find', fillProfile)
    };
    ctx.afterTaskFormSuccess = function () {
        $.get(ctx.tableUpdateUrl, fillTableByData);
    };

    setContext(ctx);
    makeDynamicTable();
    $('#allUsersButton').attr('hidden', !isAdmin);
});

const datatableOpts = {
    "columns": [
        {
            "defaultContent": "Source",
            "render": function (data, type, row) {
                // https://datatables.net/forums/discussion/comment/154284/#Comment_154284
                if (type === 'display' || type === 'sort' || type === 'filter') {
                    return renderSourceIcon(row.source);
                }
                return data;
            }
        },
        {
            "data": "keyword"
        },
        {
            "data": "city",
            "render": function (data, type, row) {
                if (type === 'display' || type === 'sort' || type === 'filter') {
                    return row.city ? row.city : '&mdash;';
                }
                return data ? data : '';
            }
        },
        {
            "defaultContent": "Scan from",
            "render": function (data, type, row) {
                // https://datatables.net/forums/discussion/comment/154284/#Comment_154284
                if (type === 'display' || type === 'sort' || type === 'filter') {
                    return timestampAsFormattedDate(row.limit, true, type === 'display' || type === 'filter');
                }
                return data;
            }
        },
        {
            "defaultContent": "Next launch",
            "render": function (data, type, row) {
                if (type === 'display' || type === 'sort' || type === 'filter') {
                    return row.launch ? (row.active ? timestampAsFormattedDate(row.launch) : 'PAUSED') : '&mdash;';
                }
                return data;
            }
        },
        {
            "defaultContent": "Rule",
            "render": function (data, type, row) {
                // https://datatables.net/forums/discussion/comment/154284/#Comment_154284
                if (type === 'display' || type === 'sort' || type === 'filter') {
                    return row.rule;
                }
                return data;
            }
        },
        {
            "data": "amount",
            "render": function (data, type, row) {
                return row.amount;
            }
        },

        {
            "defaultContent": "View",
            "orderable": false,
            "render": renderViewButton
        },
        {
            "defaultContent": "Pause/Resume",
            "orderable": false,
            "render": renderPauseButton
        },
        {
            "defaultContent": "Start",
            "orderable": false,
            "render": renderStartButton
        },
        {
            "defaultContent": "Edit",
            "orderable": false,
            "render": renderEditButton
        },
        {
            "defaultContent": "Delete",
            "orderable": false,
            "render": renderDeleteButton
        }
    ],
    "order": [
        [1, "asc"], [2, "asc"]
    ],
};

function fillProfile(userData) {
    $('#profileLogin').html(userData.login);
    $('#profileRole').html(userData.role);
}

function fillUserData() {
    $.get(profileUrl + '?action=find' + (profileId ? '&id=' + profileId : ''), function (data) {
        fillProfile(data);
        $('#id').attr('value', data.id);
        $('#profileRegistered').html(timestampAsFormattedDate(data.registered));
    });
}

function doDelete() {
    if (confirm("Are you sure?")) {
        $.ajax({
            url: profileUrl + '?action=delete' + (profileId ? '&id=' + profileId : ''),
            type: 'POST'
        }).done(function () {
            window.location.href = profileId ? "users" : "login";
        })
    }
}

function processTableElementEdit(id) {
    openEditTaskForm(id);
}

function openTaskPage(id) {
    window.location.href = 'task?id=' + id + (profileId ? '&userId=' + profileId : '');
}

function renderViewButton(data, type, row) {
    if (type === 'display') {
        return '<a onclick="openTaskPage(' + row.id + ')"><span class="fa fa-list" aria-hidden="true"></span></a>';
    }
}

function renderStartButton(data, type, row) {
    if (type === 'display') {
        return '<a onclick="doStartTask($(this).children(), ' + row.id + ')"><span class="fa fa-refresh" title="Start now"></span></a>';
    }
}

function renderPauseButton(data, type, row) {
    const active = row.active;
    if (type === 'display') {
        return '<a onclick="doPauseTask($(this).children(), ' + row.id + ')"><span class="fa fa-' + (active ? 'pause' : 'play')
            + '" title="' + (active ? 'Pause' : 'Resume') + '"></span></a>';
    }
}

function doPauseTask(icon, id) {
    const paused = icon.hasClass('fa fa-pause'); // true -> click means to pause task, task is active now (and vice versa)
    $.post(taskUrl + '?action=pause&id=' + id + '&paused=' + paused + (profileId ? '&userId=' + profileId : ''))
        .done(function () {
            switchPauseResume(icon, paused);
            successNoty(paused ? 'Task paused' : 'Task resumed');
        });

}

function switchPauseResume(icon, paused) {
    icon.removeClass(paused ? 'fa fa-pause' : 'fa fa-play');
    icon.addClass(paused ? 'fa fa-play' : 'fa fa-pause');
    icon.prop('title', paused ? 'play' : 'pause');
}

function doStartTask(icon, id) {
    if (confirm("Start now?")) {
        icon.addClass('fa-spin fa-fw'); // make refresh button spin
        $.ajax({
            url: taskUrl + '?action=start&id=' + id + (profileId ? '&userId=' + profileId : ''),
            type: 'POST'
        }).done(function (data) {
            const amountRow = $(icon).parents('tr').find('td').eq(6);
            if (data.status === 'OK') {
                amountRow.text(Number(amountRow.text()) + data.addedAmount);
                successNoty(data.foundAmount + ' vacancy(ies) found and ' + data.addedAmount + ' of them saved or updated');
            } else {
                warnNoty('Vacancy parsing fails with error');
            }
            icon.removeClass('fa-spin fa-fw'); // make refresh button freeze
        })
    }
}


function doDeleteItem(id) {
    if (confirm("Are you sure?")) {
        $.ajax({
            url: taskUrl + '?action=delete&id=' + id + (profileId ? '&userId=' + profileId : ''),
            type: 'POST'
        }).done(function () {
            context.afterTaskFormSuccess();
            successNoty('Task deleted');
        })
    }
}

function doRecount(type) {
    $.post(taskUrl + '?recount=' + type + (profileId ? '&userId=' + profileId : ''))
        .done(function () {
            context.afterTaskFormSuccess();
            successNoty('Successfully recounted ' + type);
        })
}