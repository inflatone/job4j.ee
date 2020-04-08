function buildImageGroup(data, hideable) {
    const img = `<img class="card-img-top" src="${data.url}"
                        alt="${data.name}" ${hideable && data.blank ? 'hidden' : ''}>`;
    const url = data.urlToModify;
    if (url) {
        context[url] = data.blank ? false : data.url;
        const form = buildImageForm(data, hideable);
        return img.concat(form);
    }
    return img;
}

function buildImageForm(data, hideable) {
    return `<div class="image-footer">
                <form enctype="multipart/form-data">
                    <div class="form-group mb-2"> 
                        <div class="custom-file">
                            <input id="image-${data.urlToModify}" name="${data.name}" type="file"
                                   onchange="pasteFilename($(this))"
                                   class="custom-file-input form-control-sm">
                            <label class="custom-file-label" for="image-${data.urlToModify}">Choose file</label>
                        </div>
                    </div>
                </form>
                <div class="btn-group special btn-group-sm btn-block header-group" role="group">
                    <button class="btn btn-outline-dark btn-twin" type="button"
                            onclick="uploadAndPasteImgByButton($(this), '${data.urlToModify}')">
                        <span class="fa fa-upload"></span>&nbsp; Upload
                    </button>
                    <button class="btn btn-outline-danger btn-twin" type="button"
                            onclick="deleteAndCleanImgByButton($(this), '${data.urlToModify}', ${hideable})">
                        <span class="fa fa-trash"></span>&nbsp; Clean
                     </button>
                </div>
            </div>`;
}

function pasteFilename(input) {
    const fileName = input.val().split("\\").pop();
    input.siblings('label').addClass('selected').html(fileName);
}

function buildImageView(image, className) {
    return '<img src="' + image.url + '" alt="' + image.name + '" class="' + className + '">'
}

function updateImgSrc(img, url) {
    // https://stackoverflow.com/a/3228167
    img.attr('src', url);
}

function uploadAndPasteImgByButton(button, url) {
    const footer = button.closest('.image-footer');
    const img = footer.siblings('img');
    const form = footer.find('form');
    const label = form.find('label');
    if (!label.hasClass('selected')) {
        alertNoty('Image is not selected');
    } else {
        resetValidation(form);
        const icon = turnOnProcessIcon(button, 'fa-upload');
        $.ajax({
            url: url,
            type: 'POST',
            encType: "multipart/form-data",
            data: new FormData(form[0]), // extract from jquery
            cache: false,
            processData: false,
            contentType: false
        }).done(data => {
            context[url] = data.url;
            updateImgSrc(img, data.url);
            img.prop('hidden', false);
            form.find('input').val('');
            label.removeClass('selected').html('Choose file');
            successNoty('Image uploaded');
        }).fail(jqXHR => {
            pasteInvalidFeedback(form.find('input'), jqXHR.responseJSON.message)
        }).always(() => {
            turnOffProcessIcon(icon)
        });
    }
}

function deleteAndCleanImgByButton(button, url, hideable) {
    if (!context[url]) {
        alertNoty('There is no image');
    } else {
        confirmNoty(() => {
            const icon = button.find('.fa-trash').removeClass('fa-trash').addClass('fa-spinner fa-pulse');
            $.ajax({
                url: url,
                type: 'DELETE'
            }).done(function () {
                const img = button.closest('.image-footer').siblings('img');
                updateImgSrc(img, context[url]);
                context[url] = false;
                img.prop('hidden', hideable);

                successNoty('Image deleted');
            }).always(() => icon.removeClass('fa-spinner fa-pulse').addClass('fa-trash'));
        });
    }
}