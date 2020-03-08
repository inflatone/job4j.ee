const postForm = $("#formPost");

const nestedFields = ['car', 'car.vendor', 'car.transmission', 'car.engine', 'car.body'];

const distanceFormatter = new Intl.NumberFormat('cs', {style: 'decimal', unit: 'kilometer'});
const currencyFormatter = new Intl.NumberFormat('ru', {style: 'currency', currency: 'RUB', minimumFractionDigits: 0});

function initPostLogic(ctx) {
    postForm.modal = $('#modalPostForm');
    postForm.priceCheckbox = postForm.find('.hideable-field-switcher');

    ajaxForm(postForm, sendPostForm);
    fillTable($('#postTable'), ctx);

    fillSelectFieldValues(dataAjaxUrl + 'vendors', postForm.find('#vendorId'), v => v.name + ' (' + v.country + ')');
    fillSelectFieldValues(dataAjaxUrl + 'bodies', postForm.find('#bodyId'), v => v.type);
    fillSelectFieldValues(dataAjaxUrl + 'engines', postForm.find('#engineId'), v => v.type);
    fillSelectFieldValues(dataAjaxUrl + 'transmissions', postForm.find('#transmissionId'), v => v.type);

    extendContext({
        afterPostDataModified: () => $.get(context.postUrl, fillTableByData)
    });

    $('#carYear').datetimepicker({
        format: "YYYY",
        viewMode: "years",
        // Bootstrap 4 doesn't support glyphicons
        // https://stackoverflow.com/a/58656821/10375242
        icons: {
            previous: 'fa fa-angle-left',
            next: 'fa fa-angle-right'
        }
    });
}

function sendPostForm() {
    sendForm(postForm, modPostAjaxUrl, context.afterPostDataModified);
}

function openPostCreateForm() {
    openForm(postForm.modal, 'Add post', preparePostFields);
}

function openPostEditForm(id) {
    const url = postAjaxUrl + id;
    openForm(postForm.modal, 'Edit post', () => $.get(url, preparePostFields));
}

function preparePostFields(data) {
    refillForm(postForm, data ? data : {car: {mileage: 0}}, nestedFields);

    enableFieldHiding($('#price-field'), true, data && data.price);
    enableFieldHiding($('#mileage-field'), true, data && data.car.mileage > 0);

    context['defaultMileage'] = data ? data.car.mileage : 0;
    context['defaultPrice'] = data ? data.price : undefined;
}

function buildPostMessage(data, editable) {
    const url = context.imagePostUrl + data.id;
    const imageId = data.image ? data.image.id : undefined;
    const imageGroup = buildImageGroup('postPhoto', imageId, true, editable, url);

    const dataPrice = !data.price ? '' : '<span class="listing-row__price ">' + currencyFormatter.format(data.price) + '</span>';
    const message = '<span style="font-size: 1.1em">' + (data.message ? data.message : 'The author did not provide a description') + '</span>'
    const posted = timestampAsFormattedDate(data.posted, false, false);
    return ' <div class="col-md-12">'
        + '     <div class="card">' + imageGroup
        + '         <div class="card-body">' + buildCompletePostCheckbox(data, editable)
        + '             <div>' + buildPostTitle(data)
        + '                 <p class="card-text">'
        + '                     <div class="card-subtitle listing-row__details">' + dataPrice
        + '                         <span class="listing-row__mileage">' + distanceFormatter.format(data.car.mileage) + ' km</span>'
        + '                         <ul class="listing-row__meta">'
        + '                             <li><strong>Body:</strong> ' + data.car.body.type + '</li>'
        + '                             <li><strong>Year:</strong> ' + data.car.year + '</li>'
        + '                             <li><strong>Engine:</strong> ' + data.car.engine.type + '</li>'
        + '                             <li><strong>Transmission:</strong> ' + data.car.transmission.type + '</li>'
        + '                         </ul>'
        + '                         <h6 style="font-size: 175%">' + data.title + '</h6>' + message
        + '                     </div>'
        + '                 </p>'
        + '                 <p class="card-text" style="text-align: right">'
        + '                     <small class="text-muted">'
        + '                         <span class="fa fa-clock-o"></span> ' + posted
        + '                     </small>'
        + '                 </p>'
        + '             </div>'
        + '         </div> ' + buildPostEditDeleteButtonGroup(data.id, editable)
        + '     </div>'
        + ' </div>'
}

function buildCompletePostCheckbox(data, editable) {
    if (editable) {
        return '<span style="float: right;">'
            + buildEnableCheckbox(modPostAjaxUrl + data.id, data.completed, 'Hide', 'completed', true)
            + '</span>';
    }
    return '';
}

function buildPostEditDeleteButtonGroup(id, editable) {
    if (editable) {
        return buildEditDeleteButtonGroup(`openPostEditForm(${id})`,
            `doDeleteItem('${modPostAjaxUrl + id}', context.afterPostDataModified)`);
    }
    return '';
}

function buildPostTitle(data) {
    return '<p class="card-title" style="font-size: 1.4em">'
        + '     <strong>' + data.car.vendor.name + '</strong> ' + data.car.model
        + '     <span class="listing-row__country">' + data.car.vendor.country + '</span>'
        + ' </p>'

}

function buildMessageView(data, short) {
    return '<div class="listing-row__details">'
        + '     <div class="payment-section">'
        + '         <span class="listing-row__price ">' + (data.price ? currencyFormatter.format(data.price) : '—') + '</span>'
        + '         <span class="listing-row__mileage">' + distanceFormatter.format(data.car.mileage) + ' km</span>'
        + '     </div>'
        + '     <div class="listing-row__title">'
        + '         <strong>' + data.car.vendor.name + '</strong> ' + data.car.model
        + '         <span class="listing-row__country">' + data.car.vendor.country + '</span>'
        + '     </div>'
        + '     <ul class="listing-row__meta">'
        + '         <li><strong>Body:</strong> ' + data.car.body.type + '</li>'
        + '         <li><strong>Year:</strong> ' + data.car.year + '</li>'
        + '         <li><strong>Engine:</strong> ' + data.car.engine.type + '</li>'
        + '         <li><strong>Transmission:</strong> ' + data.car.transmission.type + '</li>'
        + '     </ul>'
        + '     <h6>' + data.title + '</h6>'
        + '     <span ' + (short ? 'hidden' : '') + '>' + (data.message ? data.message : 'The author did not provide a description') + '</span>'
        + '     <div class="listing-row__posted" style="bottom:0;">'
        + '         <span class="fa fa-clock-o"></span>&nbsp;' + timestampAsFormattedDate(data.posted, false, false)
        + '     </div>'
        + ' </div>';
}

function buildPostEditButton(row) {
    return '<a onclick="openPostEditForm(' + row.id + ')"><span class="fa fa-pencil"></span></a>'
}

function buildPostDeleteButton(row) {
    const url = context.tableUrl + row.id;
    return '<a onclick="doDeleteItem(\'' + url + '\', context.afterPostDataModified)"><span class="fa fa-remove"></span></a>';
}