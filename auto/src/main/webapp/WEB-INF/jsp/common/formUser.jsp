<div class="modal fade" tabindex="-1" id="modalUserForm">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="modalTitle"></h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>

            <div class="modal-body">
                <form id="formUser" class="border border-light" novalidate>
                    <input class="form-control" type="hidden" name="id" id="id"/>

                    <div class="form-group" title="Login">
                        <label for="login" class="col-form-label"></label>
                        <input class="form-control" type="text" name="login" id="login" placeholder="Login">
                    </div>

                    <div class="form-group" title="Name">
                        <label for="name" class="col-form-label"></label>
                        <input class="form-control" type="text" name="name" id="name" placeholder="Name">
                    </div>

                    <div class="hideable-field" id="password-field">
                        <div class="hideable-field-switcher">
                            <input type="checkbox" onclick="showField($(this))">
                            Change password
                        </div>
                        <div class="form-group" title="Password">
                            <label for="password" class="col-form-label"></label>
                            <input class="form-control" type="password" name="password" id="password"
                                   placeholder="Password">
                        </div>
                    </div>

                    <div class="form-group" title="Role">
                        <label for="role" class="col-form-label"></label>
                        <select class="form-control" name="role" id="role">
                        </select>
                    </div>
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