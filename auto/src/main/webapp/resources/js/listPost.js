const postCardModal = $('#modalPostCard');

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
    "searching": false
}

$(function () {
    postCardModal.body = postCardModal.find('.modal-body');
    $.get(dataUrl, data => fillPostsPage(data.posts));
});

function fillPostsPage(url) {
    $.get(url, data => postDataInit({
        get: data.urlToPosts,
        add: data.urlToAddPost,
        form: data.urlToCarDetails
    }, postTableCtx, () => $.get(data.urlToPosts, fillTableByData), onEditPostModalClosed));
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