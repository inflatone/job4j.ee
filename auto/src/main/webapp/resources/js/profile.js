$(function () {
    extendContext({
        url: profileAjaxUrl,
        postUrl: postAjaxUrl + 'filter?user.id=' + profileId,

        tableUrl: postAjaxUrl + 'filter?user.id=' + profileId,

        imagePostUrl: modImagePostUrl,
        paging: false,

        afterUserDataModified: () => $.get(profileAjaxUrl + profileId, fillProfile),
        editUserFormTitle: 'Edit profile'
    });

    fillUserData(editable);
});

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

function fillUserData(editable) {
    $.get(profileAjaxUrl + profileId, userData => {
        fillProfile(userData, true);
        addImageGroup(userData, editable);
        addUserEditRemoveButtonGroup(editable);
    });
}

function addImageGroup(data, editable) {
    const imageId = data.image ? data.image.id : undefined;
    const imageGroup = buildImageGroup("userPhoto", imageId, false, editable, modImageProfileUrl);
    $('#user-data').prepend(imageGroup);
}

function addUserEditRemoveButtonGroup(editable) {
    if (editable) {
        const buttonGroup = buildEditDeleteButtonGroup('openUserEditForm()', 'doDelete()');
        $('.data-body').append(buttonGroup);
    }
}

function doDelete() {
    confirmNoty(() => {
        $.ajax({
            url: profileAjaxUrl + profileId,
            type: 'DELETE'
        }).done(function () {
            window.location.href = "users";
        })
    });
}