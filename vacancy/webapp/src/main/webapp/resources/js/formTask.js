let taskFormElement, taskForm, nextStartCache, isUpdate;

$(function () {
    taskFormElement = document.getElementById('taskForm');
    addFormValidator(taskFormElement, "taskSubmitButton", sendTaskForm);
    loadTaskFormResources();
    taskForm = $('#taskForm');

    $("#taskNextStart").datetimepicker({
        format: 'Y-m-d H:i'
    })
});

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
    $("#taskModalTitle").html("Edit task");
    $.get(taskUrl + '?action=find&id=' + id + (profileId ? '&userId=' + profileId : ''), prepareTaskFields);
    $('#taskModalForm').modal();
}

function prepareTaskFields(data) {
    isUpdate = !!data;
    resetForm(taskFormElement);
    resetSwitchers();
    if (!data) {
        taskForm.find(':input', ':select').val('');
    } else {
        $.each(data, function (key, value) {
            switch (key) {
                case 'ruleOrdinal':
                    taskForm.find("select[name='cronRule']").val(value);
                    break;
                case 'source':
                    taskForm.find("select[name='provider']").val(value.id);
                    break;
                default:
                    taskForm.find("input[name='" + key + "']").val(value);
            }
        });
        nextStartCache = data.launch;
        if (!nextStartCache) {
            enableNextLaunchFieldSwitcher(false);
            $('#pauseNextLaunchCheckbox').prop('checked', true);
        }
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
    const isNew = !$('#taskId').val();
    $.ajax({
        type: 'POST',
        url: "task?action=save",
        data: JSON.stringify(grabTask(isNew)),
        contentType: 'application/json'
    }).done(function () {
        $("#taskModalForm").modal('hide');
        context.afterTaskFormSuccess();
        successNoty('Data saved');
    });
}


function grabTask(isNew) {
    const result = {
        rule: $('#taskCronRule').val(),
        launch: $('#setNextLaunchCheckbox').is(':checked') || !isNew ? new Date($('#taskNextStart').val()).getTime() : null
    };
    if (isNew) {
        result.keyword = $('#taskKey').val();
        result.city = $('#taskCity').val();
        result.limit = grabDateLimit();
        result.source = {
            id: $('#taskProvider').val()
        };
    } else {
        result.id = $('#taskId').val();
    }
    if (profileId) {
        result.user = {id: profileId};
    }
    return result;
}

function grabDateLimit() {
    const value = $('#taskDateLimit').val();
    const now = new Date();
    now.setHours(0, 0, 0, 0);
    let res;
    switch (value) {
        case '1':
            res = getMondayDate(now);
            break;
        case '2':
            res = getFirstDayOfMonthDate(now);
            break;
        default:
            res = getFirstDayOfYearDate(now);
    }
    return res.getTime();
}

// https://stackoverflow.com/a/4156516/10375242
function getMondayDate(d) {
    d = new Date(d);
    const day = d.getDay(),
        diff = d.getDate() - day + (day === 0 ? -6 : 1); // adjust when day is sunday
    return new Date(d.setDate(diff));
}

function getFirstDayOfMonthDate(d) {
    d = new Date(d);
    return new Date(d.setDate(1));
}

function getFirstDayOfYearDate(d) {
    d = new Date(d);
    d.setDate(1);
    d.setMonth(0);
    return d;
}

function showSetNextLaunchField() {
    const checked = $('#setNextLaunchCheckbox').is(':checked');
    hideFormField('taskNextStart', !checked, true);
    if (checked) {
        $('#pauseNextLaunchCheckbox').prop('checked', !checked);
    } else if (isUpdate && !nextStartCache) {
        $('#pauseNextLaunchCheckbox').prop('checked', true);
    }
}

//  TODO replace this feature on pause/unpause task button as checkbox
function cancelNextLaunch() {
    const checked = $('#pauseNextLaunchCheckbox').is(':checked');
    const nextStart = $('#taskNextStart');
    if (checked) {
        nextStartCache = nextStart.val();
        nextStart.val('');
    } else {
        nextStart.val(nextStartCache);
    }
    hideFormField('taskNextStart', checked, true);
    enableNextLaunchFieldSwitcher(!checked);
}

function enableNextLaunchFieldSwitcher(enabled) {
    const nextTimeSwitcher = $('#setNextLaunchCheckbox');
    nextTimeSwitcher.prop('checked', enabled);
    nextTimeSwitcher.prop('disabled', !enabled);
}

function resetSwitchers() {
    $('#pauseNextLaunchCheckbox').prop('checked', false);
    const setLaunchCheckbox = $('#setNextLaunchCheckbox');
    setLaunchCheckbox.prop('checked', false);
    setLaunchCheckbox.prop('disabled', false);
    hideFormField('taskNextStart', true, true);
}