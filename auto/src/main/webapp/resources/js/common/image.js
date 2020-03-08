function buildImageGroup(name, imageId, hideIfAbsent, editable, url) {
    const img = `<img class="card-img-top" src="${imagesUrl}${imageId ? imageId : ''}"
                        alt="${name}" ${hideIfAbsent && !imageId ? 'hidden' : ''}>`;
    if (editable) {
        context[url] = imageId;
        const form = buildImageForm(name, url, hideIfAbsent);
        return img.concat(form);
    }
    return img;
}

function buildImageForm(name, url, hideIfAbsent) {
    return `<div class="image-footer">
                <form enctype="multipart/form-data">
                    <div class="form-group mb-2"> 
                        <div class="custom-file">
                            <input id="image-${url}" name="${name}" type="file"
                                   onchange="pasteFilename($(this))"
                                   class="custom-file-input form-control-sm">
                            <label class="custom-file-label" for="image-${url}">Choose file</label>
                        </div>
                    </div>
                </form>
                <div class="btn-group special btn-group-sm btn-block header-group" role="group">
                    <button class="btn btn-outline-dark btn-twin" type="button"
                            onclick="uploadAndPasteImgByButton($(this), '${url}')">
                        <span class="fa fa-upload"></span>&nbsp; Upload
                    </button>
                    <button class="btn btn-outline-danger btn-twin" type="button"
                            onclick="deleteAndCleanImgByButton($(this), '${url}', ${hideIfAbsent})">
                        <span class="fa fa-trash"></span>&nbsp; Clean
                     </button>
                </div>
            </div>`;
}

function pasteFilename(input) {
    const fileName = input.val().split("\\").pop();
    input.siblings('label').addClass('selected').html(fileName);
}

function updateImgSrc(img, id) {
    // https://stackoverflow.com/a/3228167
    img.attr('src', imagesUrl + id);
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
        $.ajax({
            url: url,
            type: 'POST',
            encType: "multipart/form-data",
            data: new FormData(form[0]), // extract from jquery
            cache: false,
            processData: false,
            contentType: false
        }).done(function (data) {
            context[url] = data.id;
            updateImgSrc(img, data.id);
            img.prop('hidden', false);
            form.find('input').val('');
            label.removeClass('selected').html('Choose file');
            successNoty('Image uploaded');
        }).fail(function (jqXHR) {
            pasteInvalidFeedback(form.find('input'), jqXHR.responseJSON.message)
        })
    }
}

function deleteAndCleanImgByButton(button, url, hideIfAbsent) {
    if (!context[url]) {
        alertNoty('There is no image');
    } else {
        confirmNoty(() => {
            const img = button.closest('.image-footer').siblings('img');
            $.ajax({
                url: url,
                type: 'DELETE'
            }).done(function () {
                context[url] = undefined;
                updateImgSrc(img, '');
                hideImageIfAbsent(img, hideIfAbsent, true);
                successNoty('Image deleted');
            })
        });
    }
}

function hideImageIfAbsent(img, hide, absent) {
    if (hide && absent) {
        img.prop('hidden', true);
    }
}