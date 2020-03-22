let context;
const vacancyUrl = "vacancy";

$(function () {
    let ctx = {
        url: vacancyUrl,
        tableUrl: vacancyUrl,
        idRequired: true,
    };

    ctx.tableUpdateUrl = ctx.tableUrl + '?action=find&taskId=' + taskId;
    ctx.afterSuccess = function () {
        $.get(ctx.tableUpdateUrl, fillTableByData)
    };
    context = ctx;
    makeDynamicTable();
});

const datatableOpts = {
    "columns": [
        {
            "defaultContent": "Title",
            "render": function (data, type, row) {
                if (type === 'display' || type === 'sort' || type === 'filter') {
                    return '<a href="' + row.data.url + '">' + row.data.title + '</a>';
                }
                return data;
            }
        },
        {
            "data": "data.description"
        },
        {
            "defaultContent": "Date",
            "render": function (data, type, row) {
                if (type === 'display' || type === 'sort' || type === 'filter') {
                    return row.data.dateTime;
                }
                return data;
            }
        },
        {
            "defaultContent": "Highlight",
            "orderable": false,
        },
        {
            "defaultContent": "Delete",
            "orderable": false,
        }

    ],
    "order": [
        [2, "desc"]
    ],
};