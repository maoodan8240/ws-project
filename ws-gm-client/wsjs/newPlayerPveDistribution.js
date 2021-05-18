var queryPveDistributionRequest = "QueryPveDistribution$Request";


function queryPveDistribution() {
    if (!checkDate()) {
        return;
    }
    var msgContent =
        {
            createAtDate: $("#bgDate").val(),
            date: $("#edDate").val(),
            platformType: $("#platformType").val(),
            orid: $("#realmId").val(),
        };
    var msgContentJsonStr = JSON.stringify(msgContent);
    sendRequest(queryPveDistributionRequest, msgContentJsonStr, queryPveDistributionReturn);
}


function queryPveDistributionReturn(argsPass) {
    var all = {};
    var keys = [];
    var newPlayerCount = argsPass["newPlayerCount"];
    var arr = argsPass["pveIds"];
    for (var idx in  arr) {
        var l = arr[idx] + "";
        if (all.hasOwnProperty(l)) {
            all[l].count = all[l].count + 1;
        } else {
            keys.push(arr[idx]);
            all[l] = {count: 1, percent: 0};
        }
        all[l].percent = (100 * all[l].count ) / newPlayerCount;
        all[l].percent = all[l].percent.toFixed(2);
    }
    keys.sort();
    var mydata = [];
    for (var k in  keys) {
        var l = keys[k] + "";
        if (all.hasOwnProperty(l)) {
            mydata.push({
                level: l,
                count: all[l].count,
                percent: all[l].percent + "%"
            });
        }
    }
    $("#sumNew").val(newPlayerCount);
    $("#table_list_1").jqGrid('clearGridData');  // 清空数据源
    jQuery("#table_list_1").jqGrid('setGridParam', {data: mydata}).trigger("reloadGrid"); // 重新加载新的数据源并且刷新
}

$(document).ready(function () {
    var time = new Date().format("yyyyMMdd");
    $("#bgDate").val(time);
    $("#edDate").val(time);
    var mydata = [
        {
            level: "",
            count: "",
            percent: "",
        },
    ];
    loadTableData(mydata);

    queryPveDistribution();
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
        colNames: ['等级', '数量', '通过率',],
        colModel: [
            {
                name: 'level',
                index: 'level',
                width: 60,
            },
            {
                name: 'count',
                index: 'count',
                width: 60,
            },
            {
                name: 'percent',
                index: 'percent',
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
