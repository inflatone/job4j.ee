const profileUrl = 'profile/';

const userTableCtx = {
    "columns": [
        {
            "defaultContent": "Image",
            "orderable": false,
            "render": function (data, type, row) {
                return type !== 'display' ? '' : buildImageView(row.image, 'user-thumbnail');
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
                return type !== "display" ? data : buildEnableCheckbox(row.urlToModify + '/enabled', data, 'Enable user', 'enabled');
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
                    return `<a onclick="openUserEditForm('${row.url}')"><span class="fa fa-pencil"></span></a>`;
                }
            }
        },
        {
            "defaultContent": "Delete",
            "orderable": false,
            "render": function (data, type, row) {
                if (type === "display") {
                    return `<a onclick="doDeleteItem('${row.urlToModify}', userForm.afterDataModified)"><span class="fa fa-remove"></span></a>`;
                }
            }
        }
    ],
    "order": [
        [1, "asc"]
    ],
    "createdRow": function (row, data) {
        $(row).attr("active", data.enabled);
    },
    "paging": false
}

$(function () {
    $('#addPost').prop('hidden', true);

    extendContext({
        addUserFormTitle: 'Add user',
        editUserFormTitle: 'Edit user',
    });

    $.get(dataUrl, data => $.get(data.users, urls => {
        console.log(urls);
        userFormInit(urls.urlToRoles);
        context.urlToAddUser = urls.urlToAddUser;
        $('#addUser').prop('hidden', !context.urlToAddUser);

        userForm.afterDataModified = () => $.get(urls.urlToUsers, fillTableByData);
        fillTable($("#userTable"), urls.urlToUsers, userTableCtx);
    }))
});