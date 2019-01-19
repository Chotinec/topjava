const ajaxMealUrl = "ajax/admin/meals/";
let mealDatatableApi;

$(function () {
    mealDatatableApi = $("#datatable").DataTable({
        "paging": false,
        "info": true,
        "columns": [
            {
                "data": "dateTime"
            },
            {
                "data": "description"
            },
            {
                "data": "calories"
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
        deleteRow($(this).attr("id"));
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
        url: ajaxMealUrl + id,
        type: "DELETE"
    }).done(function () {
        updateTable();
        successNoty("Deleted");
    });
}

function updateTable() {
    $.get(ajaxMealUrl, function (data) {
        mealDatatableApi.clear().rows.add(data).draw();
    });
}

function save() {
    let form = $("#detailsForm");
    $.ajax({
        type: "POST",
        url: ajaxMealUrl,
        data: form.serialize()
    }).done(function () {
        $("#editRow").modal("hide");
        updateTable();
        successNoty("Saved");
    });
}

function filter() {
    let form = $("#filterForm");
    $.ajax({
        type: "POST",
        url: ajaxMealUrl + "filter",
        data: form.serialize()
    }).done(function (data) {
        mealDatatableApi.clear().rows.add(data).draw();
        successNoty("Filtered");
    });
}

function clearFilters() {
    document.getElementById("filterForm").reset();
    updateTable();
}