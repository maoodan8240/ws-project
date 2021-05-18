var queryRechargeRecordRequest = "QueryRechargeRecord$Request";


function queryRechargeRecord() {
    if (!checkDate()) {
        return;
    }
    var msgContent =
        {
            beginDate: $("#bgDate").val(),
            endDate: $("#edDate").val(),
            platformType: $("#platformType").val(),
            orid: $("#realmId").val(),
            simpleId: $("#playerSimpleId").val(),
        };
    var msgContentJsonStr = JSON.stringify(msgContent);
    sendRequest(queryRechargeRecordRequest, msgContentJsonStr, queryRechargeRecordReturn);
}


function queryRechargeRecordReturn(argsPass) {
    $("#table_list_1").jqGrid('clearGridData');  // 清空数据源
    jQuery("#table_list_1").jqGrid('setGridParam', {data: argsPass}).trigger("reloadGrid"); // 重新加载新的数据源并且刷新
    $("#sumRmb").val(formatMoney(sunRmb(argsPass), 0));
}

$(document).ready(function () {
    var time = new Date().format("yyyyMMdd");
    $("#bgDate").val(time);
    $("#edDate").val(time);

    var mydata = [
        {
            date: "",
            platformType: "",
            outerRealmId: "",
            simpleId: "",
            goodName: "",
            rmb: "",
            outerOrderId: "",
            endDateTime: "",
        },
    ];
    loadTableData(mydata);

    queryRechargeRecord();
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
        rowList: [15, 30, 45, 60],
        colNames: ['日期', '渠道', '服id', '玩家Id', '物品名称', 'RMB', '外部订单号', '订单结算时间'],
        colModel: [
            {
                name: 'date',
                index: 'date',
                width: 60,
            },
            {
                name: 'platformType',
                index: 'platformType',
                width: 60,
            },
            {
                name: 'outerRealmId',
                index: 'outerRealmId',
                width: 60,
            },
            {
                name: 'simpleId',
                index: 'simpleId',
                width: 60,
            },
            {
                name: 'goodName',
                index: 'goodName',
                width: 90,
            },
            {
                name: 'rmb',
                index: 'rmb',
                width: 60,
            },
            {
                name: 'outerOrderId',
                index: 'outerOrderId',
                width: 90,
            },
            {
                name: 'endDateTime',
                index: 'endDateTime',
                width: 90,
                formatter: function (cellValue, options, rowObject) {
                    return dateFormat(cellValue);
                },
                // formatoptions: {srcformat: 'YmdHis', newformat: 'Y-m-d H:i:s'},
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


function sunRmb(data) {
    var sum = 0;
    for (var d in data) {
        sum += data[d].rmb;
    }
    return sum;
}
