const adminUrl = "users";

$(function () {
    const ctx = {
        url: adminUrl,
        tableUrl: adminUrl,
        idRequired: true,
        addUser: 'Add user',
        editUser: 'Edit user',
    };

    ctx.tableUpdateUrl = ctx.tableUrl + '?action=find';
    ctx.afterSuccess = function () {
        $.get(ctx.tableUpdateUrl, fillTableByData)
    };

    setContext(ctx);
    makeDynamicTable();
});

const datatableOpts = {
    "columns": [
        {
            "data": "login"
        },
        {
            "data": "registered"
        },
        {
            "data": "role"
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
        [0, "asc"]
    ],
    "createdRow": function (row, data, dataIndex) {
        $(row).addClass("drawed");
    }
};

function doDeleteItem(id) {
    if (confirm("Are you sure?")) {
        $.ajax({
            url: context.tableUrl + '?action=delete&id=' + id,
            type: 'POST'
        }).done(function () {
            context.afterSuccess();
            successNoty('User deleted');
        })
    }
}

function openProfilePage(id) {
    window.location.href = 'profile?id=' + id;
}

function renderProfileButton(data, type, row) {
    if (type === "display") {
        return '<a onclick="openProfilePage(' + row.id + ')"><i class="fa fa-address-card-o" aria-hidden="true"></i></a>';
    }
}

function processTableElementEdit(id) {
    openEditForm(id);
}