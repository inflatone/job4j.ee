<div class="modal fade" tabindex="-1" id="modalPostForm">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="modalTitle"></h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>

            <div class="modal-body">
                <form id="formPost" class="border border-light" novalidate>
                    <input class="form-control" type="hidden" name="id" id="postId"/>

                    <input class="form-control" type="hidden" name="user.id" id="profileId"/>

                    <div class="form-group" title="Title">
                        <label for="title" class="col-form-label"></label>
                        <input class="form-control" type="text" name="title" id="title" placeholder="Title">
                    </div>

                    <div class="hideable-field" id="price-field">
                        <div class="hideable-field-switcher">
                            <input type="checkbox" onclick="showField($(this), context['defaultPrice'])">
                            Set price
                        </div>
                        <div class="form-group" title="Price">
                            <label for="price" class="col-form-label"></label>
                            <input class="form-control" type="number" name="price" id="price" placeholder="Price">
                        </div>
                    </div>

                    <div class="form-group" title="Message">
                        <label for="message" class="col-form-label"></label>
                        <textarea class="form-control" rows="5" name="message" id="message" placeholder="Message"></textarea>
                    </div>

                    <input class="form-control" type="hidden" name="car.id" id="carId"/>

                    <div class="form-group" title="Vendor">
                        <label for="vendorId" class="col-form-label"></label>
                        <select class="form-control" name="car.vendor.id" id="vendorId">
                            <option>Vendor</option>
                        </select>
                    </div>

                    <div class="form-group" title="Model">
                        <label for="carModel" class="col-form-label"></label>
                        <input class="form-control" type="text" name="car.model" id="carModel" placeholder="Car model">
                    </div>

                    <div class="form-group" title="Year">
                        <div class='input-group date' id='yearPicker'>
                            <label for="carYear" class="col-form-label"></label>
                            <input class="form-control" type="text" name="car.year" id="carYear" placeholder="Year">
                            <span class="input-group-addon">
                                <span class="glyphicon glyphicon-calendar"></span>
                            </span>
                        </div>
                    </div>

                    <br>

                    <div class="form-group" title="Body">
                        <label for="bodyId" class="col-form-label"></label>
                        <select class="form-control" name="car.body.id" id="bodyId">
                            <option>Body</option>
                        </select>
                    </div>

                    <div class="form-group" title="Engine">
                        <label for="engineId" class="col-form-label"></label>
                        <select class="form-control" name="car.engine.id" id="engineId">
                            <option>Engine</option>
                        </select>
                    </div>

                    <div class="form-group" title="Transmission">
                        <label for="transmissionId" class="col-form-label"></label>
                        <select class="form-control" name="car.transmission.id" id="transmissionId">
                            <option>Transmission</option>
                        </select>
                    </div>

                    <br>

                    <div class="hideable-field" id="mileage-field">
                        <div class="hideable-field-switcher">
                            <input type="checkbox" onclick="showField($(this), context['defaultMileage'])">
                            Used car
                        </div>
                        <div class="form-group" title="Mileage">
                            <label for="carMileage" class="col-form-label"></label>
                            <input class="form-control" type="number" name="car.mileage" id="carMileage" placeholder="Mileage">
                        </div>
                    </div>

                    <br>
                </form>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <span class="fa fa-close"></span>
                    Cancel
                </button>
                <button type="button" class="btn btn-primary form-submit">
                    <span class="fa fa-check"></span>
                    Save
                </button>
            </div>
        </div>
    </div>
</div>

