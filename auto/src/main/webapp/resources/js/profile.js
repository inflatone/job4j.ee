const postTableCtx = {
    "columns": [
        {
            "defaultContent": "Message",
            "render": function (data, type, row) {
                return type === 'display' || type === 'filter' ? buildPostAsCard(row) : row.posted;
            }
        }
    ],
    "order": [
        [0, "desc"]
    ],
    "createdRow": function (row, data) {
        buildPostEditDeleteButtonGroup($(row).attr("active", !data.completed).find('.card'), data);
    },
    "language": {
        "emptyTable": "No post available yet"
    },
    "dom": '<"top"f>rt<"bottom"i>',
    "paging": false
}

$(function () {
    $.get(dataUrl, data => $.get(data.profile + profileId, urls => {
        userFormInit(urls.urlToRoles);
        fillProfilePage(urls);
        context.userDataUrl = urls.userDataUrl;
    }))
    context.editUserFormTitle = 'Edit profile';
});

function fillProfilePage(urls) {
    $.get(urls.userDataUrl, userData => {
        fillProfile(userData, true);
        addImageGroup(userData);
        addUserEditRemoveButtonGroup(userData);
        postDataInit({
            get: userData.urlToPosts,
            add: userData.urlToAddPost,
            form: urls.urlToCarDetails
        }, postTableCtx);
        userForm.afterDataModified = () => $.get(userData.url, fillProfile);
    });
}

function fillProfile(data, inImmutable) {
    const table = $('#table-profile');
    table.find('.login').html(data.login);
    table.find('.name').html(data.name);
    table.find('.role').html(data.role);
    if (inImmutable) {
        table.find('.registered').html(timestampAsFormattedDate(data.registered, false, true));
        table.find('.enabled').html(`<span style="margin: 2px 0 0 0" class="${data.enabled ? 'fa fa-check-circle' : 'fa fa-minus-circle'}"></span>`);
    }
}

function addImageGroup(data) {
    const imageGroup = buildImageGroup(data.image, false);
    $('#user-data').prepend(imageGroup);
}

function addUserEditRemoveButtonGroup(data) {
    if (data.urlToModify) {
        buildEditDeleteButtonGroup($('.data-body'), () => openUserEditForm(context.userDataUrl),
            () => doDeleteItem(data.urlToModify, () => window.location.href = "users"));
    }
}