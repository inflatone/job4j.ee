const adminUrl = "users";

$(function () {
    setContext({
        url: adminUrl,
        afterSuccess: function () {
            $.get(adminUrl + '?action=find', fillTableByData)
        },
        idRequired: true,
        addUser: 'Add user',
        editUser: 'Edit user'
    });
    makeDynamicTable();
    $('#formUserImage').attr('hidden', false);
});
//<td><img src="images?id=${user.id}" alt="userpic" width="100" height="100"></td>
const datatableOpts = {
    "columns": [
        {
            "data": "login"
        },
        {
            "data": "password"
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

function makeDynamicTable() {
    context.datatableApi = $("#table").DataTable(
        // https://api.jquery.com/jquery.extend/#jQuery-extend-deep-target-object1-objectN
        $.extend(true, datatableOpts, {
            "ajax": {
                "url": context.url + '?action=find',
                "dataSrc": ""
            },
            "paging": false,
            "info": true
        })
    );
}

function doDeleteItem(id) {
    if (confirm("Are you sure?")) {
        $.ajax({
            url: context.url + '?action=delete&id=' + id,
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

function renderEditButton(data, type, row) {
    if (type === "display") {
        return '<a onclick="openEditForm(' + row.id + ')"><span class="fa fa-pencil"></span></a>';
    }
}

function renderDeleteButton(data, type, row) {
    if (type === "display") {
        return '<a onclick="doDeleteItem(' + row.id + ')"><span class="fa fa-remove"></span></a>';
    }
}

function fillTableByData(data) {
    context.datatableApi.clear().rows.add(data).draw();
}