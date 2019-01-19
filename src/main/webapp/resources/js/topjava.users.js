const ajaxUserUrl = "ajax/admin/users/";
let userDatatableApi;

// $(document).ready(function () {
$(function () {
    userDatatableApi = $("#datatable").DataTable({
        "paging": false,
        "info": true,
        "columns": [
            {
                "data": "name"
            },
            {
                "data": "email"
            },
            {
                "data": "roles"
            },
            {
                "data": "enabled"
            },
            {
                "data": "registered"
            },
            {
                "defaultContent": "Edit",
                "orderable": false
            },
            {
                "defaultContent": "Delete",
                "orderable": false
            }
        ],
        "order": [
            [
                0,
                "asc"
            ]
        ]
    });
    makeEditable();
});

function makeEditable() {
    $(".delete").click(function () {
        var tt = $(this).parents("tr");
        deleteRow($(this).parents("tr").attr("id"));
    });

    $(document).ajaxError(function (event, jqXHR, options, jsExc) {
        failNoty(jqXHR);
    });

    // solve problem with cache in IE: https://stackoverflow.com/a/4303862/548473
    $.ajaxSetup({cache: false});
}

function add() {
    $("#detailsForm").find(":input").val("");
    $("#editRow").modal();
}

function deleteRow(id) {
    $.ajax({
        url: ajaxUserUrl + id,
        type: "DELETE"
    }).done(function () {
        updateTable();
        successNoty("Deleted");
    });
}

function updateTable() {
    $.get(ajaxUserUrl, function (data) {
        userDatatableApi.clear().rows.add(data).draw();
    });
}

function save() {
    let form = $("#detailsForm");
    $.ajax({
        type: "POST",
        url: ajaxUserUrl,
        data: form.serialize()
    }).done(function () {
        $("#editRow").modal("hide");
        updateTable();
        successNoty("Saved");
    });
}

function check(check) {
    var checked = check.is(":checked");
    var id = check.parents("tr").attr("id");
    $.ajax({
        type: "POST",
        url: ajaxUserUrl + id,
        data: {'checked': checked}
    }).done(function () {
        check.parent().parent().css('text-decoration', checked ? 'none' : 'line-through');
        successNoty(checked ? "Enabled" : "Disabled");
    });
}