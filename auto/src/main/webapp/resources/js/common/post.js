const postForm = $("#formPost");

const nestedFields = ['car', 'car.vendor', 'car.transmission', 'car.engine', 'car.body'];

const distanceFormatter = new Intl.NumberFormat('cs', {style: 'decimal', unit: 'kilometer'});
const currencyFormatter = new Intl.NumberFormat('ru', {style: 'currency', currency: 'RUB', minimumFractionDigits: 0});

function postDataInit(urls, ctx, afterDataModified, onModalClosed) {
    context.urlToAddPost = urls.add;

    postForm.modal = $('#modalPostForm');
    postForm.modal.on('hidden.bs.modal', onModalClosed);

    postForm.priceCheckbox = postForm.find('.hideable-field-switcher');
    postForm.afterDataModified = afterDataModified ? afterDataModified : () => $.get(urls.get, fillTableByData);

    setFormSubmitAction(postForm, sendPostForm);
    fillTable($('#postTable'), urls.get, ctx, true);

    $.get(urls.form, data => {
        fillSelectFieldValuesByData(data['vendors'], postForm.find('#vendorId'), v => v.name + ' (' + v.country + ')');
        fillSelectFieldValuesByData(data['bodies'], postForm.find('#bodyId'), v => v.type);
        fillSelectFieldValuesByData(data['engines'], postForm.find('#engineId'), v => v.type);
        fillSelectFieldValuesByData(data['transmissions'], postForm.find('#transmissionId'), v => v.type);
    })

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
    sendForm(postForm);
}

function openPostCreateForm() {
    openModal(postForm.modal, 'Add post', preparePostFields);
}

function openPostEditForm(url) {
    openModal(postForm.modal, 'Edit post', () => $.get(url, preparePostFields));
}

function preparePostFields(data) {
    refillForm(postForm, data ? data : {car: {mileage: 0}, urlToModify: context['urlToAddPost']}, nestedFields);

    enableFieldHiding($('#price-field'), true, data && data.price);
    enableFieldHiding($('#mileage-field'), true, data && data.car.mileage > 0);

    context['defaultMileage'] = data ? data.car.mileage : 0;
    context['defaultPrice'] = data ? data.price : undefined;
}

function buildPostAsCard(data, showBlankImage) {
    const imageGroup = buildImageGroup(data.image, !showBlankImage);
    const dataPrice = !data.price ? '' : '<span class="listing-row__price ">' + currencyFormatter.format(data.price) + '</span>';
    const message = '<span style="font-size: 1.1em">' + (data.message ? data.message : 'The author did not provide a description') + '</span>'
    const posted = timestampAsFormattedDate(data.posted, false, false);

    const card = $(
        '<div class="card">'
        + '         <div class="card-body">' + buildCompletePostCheckbox(data)
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
        + '         </div> '
        + '</div>');
    return $('<div class="col-md-12"></div>')
        .append(card.prepend(imageGroup))
        .prop('outerHTML');
}

function buildCompletePostCheckbox(data) {
    if (data.urlToModify) {
        return '<span class="float-right">'
            + buildEnableCheckbox(data.urlToModify + '/completed', data.completed, 'Hide', 'completed', true)
            + '</span>';
    }
    return '';
}

function buildPostEditDeleteButtonGroup(card, data) {
    if (data.urlToModify) {
        buildEditDeleteButtonGroup(card, () => openPostEditForm(data.urlToModify),
            () => doDeleteItem(data.urlToModify, postForm.afterDataModified))
    }
}

function buildPostTitle(data) {
    return '<p class="card-title" style="font-size: 1.4em">'
        + '     <strong>' + data.car.vendor.name + '</strong> ' + data.car.model
        + '     <span class="listing-row__country">' + data.car.vendor.country + '</span>'
        + ' </p>'

}

function buildPostAsPreview(data) {
    return '<div class="listing-row__details">'
        + '     <div class="payment-section">'
        + '         <span class="listing-row__price ">' + (data.price ? currencyFormatter.format(data.price) : '—') + '</span>'
        + '         <span class="listing-row__mileage">' + distanceFormatter.format(data.car.mileage) + ' km</span>'
        + '     </div>'
        + '     <div class="row-title">'
        + '         <a class="extra-link clickable"><span class="fa fa-info-circle"></span></a>'
        + '         <strong>' + data.car.vendor.name + '</strong> ' + data.car.model
        + '         <span class="listing-row__country">' + data.car.vendor.country + '</span>'
        + '     </div>'
        + '     <ul class="listing-row__meta">'
        + '         <li><strong>Body:</strong> ' + data.car.body.type + '</li>'
        + '         <li><strong>Year:</strong> ' + data.car.year + '</li>'
        + '         <li><strong>Engine:</strong> ' + data.car.engine.type + '</li>'
        + '         <li><strong>Transmission:</strong> ' + data.car.transmission.type + '</li>'
        + '     </ul>'
        + '     <div class="listing-row__posted" style="bottom:0;">'
        + '         <a class="extra-link" href="profile/' + data.user.id + '">'
        + '             <span class="fa fa-id-card-o" aria-hidden="true"></span>&nbsp;' + data.user.name
        + '         </a><br>'
        + '         <span class="fa fa-clock-o"></span>&nbsp;' + timestampAsFormattedDate(data.posted, false, false)
        + '     </div>'
        + ' </div>';
}