function fillTable(table, datatableOpts) {
    context.datatableApi = table.DataTable(
        // https://api.jquery.com/jquery.extend/#jQuery-extend-deep-target-object1-objectN

        $.extend(true, datatableOpts, {
            "ajax": {
                "url": context.tableUrl,
                "dataSrc": ""
            },
            "paging": context.paging,
            "info": true
        })
    );
}

function doEnableItem(checkbox, url, key, reversed) {
    const checked = checkbox.is(":checked");
    //  https://stackoverflow.com/a/22213543/548473
    $.ajax({
        url: url,
        type: 'POST',
        data: key + '=' + checked
    }).done(function () {
        checkbox.closest("tr").attr("active", reversed ? !checked : checked);
        successNoty('Record is '+ (checked ? '' : 'not ') + key + ' now');
    }).fail(function () {
        $(checkbox).prop("checked", !checked);
    })
}

function doDeleteItem(url, afterDataModified) {
    confirmNoty(() => {
        $.ajax({
            url: url,
            type: 'DELETE'
        }).done(function () {
            afterDataModified();
            // context.afterUserDataModified();
            successNoty('Record deleted');
        })
    });
}

function fillTableByData(data) {
    context.datatableApi.clear().rows.add(data).draw();
}

function openPage(url) {
    window.location.href = url;
}