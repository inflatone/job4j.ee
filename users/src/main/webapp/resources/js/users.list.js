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
            "defaultContent": "Image",
            "orderable": false,
            "render": function (data, type, row) {
                if (type === "display") {
                    return '<img src=' + imageUrl + '?id=' + (row.image.id ? row.image.id : '') + ' alt="userpic" width="100">'
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
            "data": "created"
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
            "data": "city",
            "render": function (data, type, row) {
                // https://datatables.net/forums/discussion/comment/154284/#Comment_154284
                if (type === 'display' || type === 'sort' || type === 'filter') {
                    return row.city.name;
                }
                return data;
            }
        },
        {
            "data": "city",
            "render": function (data, type, row) {
                // // https://datatables.net/forums/discussion/comment/154284/#Comment_154284
                if (type === 'display' || type === 'sort' || type === 'filter') {
                    return row.city.country.name;
                }
                return data;
            }
        },

        {
            "defaultContent": "View",
            "orderable": false,
            "render": renderViewButton
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
        $(row).addClass("drawed");
        if (!data.enabled) {
            $(row).attr("userEnabled", false);
        }
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

function enable(chkbox, id) {
    const enabled = chkbox.is(":checked");
    //  https://stackoverflow.com/a/22213543/548473
    $.ajax({
        url: 'users?action=enable&id=' + id,
        type: "POST",
        data: "enabled=" + enabled
    }).done(function () {
        chkbox.closest("tr").attr("userEnabled", enabled);
        successNoty(enabled ? 'User enabled' : 'User disabled');
    }).fail(function () {
        $(chkbox).prop("checked", !enabled);
    })
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

function renderViewButton(data, type, row) {
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

function updateCountries() {
    const BATTUTA_KEY = "0ba88b60126a7faa3238430a42e90684";
    const countryApiUrl =
        "http://battuta.medunes.net/api/country/all?key=" +
        BATTUTA_KEY +
        "&callback=?";
    $.getJSON(countryApiUrl, saveCountries);
}

function saveCountries(countries) {
    $.ajax({
        type: 'POST',
        url: ajaxUrl + "?data=countries&action=update",
        data: JSON.stringify(countries),
        contentType: 'application/json'
    }).done(function (res) {
        getCountryList();
        successNoty(res.message);
    });
}

function clear(url, afterSuccess) {
    $.ajax({
        type: 'POST',
        url: url,
        contentType: 'application/json'
    }).done(function (data) {
        if (afterSuccess) {
            afterSuccess();
        }
        successNoty(data.message);
    });
}

function clearCountries() {
    clear(ajaxUrl + "?data=countries&action=clear", getCountryList);
}

function clearImages() {
    clear(imageUrl + "?action=clear");
}

