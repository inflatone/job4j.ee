const taskUrl = "task";

let taskForm, nextLaunchCached, isUpdate, taskFormValidator;

$(function () {
    taskForm = $('#taskForm');
    prepareDateTimeField();
    loadTaskFormResources();

    taskFormValidator = addFormValidator(sendTaskForm, taskForm, {
            keyword: "required",
            limit: {
                required: true,
                maxDate: ['yyyy-MM-dd HH:mm', 0]
            },
            launch: {
                minDate: ['yyyy-MM-dd HH:mm', 0]
            },
            cronRule: "required",
            provider: "required"
        },
        {
            keyword: "Please enter search key",
            limit: {
                required: "Please fill scan from field",
                maxDate: "Scan from must be earlier then now"
            },
            launch: "Please set next start time after now (or leave it blank)",
            cronRule: "Please choose repeat frequency",
            provider: "Please choose scan source",
        });

});


function prepareDateTimeField() {
    $("#taskNextStart").datetimepicker({
        dateFormat: 'Y-m-d',
        timeFormat: 'H:i',
        format: 'Y-m-d H:i',
        minDate: '-0',
    });

    $("#taskScanFrom").datetimepicker({
        format: 'Y-m-d H:i',
        maxDate: '+0',
    });

    // https://stackoverflow.com/a/26060715/10375242
    $.validator.addMethod("maxDate", function (value, element, params) {
            if (!params[0])
                throw 'params missing dateFormat';
            if (typeof (params[1]) == 'undefined')
                throw 'params missing maxDate';
            const dateFormat = params[0];
            let maxDate = params[1];
            if (maxDate === 0) {
                maxDate = new Date();
            }
            if (typeof (params[2]) == 'undefined') {
                params[2] = $.format.toBrowserTimeZone(maxDate, dateFormat);
            }
            try {
                return value.length === 0 || new Date(value) < maxDate;
            } catch (e) {
                return false;
            }
        }, 'Must be before {2}'
    );

    $.validator.addMethod("minDate", function (value, element, params) {
            if (!params[0])
                throw 'params missing dateFormat';
            if (typeof (params[1]) == 'undefined')
                throw 'params missing maxDate';
            const dateFormat = params[0];
            let minDate = params[1];
            if (minDate === 0) {
                minDate = new Date();
            }
            if (typeof (params[2]) == 'undefined') {
                params[2] = $.format.toBrowserTimeZone(minDate, dateFormat);
            }
            try {
                return value.length === 0 || new Date(value) > minDate;
            } catch (e) {
                return false;
            }
        }, 'Must be after {2}'
    );
}


function loadTaskFormResources() {
    $.get(ajaxUrl + '?form=task', function (data) {
        fillSelectField('taskCronRule', 'Repeat frequency', data.rules);
        fillSelectField('taskProvider', 'Scan source', data.sources, true);
    })
}

function openAddTaskForm() {
    $("#taskModalTitle").html("Add task");
    prepareTaskFields();
    $("#taskModalForm").modal();
}

function openEditTaskForm(id) {
    id = id ? id : taskId;
    $("#taskModalTitle").html("Edit task");
    $.get(taskUrl + '?action=find&id=' + id + (profileId ? '&userId=' + profileId : ''), prepareTaskFields);
    $('#taskModalForm').modal();
}

function prepareTaskFields(data) {
    isUpdate = !!data;
    resetForm(taskForm, taskFormValidator);
    resetSwitchers();
    if (!isUpdate) {
        taskForm.find(':input', ':select').val('');
    } else {
        $.each(data, function (key, value) { // pauseNextLaunchCheckbox
            switch (key) {
                case 'ruleOrdinal':
                    taskForm.find("select[name='cronRule']").val(value);
                    break;
                case 'source':
                    taskForm.find("select[name='provider']").val(value.id);
                    break;
                case 'launch':
                    nextLaunchCached = timestampAsFormattedDate(data.launch);
                    taskForm.find("input[name='launch']").val(nextLaunchCached);
                    break;
                case 'limit':
                    taskForm.find("input[name='limit']").val(timestampAsFormattedDate(value));
                    break;
                case 'active': {
                    const checkbox = taskForm.find("input[name='active']");
                    checkbox.prop('checked', !value);
                    checkbox.click(function () {
                        cleanNextLaunch(checkbox);
                    });
                    if (!value) {
                        enableNextLaunchFieldSwitcher(false);
                    }
                    break;
                }
                default:
                    taskForm.find("input[name='" + key + "']").val(value);
            }
        });

    }
    showTaskFields(data);
}

function showTaskFields(data) {
    const isNew = !data;
    $('#taskKey').prop('disabled', !isNew);
    $('#taskProvider').prop('disabled', !isNew);

    $('#taskCity').prop('disabled', !isNew);
    $('#taskCityField').prop('hidden', !isNew && !data.city);

    hideFormField('taskDateLimit', !isNew, true);
}

function sendTaskForm() {
    $.ajax({
        type: 'POST',
        url: "task?action=save",
        data: JSON.stringify(grabTask()),
        contentType: 'application/json'
    }).done(function () {
        $("#taskModalForm").modal('hide');
        context.afterTaskFormSuccess();
        successNoty('Data saved');
    });
}


function grabTask() {
    const result = {
        id: $('#taskId').val(),
        rule: $('#taskCronRule').val(),
        limit: new Date($('#taskScanFrom').val()).getTime(),
        launch: grabDate(),
        active: !$('#pauseNextLaunchCheckbox').is(':checked')
    };
    if (!isUpdate) {
        result.keyword = $('#taskKey').val();
        result.city = $('#taskCity').val();
        result.source = {
            id: $('#taskProvider').val()
        };
    }
    if (profileId) {
        result.user = {id: profileId};
    }
    return result;
}

function grabDate() {
    const line = $('#setNextLaunchCheckbox').is(':checked') ? $('#taskNextStart').val() : (isUpdate ? nextLaunchCached : null);
    return line == null ? null : new Date(line).getTime();
}

function fillScanFrom(num) {
    $('#taskScanFrom').val(timestampAsFormattedDate(buildDateLimit(num)));
    $('#taskForm').valid();
}


function buildDateLimit(num) {
    const now = new Date();
    now.setHours(0, 0, 0, 0);
    switch (num) {
        case 0: // today
            return now;
        case 1: // week start
            // https://stackoverflow.com/a/4156516/10375242
            const day = now.getDay(),
                diff = now.getDate() - day + (day === 0 ? -6 : 1); // adjust when day is sunday
            return now.setDate(diff);
        case 2: // month start
            return now.setDate(1);
        default: // year start
            now.setDate(1);
            return now.setMonth(0);
    }
}

function fillLaunch(num) {
    const launch = buildLaunch(num);
    $('#taskNextStart').val(!launch ? '' : timestampAsFormattedDate(launch));
    $('#taskForm').valid();
}

function buildLaunch(num) {
    const now = new Date();
    now.setHours(0, 0, 0, 0);
    switch (num) {
        case 0: // tomorrow
            return now.setDate(now.getDate() + 1);
        case 1: // next week start
            // https://stackoverflow.com/a/4156516/10375242
            const day = now.getDay(),
                diff = now.getDate() - day + (day === 0 ? 1 : 8); // adjust when day is sunday
            return now.setDate(diff);
        case 2: // next month start
            now.setMonth(now.getMonth() + 1);
            return now.setDate(1);
        default:
            return undefined;
    }
}

function showSetNextLaunchField() {
    const checked = $('#setNextLaunchCheckbox').is(':checked');
    hideFormField('taskNextStart', !checked);
    if (checked) {
        $('#taskNextStart').val(nextLaunchCached);
        $('#pauseNextLaunchCheckbox').prop('checked', !checked);
    }
}

function cleanNextLaunch(checkbox) {
    const checked = checkbox.is(':checked');
    const nextStart = $('#taskNextStart');
    nextStart.val(checked ? null : nextLaunchCached);
    hideFormField('taskNextStart', checked, true);
    enableNextLaunchFieldSwitcher(!checked);
}

function enableNextLaunchFieldSwitcher(enabled) {
    const nextTimeSwitcher = $('#setNextLaunchCheckbox');
    nextTimeSwitcher.prop('checked', enabled);
    nextTimeSwitcher.prop('disabled', !enabled);
}

function resetSwitchers() {
    const setLaunchCheckbox = $('#setNextLaunchCheckbox');
    setLaunchCheckbox.prop('checked', false);
    setLaunchCheckbox.prop('disabled', false);
    hideFormField('taskNextStart', true);
}