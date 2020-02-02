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
                        <input class="form-control" type="text" name="keyword" id="taskKey" placeholder="Search key">
                    </div>

                    <div class="form-group" id="taskCityField">
                        <label for="taskCity" class="col-form-label"></label>
                        <input class="form-control" type="text" name="city" id="taskCity" placeholder="Search city (not required)">
                    </div>

                    <div class="form-group" id="taskScanFromField">
                        <button type="button" onclick="fillScanFrom(0)" class="btn btn-basic btn-sm">Today</button>
                        <button type="button" onclick="fillScanFrom(1)" class="btn btn-basic btn-sm">Week start</button>
                        <button type="button" onclick="fillScanFrom(2)" class="btn btn-basic btn-sm">Month start</button>
                        <button type="button" onclick="fillScanFrom(3)" class="btn btn-basic btn-sm">Year start</button>
                        <label for="taskScanFrom" class="col-form-label"></label>
                        <input class="form-control" autocomplete="off" name="limit" id="taskScanFrom" title="Scan from" alt="Scan from" placeholder="Scan from">
                    </div>

                    <p class="h7"><input type="checkbox" id="setNextLaunchCheckbox" onclick="showSetNextLaunchField()"> Change next start
                        &nbsp;&nbsp;&nbsp;
                        <input type="checkbox" id="pauseNextLaunchCheckbox" name="active"> Pause task</p>

                    <div class="form-group" id="taskNextStartField" hidden>
                        <button type="button" onclick="fillLaunch(-1)" class="btn btn-basic btn-sm">Default</button>
                        <button type="button" onclick="fillLaunch(0)" class="btn btn-basic btn-sm">Tomorrow</button>
                        <button type="button" onclick="fillLaunch(1)" class="btn btn-basic btn-sm">Next week start</button>
                        <button type="button" onclick="fillLaunch(2)" class="btn btn-basic btn-sm">Next month start</button>
                        <label for="taskNextStart" class="col-form-label"></label>
                        <input class="form-control" autocomplete="off" name="launch" id="taskNextStart" title="Next launch" alt="Next launch" placeholder="Next launch">
                    </div>

                    <div class="form-group" >
                        <label for="taskCronRule" class="col-form-label"></label>
                        <select class="form-control" name="cronRule" id="taskCronRule">
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="taskProvider" class="col-form-label"></label>
                        <select class="form-control" name="provider" id="taskProvider">
                        </select>
                    </div>
                </form>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">
                    <span class="fa fa-close"></span>
                    Cancel
                </button>
                <button type="button" onclick="$('#taskForm').submit();" id="taskSubmitButton" class="btn btn-primary">
                    <span class="fa fa-check"></span>
                    Save
                </button>
            </div>
        </div>
    </div>
</div>