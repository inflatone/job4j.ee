<div class="modal fade" tabindex="-1" id="modalForm">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="modalTitle"></h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>

            <div class="modal-body">
                <form id="form" class="border border-light" novalidate>
                    <input type="hidden" name="id" id="id"/>

                    <div class="form-group">
                        <label for="login" class="col-form-label"></label>
                        <input class="form-control" type="text" name="login" id="login" placeholder="Login" required>
                        <div class="invalid-feedback">Please enter username</div>
                    </div>

                    <p class="h7" id="passwordSwitcherLine" hidden><input type="checkbox" id="passwordSwitcher" onclick="enablePasswordChange($(this))"> Change password</p>

                    <div class="form-group" id="passwordField">
                        <label for="password" class="col-form-label"></label>
                        <input class="form-control" type="password" name="password" id="password" placeholder="Password"
                               required>
                        <div class="invalid-feedback">Please enter password</div>
                    </div>

                    <div class="form-group">
                        <label for="name" class="col-form-label"></label>
                        <input class="form-control" type="text" name="name" id="name" placeholder="Name" required>
                        <div class="invalid-feedback">Please enter name</div>
                    </div>

                    <div class="form-group">
                        <label for="role" class="col-form-label"></label>
                        <select class="form-control" name="role" id="role" required>
                        </select>
                        <div class="invalid-feedback">Please choose role</div>
                    </div>

                    <input type="hidden" name="cityId" id="cityId"/>

                    <div class="form-group">
                        <label for="country" class="col-form-label"></label>
                        <select class="form-control" name="country" id="country" required>
                        </select>
                        <div class="invalid-feedback">Please choose country</div>
                    </div>

                    <div id="cityPart" hidden>
                        <div class="form-group">
                            <label for="city" class="col-form-label"></label>
                            <select class="form-control" name="city" id="city" required>
                            </select>
                            <div class="invalid-feedback">Please choose city</div>
                        </div>

                        <p class="h7"><input type="checkbox" id="citySwitcher" onclick="switchCityForm($(this))"> Required city is not on the list</p>

                        <div class="form-group">
                            <label for="cityInput" class="col-form-label"></label>
                            <input class="form-control" type="text" name="cityInput" id="cityInput" placeholder="City" hidden>
                            <div class="invalid-feedback">Please enter city</div>
                        </div>
                    </div>
                </form>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <span class="fa fa-close"></span>
                    Cancel
                </button>
                <button type="button" id="submitButton" class="btn btn-primary">
                    <span class="fa fa-check"></span>
                    Save
                </button>
            </div>
        </div>
    </div>
</div>