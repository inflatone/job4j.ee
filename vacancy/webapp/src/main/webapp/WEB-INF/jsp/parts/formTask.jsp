<div class="modal fade" tabindex="-1" id="taskModalForm">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="taskModalTitle"></h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>

            <div class="modal-body">
                <form id="taskForm" class="border border-light" novalidate>
                    <input type="hidden" name="id" id="taskId"/>

                    <div class="form-group">
                        <label for="taskKey" class="col-form-label"></label>
                        <input class="form-control" type="text" name="keyword" id="taskKey" placeholder="Search key" required>
                        <div class="invalid-feedback">Please enter search key</div>
                    </div>

                    <div class="form-group" id="taskCityField">
                        <label for="taskCity" class="col-form-label"></label>
                        <input class="form-control" type="text" name="city" id="taskCity" placeholder="Search city (not required)">
                    </div>

                    <div class="form-group" id="taskDateLimitField">
                        <label for="taskDateLimit" class="col-form-label"></label>
                        <select class="form-control" name="limit" id="taskDateLimit" required>
                            <option value hidden disabled>Limit scan from</option>
                            <option value="1">week start</option>
                            <option value="2">month start</option>
                            <option value="3">year start</option>
                        </select>
                        <div class="invalid-feedback">Please choose scan limit</div>
                    </div>

                    <p class="h7"><input type="checkbox" id="setNextLaunchCheckbox" onclick="showSetNextLaunchField()"> Change next start
                        &nbsp;&nbsp;&nbsp;
                        <input type="checkbox" id="pauseNextLaunchCheckbox" name="active"> Pause task</p>

                    <div class="form-group" id="taskNextStartField" hidden>
                        <label for="taskNextStart" class="col-form-label"></label>
                        <input class="form-control" autocomplete="off" name="launch" id="taskNextStart">
                        <div class="invalid-feedback">Please set next start time</div>
                    </div>

                    <div class="form-group" >
                        <label for="taskCronRule" class="col-form-label"></label>
                        <select class="form-control" name="cronRule" id="taskCronRule" required>
                        </select>
                        <div class="invalid-feedback">Please choose repeat frequency</div>
                    </div>

                    <div class="form-group">
                        <label for="taskProvider" class="col-form-label"></label>
                        <select class="form-control" name="provider" id="taskProvider" required>
                        </select>
                        <div class="invalid-feedback">Please choose scan source</div>
                    </div>
                </form>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <span class="fa fa-close"></span>
                    Cancel
                </button>
                <button type="button" id="taskSubmitButton" class="btn btn-primary">
                    <span class="fa fa-check"></span>
                    Save
                </button>
            </div>
        </div>
    </div>
</div>