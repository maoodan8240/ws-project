var queryPaymentByDateRequest = "QueryPaymentByDate$Request";


function queryPaymentByDate() {
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
    sendRequest(queryPaymentByDateRequest, msgContentJsonStr, queryPaymentByDateReturn);
}


function queryPaymentByDateReturn(argsPass) {
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
            newPlayerNewPayment: "",
            paymentCount: "",
            paymentSum: "",
            newPlayerCount: "",
            activeAccount: "",
        },
    ];
    loadTableData(mydata);

    queryPaymentByDate();
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
        colNames: ['日期', '新增玩家中充值人数', '充值总人数', '总流水', '新增人数', '活跃帐号'],
        colModel: [
            {
                name: 'date',
                index: 'date',
                width: 60,
            },
            {
                name: 'newPlayerNewPayment',
                index: 'newPlayerNewPayment',
                width: 60,
            },
            {
                name: 'paymentCount',
                index: 'paymentCount',
                width: 60,
            },
            {
                name: 'paymentSum',
                index: 'paymentSum',
                width: 60,
            },
            {
                name: 'newPlayerCount',
                index: 'newPlayerCount',
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
