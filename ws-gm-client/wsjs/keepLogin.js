var queryRemainDataRequest = "QueryRemainData$Request";


function queryRemainData() {
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
    sendRequest(queryRemainDataRequest, msgContentJsonStr, queryRemainDataReturn);
}


function queryRemainDataReturn(argsPass) {
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
            dayNewly: "",
            day1Remain: "",
            day2Remain: "",
            day3Remain: "",
            day4Remain: "",
            day5Remain: "",
            day6Remain: "",
            day7Remain: "",
            day14Remain: "",
            day30Remain: "",
        },
    ];
    loadTableData(mydata);

    queryRemainData();
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
        colNames: ['日期', '新增用户', '次日留存', '2日留存', '3日留存', '4日留存', '5日留存', '6日留存', '7日留存', '14日留存', '30日留存'],
        colModel: [
            {
                name: 'date',
                index: 'date',
                width: 60,
            },
            {
                name: 'dayNewly',
                index: 'dayNewly',
                width: 60,
            },
            {
                name: 'day1Remain',
                index: 'day1Remain',
                width: 60,
                formatter: "currency", formatoptions: {decimalPlaces: 2, suffix: "%"},
            },
            {
                name: 'day2Remain',
                index: 'day2Remain',
                width: 60,
                formatter: "currency", formatoptions: {decimalPlaces: 2, suffix: "%"},
            },
            {
                name: 'day3Remain',
                index: 'day3Remain',
                width: 60,
                formatter: "currency", formatoptions: {decimalPlaces: 2, suffix: "%"},
            },
            {
                name: 'day4Remain',
                index: 'day4Remain',
                width: 60,
                formatter: "currency", formatoptions: {decimalPlaces: 2, suffix: "%"},
            },
            {
                name: 'day5Remain',
                index: 'day5Remain',
                width: 60,
                formatter: "currency", formatoptions: {decimalPlaces: 2, suffix: "%"},
            },
            {
                name: 'day6Remain',
                index: 'day6Remain',
                width: 60,
                formatter: "currency", formatoptions: {decimalPlaces: 2, suffix: "%"},
            },
            {
                name: 'day7Remain',
                index: 'day7Remain',
                width: 60,
                formatter: "currency", formatoptions: {decimalPlaces: 2, suffix: "%"},
            },
            {
                name: 'day14Remain',
                index: 'day14Remain',
                width: 60,
                formatter: "currency", formatoptions: {decimalPlaces: 2, suffix: "%"},
            },
            {
                name: 'day30Remain',
                index: 'day30Remain',
                width: 60,
                formatter: "currency", formatoptions: {decimalPlaces: 2, suffix: "%"},
            }
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
