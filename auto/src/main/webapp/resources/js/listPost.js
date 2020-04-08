const postCardModal = $('#modalPostCard');
const filterForm = $('#filterForm');

const postTableCtx = {
    "columns": [
        {
            "defaultContent": "Image",
            "orderable": false,
            "render": function (data, type, row) {
                return type === "display" ? buildImageView(row.image, 'post-thumbnail clickable') : "";
            }
        },
        {
            "defaultContent": "Message",
            "render": function (data, type, row) {
                return type === 'display' || type === 'filter' ? buildPostAsPreview(row) : row.posted;
            }
        }
    ],
    "order": [
        [1, "desc"]
    ],
    "createdRow": function (row, data) {
        const jRow = $(row);
        jRow.attr("active", !data.completed);
        jRow.find('.clickable').click(() => onRowClick(data));
    },
    "searching": false,
    "language": {
        "emptyTable": "No post available yet"
    },
    "dom": '<"top"p>rt<"bottom"il>',
    "paging": true
}

$(function () {
    postCardModal.body = postCardModal.find('.modal-body');
    $.get(dataUrl, data => fillPostsPage(data.posts));
    prepareFilterForm();
});

function prepareFilterForm() {
    setLinkedDateTimeFields($('#yearMin'), $('#yearMax'), 'YYYY', 'years');
    setLinkedDateTimeFields($("#filterPostedMin"), $("#filterPostedMax"), 'YYYY-MM-DD');

}

// https://eonasdan.github.io/bootstrap-datetimepicker/#linked-pickers
function setLinkedDateTimeFields(fromField, toField, format, viewMode) {
    setFieldAsDateTime(fromField, format, viewMode);
    setFieldAsDateTime(toField, format, viewMode, {useCurrent: false});
    fromField.on('dp.change', e => toField.data('DateTimePicker').minDate(e.date));
    toField.on('dp.change', e => fromField.data('DateTimePicker').maxDate(e.date));

    // occasionally datetimepicker inserts current date in the field — bug??
    fromField.val('');
    toField.val('');
}

function fillPostsPage(url) {
    $.get(url, data => {
        postDataInit({
            get: data.urlToPosts,
            add: data.urlToAddPost,
            form: data.urlToCarDetails
        }, postTableCtx, () => $.get(data.urlToPosts, fillTableByData), onEditPostModalClosed);

        filterForm.urlToPosts = data.urlToPosts;
    });
}

function resetFilter(button) {
    filterForm[0].reset();
    const icon = turnOnProcessIcon(button, 'fa-undo');
    $.get(filterForm.urlToPosts, fillTableByData)
        .always(() => turnOffProcessIcon(icon));
}

function doFilter(button) {
    const icon = turnOnProcessIcon(button, 'fa-refresh');
    $.ajax({
        type: 'GET',
        url: filterForm.urlToPosts + '/filter',
        data: retrieveFormValues()
    }).done(data => fillTableByData(data)).always(() => turnOffProcessIcon(icon));
}

function retrieveFormValues() {
    const params = {};
    $.each(filterForm.serializeArray(), function () {
        params[this.name] = this.value
    });

    transformToTimestamp(params, 'posted.min');
    transformToTimestamp(params, 'posted.max', 1000 * 60 * 60 * 24); // append day millis to include this day date
    return $.param(params);
}

function transformToTimestamp(params, key, increment) {
    const value = params[key];
    if (value) {
        params[key] = Date.parse(value) + (increment ? increment : 0);
    }
}

function onRowClick(data) {
    openModal(postCardModal, 'Post card', () => {
        $.get(data.url, data => {
            postCardModal.body.html(buildPostAsCard(data, true));
            if (data.urlToModify) {
                buildEditDeleteButtonGroup(postCardModal.body.find('.card'),
                    () => onclickEditPostCard(data), () => onclickDeletePostCard(data));
            }
        });
    });
}

function onclickEditPostCard(data) {
    postCardModal.post = data;
    postCardModal.modal('hide');
    openPostEditForm(data.urlToModify);
}

function onclickDeletePostCard(data) {
    doDeleteItem(data.urlToModify, () => {
        postForm.afterDataModified();
        postCardModal.modal('hide');
    })
}

function onEditPostModalClosed() {
    if (postCardModal.post) {
        onRowClick(postCardModal.post);
        postCardModal.post = false;
    }
}