$(function () {
    const ctx = {
        url: adminUrl,
        tableUrl: adminUrl,
        idRequired: true,
        addUser: 'Add user',
        editUser: 'Edit user',
    };

    ctx.tableUpdateUrl = ctx.tableUrl;
    ctx.afterSuccess = function () {
        $.get(ctx.tableUpdateUrl, fillTableByData)
    };

    setContext(ctx);
    makeDynamicTable();
});

const datatableOpts = {
    "columns": [
        {
            "defaultContent": "Image",
            "orderable": false,
            "render": function (data, type, row) {
                if (type === "display") {
                    return '<img src="' + imageUrl + (row.image ? row.image.id : '') + '" alt="userImage" width="100">'
                }
                return "";
            }
        },
        {
            "data": "login"
        },
        {
            "data": "name"
        },
        {
            "defaultContent": "Registered",
            "render" : function (data, type, row) {
                if (type === 'display' || type === 'sort' || type === 'filter') {
                    return timestampAsFormattedDate(row.registered, false, type === 'display');
                }
                return data;
            }
        },
        {
            "data": "role"
        },
        {
            "data": "enabled",
            "render": function (data, type, row) {
                if (type === "display") {
                    return "<input type='checkbox' " + (data ? "checked" : "") + " onclick='enable($(this), " + row.id + ")'/>";
                }
                return data;
            }
        },

        {
            "defaultContent": "View",
            "orderable": false,
            "render": renderProfileButton
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
        [1, "asc"]
    ],
    "createdRow": function (row, data, dataIndex) {
        $(row).attr("active", data.enabled);
    }
};

function enable(chkbox, id) {
    const enabled = chkbox.is(":checked");
    //  https://stackoverflow.com/a/22213543/548473
    $.ajax({
        url: adminUrl + id,
        type: "POST",
        data: "enabled=" + enabled
    }).done(function () {
        chkbox.closest("tr").attr("active", enabled);
        successNoty(enabled ? 'User enabled' : 'User disabled');
    }).fail(function () {
        $(chkbox).prop("checked", !enabled);
    })
}

function doDeleteItem(id) {
    if (confirm("Are you sure?")) {
        $.ajax({
            url: adminUrl + id,
            type: 'DELETE'
        }).done(function () {
            context.afterSuccess();
            successNoty('User deleted');
        })
    }
}

function openProfilePage(id) {
    window.location.href = 'profile/' + id;
}

function renderProfileButton(data, type, row) {
    if (type === "display") {
        return '<a onclick="openProfilePage(' + row.id + ')"><i class="fa fa-address-card-o" aria-hidden="true"></i></a>';
    }
}

function processTableElementEdit(id) {
    openEditForm(id);
}