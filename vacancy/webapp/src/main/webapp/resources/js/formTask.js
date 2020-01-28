const taskUrl = "task";

let taskFormElement, taskForm, nextLaunchCached, isUpdate;

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
    id = id ? id : taskId;
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
        $.each(data, function (key, value) { // pauseNextLaunchCheckbox
            switch (key) {
                case 'ruleOrdinal':
                    taskForm.find("select[name='cronRule']").val(value);
                    break;
                case 'source':
                    taskForm.find("select[name='provider']").val(value.id);
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
        nextLaunchCached = data.launch;
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
        launch: grabDate(),
        active: !$('#pauseNextLaunchCheckbox').is(':checked')
    };
    if (!isUpdate) {
        result.keyword = $('#taskKey').val();
        result.city = $('#taskCity').val();
        result.limit = grabDateLimit().getTime();
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

function grabDateLimit() {
    const value = $('#taskDateLimit').val();
    const now = new Date();
    now.setHours(0, 0, 0, 0);
    switch (value) {
        case '1':
            return getMondayDate(now);
        case '2':
            return getFirstDayOfMonthDate(now);
        default:
            return getFirstDayOfYearDate(now);
    }
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
    hideFormField('taskNextStart', true, true);
}