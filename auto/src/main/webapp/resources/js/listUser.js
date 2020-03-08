$(function () {
    $('#addUser').prop('hidden', false);
    $('#addPost').prop('hidden', true);

    extendContext({
        url: adminAjaxUrl,
        tableUrl: adminAjaxUrl,
        addUserFormTitle: 'Add user',
        editUserFormTitle: 'Edit user',
        paging: false,
        afterUserDataModified: () => $.get(adminAjaxUrl, fillTableByData)
    });

    fillTable($("#userTable"), {
        "columns": [
            {
                "defaultContent": "Image",
                "orderable": false,
                "render": function (data, type, row) {
                    if (type === "display") {
                        return '<img src="' + imagesUrl + (row.image ? row.image.id : '') + '" alt="user image" class="user-thumbnail">'
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
                "render": function (data, type, row) {
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
                    return type !== "display" ? data : buildEnableCheckbox(context.tableUrl + row.id, data, 'Enable user', 'enabled');
                }
            },

            {
                "defaultContent": "View",
                "orderable": false,
                "render": function (data, type, row) {
                    if (type === "display") {
                        const url = profileUrl + row.id;
                        return '<a onclick="openPage(\'' + url + '\')"><i class="fa fa-address-card-o" aria-hidden="true"></i></a>';
                    }
                }
            },
            {
                "defaultContent": "Edit",
                "orderable": false,
                "render": function (data, type, row) {
                    if (type === "display") {
                        return '<a onclick="openUserEditForm(' + row.id + ')"><span class="fa fa-pencil"></span></a>';
                    }
                }
            },
            {
                "defaultContent": "Delete",
                "orderable": false,
                "render": function (data, type, row) {
                    if (type === "display") {
                        const url = context.tableUrl + row.id;
                        return '<a onclick="doDeleteItem(\'' + url + '\', context.afterUserDataModified)"><span class="fa fa-remove"></span></a>';
                    }
                }
            }
        ],
        "order": [
            [1, "asc"]
        ],
        "createdRow": function (row, data, dataIndex) {
            $(row).attr("active", data.enabled);
        },
    });
});