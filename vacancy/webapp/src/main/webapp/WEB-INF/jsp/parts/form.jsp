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
                        <input class="form-control" type="text" name="login" id="login" placeholder="Login">
                    </div>

                    <p class="h7" id="passwordSwitcherLine" hidden><input type="checkbox" id="passwordSwitcher" onclick="enablePasswordChange($(this))"> Change password</p>

                    <div class="form-group" id="passwordField">
                        <label for="password" class="col-form-label"></label>
                        <input class="form-control" type="password" name="password" id="password" placeholder="Password">
                    </div>

                    <div class="form-group" id="roleField">
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
                <button type="button" onclick="$('#form').submit();"  id="submitButton" class="btn btn-primary">
                    <span class="fa fa-check"></span>
                    Save
                </button>
            </div>
        </div>
    </div>
</div>