function makeDynamicTable(paging) {
    context.datatableApi = $("#table").DataTable(
        // https://api.jquery.com/jquery.extend/#jQuery-extend-deep-target-object1-objectN

        $.extend(true, datatableOpts, {
            "ajax": {
                "url": context.tableUpdateUrl,
                "dataSrc": ""
            },
            "paging": !!paging,
            "info": true
        })
    );
}

function fillTableByData(data) {
    context.datatableApi.clear().rows.add(data).draw();
}

function renderEditButton(data, type, row) {
    if (type === "display") {
        return '<a onclick="processTableElementEdit(' + row.id + ')"><span class="fa fa-pencil"></span></a>';
    }
}

function renderDeleteButton(data, type, row) {
    if (type === "display") {
        return '<a onclick="doDeleteItem(' + row.id + ')"><span class="fa fa-remove"></span></a>';
    }
}