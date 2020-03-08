const postCardModal = $('#modalPostCard');

$(function () {
    postCardModal.body = postCardModal.find('.modal-body');
    extendContext({
        url: modPostAjaxUrl,
        postUrl: postAjaxUrl,

        tableUrl: postAjaxUrl,

        paging: false,
        editable: false
    });
    initPostLogic(postTableCtx());
});

function postTableCtx() {
    return {
        "columns": [
            {
                "defaultContent": "Image",
                "orderable": false,
                "render": function (data, type, row) {
                    return type === "display" ? buildImageView(row) : "";
                }
            },
            {
                "defaultContent": "Message",
                "render": function (data, type, row) {
                    return type === 'display' || type === 'filter' ? buildMessageView(row, true) : row.posted;
                }
            }
        ],
        "order": [
            [1, "desc"]
        ],
        "createdRow": function (row, data, dataIndex) {
            const jRow = $(row);
            jRow.attr("active", !data.completed);
            jRow.click(() => openForm(postCardModal, buildPostTitle(data), () => postCardModal.body.html(buildPostMessage(data))));
        },
        "searching": false
    }
}

function buildImageView(row) {
    const image = '<img src="' + imagesUrl + (row.image ? row.image.id : '') + '" alt="postImage" width="250">'
    return !context.editable ? image
        : image.concat('<div class="listing-row__posted">')
            .concat(buildPostEditButton(row))
            .concat('&nbsp;')
            .concat(buildPostDeleteButton(row))
            .concat('</div>');

}