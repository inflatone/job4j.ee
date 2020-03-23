const profileUrl = "profile";
const taskUrl = "task";

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
                    return row.limit.substr(0, 10);
                }
                return data;
            }
        },
        {
            "defaultContent": "Next launch",
            "render": function (data, type, row) {
                if (type === 'display' || type === 'sort' || type === 'filter') {
                    return row.launch ? row.launch : '&mdash;';
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
            "defaultContent": "Platform",
            "render": function (data, type, row) {
                // https://datatables.net/forums/discussion/comment/154284/#Comment_154284
                if (type === 'display' || type === 'sort' || type === 'filter') {
                    return row.source.title;
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
        [0, "asc"]
    ],
};

function fillProfile(userData) {
    $('#profileLogin').html(userData.login);
    $('#profileRegistered').html(userData.registered);
    $('#profileRole').html(userData.role);
}

function fillUserData() {
    $.get(profileUrl + '?action=find' + (profileId ? '&id=' + profileId : ''), function (data) {
        fillProfile(data);
        $('#id').attr('value', data.id);
        $('#profileRegistered').html(data.registered);
    });
}

function doDelete(id) {
    if (confirm("Are you sure?")) {
        $.ajax({
            url: profileUrl + '?action=delete' + (id ? '&id=' + id : ''),
            type: 'POST'
        }).done(function () {
            window.location.href = "login";
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

function doStartTask(refresh, id) {
    if (confirm("Start now?")) {
        refresh.addClass('fa-spin fa-fw'); // make refresh button spin
        $.ajax({
            url: taskUrl + '?action=start&id=' + id + (profileId ? '&userId=' + profileId : ''),
            type: 'POST'
        }).done(function (data) {
            const amountRow = $(refresh).parents('tr').find('td').eq(6);
            if (data.status === 'OK') {
                amountRow.text(Number(amountRow.text()) + data.addedAmount);
                successNoty(data.foundAmount + ' vacancy(ies) found and ' + data.addedAmount + ' of them saved or updated');
            } else {
                warnNoty('Vacancy parsing fails with error');
            }
            refresh.removeClass('fa-spin fa-fw'); // make refresh button freeze
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

function doRecount() {
    $.post(taskUrl + (profileId ? '?userId=' + profileId : ''))
        .done(function () {
            context.afterTaskFormSuccess();
            successNoty('Vacancies recounted');
        })
}