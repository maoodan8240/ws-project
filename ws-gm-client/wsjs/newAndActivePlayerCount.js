var queryNewlyAndActivePlayerCountRequest = "QueryNewlyAndActivePlayerCount$Request";


function queryNewlyAndActivePlayerCount() {
    if (!checkDate()) {
        return;
    }
    var msgContent =
        {
            beginDate: $("#bgDate").val(),
            endDate: $("#edDate").val(),
            platformType: $("#platformType").val(),
            orid: $("#realmId").val(),
        };
    var msgContentJsonStr = JSON.stringify(msgContent);
    sendRequest(queryNewlyAndActivePlayerCountRequest, msgContentJsonStr, queryNewlyAndActivePlayerCountReturn);
}


function queryNewlyAndActivePlayerCountReturn(argsPass) {
    $("#table_list_1").jqGrid('clearGridData');  // 清空数据源
    jQuery("#table_list_1").jqGrid('setGridParam', {data: argsPass}).trigger("reloadGrid"); // 重新加载新的数据源并且刷新
}

$(document).ready(function () {
    var time = new Date().format("yyyyMMdd");
    $("#bgDate").val(time);
    $("#edDate").val(time);

    var mydata = [
        {
            date: "",
            newPlayerCount: "",
            activeDeviceCount: "",
            activeAccount: "",
        },
    ];
    loadTableData(mydata);

    queryNewlyAndActivePlayerCount();
});


function loadTableData(data) {
    $.jgrid.defaults.styleUI = 'Bootstrap';
    $("#table_list_1").jqGrid({
        data: data,
        datatype: "local",
        height: 700,
        autowidth: true,
        shrinkToFit: true,
        rowNum: 14,
        rowList: [50],
        colNames: ['日期', '新增', '活跃设备', '活跃账号'],
        colModel: [
            {
                name: 'date',
                index: 'date',
                width: 60,
            },
            {
                name: 'newPlayerCount',
                index: 'newPlayerCount',
                width: 60,
            },
            {
                name: 'activeDeviceCount',
                index: 'activeDeviceCount',
                width: 60,
            },
            {
                name: 'activeAccount',
                index: 'activeAccount',
                width: 60,
            },
        ],
        pager: "#pager_list_1",
        viewrecords: true,
        hidegrid: false,
        onSelectRow: function (rowId) {
            var rowData = $('#table_list_1').jqGrid('getRowData', rowId);
            // alert(rowId + "xxxxxxxxxx  --> " + rowData.groupActId + " --> " + rowData.groupTitle)
        }
    });

    $(window).bind('resize', function () {
        var width = $('.jqGrid_wrapper').width();
        $('#table_list_1').setGridWidth(width);
    });
}

