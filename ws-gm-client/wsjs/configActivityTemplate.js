var AddActivityMsgRequest = "AddActivityMsg$Request";


function addNewGroupActConfig() {
    var msgContent =
        {
            "subActivityConfig": getSubActArrContent(),
            "groupActivityConfig": getGroupActContent(),
        };
    var msgContentJsonStr = JSON.stringify(msgContent);
    sendRequest(AddActivityMsgRequest, msgContentJsonStr, addNewGroupActConfigReturn);
}


function addNewGroupActConfigReturn(argsPass){
    alert(argsPass)
}


function queryGroupActConfig() {
    var msgContent =
        {
        };
    var msgContentJsonStr = JSON.stringify(msgContent);
    sendRequest(AddActivityMsgRequest, msgContentJsonStr, queryGroupActConfigReturn);
}


function queryGroupActConfigReturn(argsPass){
    alert(argsPass)
}






/**
 * 保存新增的子活动
 */
function saveSubActConfig() {
    // ------------------------------------ check ------------------------------------
    if ($("#subActTitle").val().length === 0) {
        alert("子活动内容 不能为空！");
        return;
    }
    if ($("#selectedItemsTxt").val().length === 0) {
        alert("任务完成奖励 不能为空！");
        return;
    }
    if ($("#subActCompletePlayerLevel").val().length === 0) {
        $("#subActCompletePlayerLevel").val(0);
    }
    if ($("#subActCompletePlayerVipLevel").val().length === 0) {
        $("#subActCompletePlayerVipLevel").val(0);
    }
    // ------------------------------------ logic ------------------------------------
    var subActObj = new SubActObj();
    subActObj.subActTitle = $("#subActTitle").val();
    subActObj.subActComplateType = $("#subActComplateType").val();
    subActObj.subActComplateNumber = $("#subActComplateNumber").val();
    subActObj.subActExchangeConsume = $("#subActExchangeConsume").val();
    subActObj.subActCanExchangeConsumeTs = $("#subActCanExchangeConsumeTs").val();
    subActObj.subActCompletePlayerLevel = $("#subActCompletePlayerLevel").val();
    subActObj.subActCompletePlayerVipLevel = $("#subActCompletePlayerVipLevel").val();
    subActObj.selectedItemsTxt = $("#selectedItemsTxt").val();
    subActObj.selectedItems = $("#selectedItems").val();
    addSubActConfRow(subActObj);
    clearSubActConfig();
}


function clearSubActConfig() {
    $("#subActTitle").val("");
    $("#subActComplateType").val("");
    $("#subActComplateNumber").val("");
    $("#subActExchangeConsume").val("");
    $("#subActCanExchangeConsumeTs").val("");
    $("#subActCompletePlayerLevel").val("");
    $("#subActCompletePlayerVipLevel").val("");
    $("#selectedItemsTxt").val("");
    $("#selectedItems").val("");
}

/**
 * 新增子活动行
 * @param subActObj
 */
function addSubActConfRow(subActObj) {
    var saStr = JSON.stringify(subActObj);
    var subActTable = document.getElementById("subActTable");
    var len = subActTable.rows.length;
    var newIdx = len;
    var subActTableBody = document.getElementById("subActTableBody");
    $("#subActTableBody").append("<tr><td>" + newIdx + "</td>" +
        "<td>" + subActObj.subActTitle + "</td>" +
        "<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
        "<a href='#' onclick='modifySubActConfigRow(this)'><i class='fa text-navy'>修改</i></a>" +
        "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='#' onclick='delSubActConfigRow(this)'><i class='fa' style='color: #E33737'>删除</i></a>" +
        "<input type='hidden' id='subActAllContent_'" + newIdx + " name='subActAllContent_'" + newIdx + " value ='" + saStr + "' </td></tr>");
}

/**
 * 删除子活动行
 * @param delBtObj
 */
function delSubActConfigRow(delBtObj) {
    var subActTable = document.getElementById("subActTable");
    var tdObj = delBtObj.parentNode;  // td
    var trObj = tdObj.parentNode      // tr
    var idx = trObj.rowIndex;         // rowIdx
    subActTable.deleteRow(idx);
}


/**
 * 修改子活动行
 * @param modifyBtObj
 */
function modifySubActConfigRow(modifyBtObj) {
    var tdObj = modifyBtObj.parentNode; // td
    var inputObj = tdObj.childNodes.item(4);
    var subActObj = JSON.parse(inputObj.value);
    $("#subActTitle").val(subActObj.subActTitle);
    $("#subActComplateType").val(subActObj.subActComplateType);
    $("#subActComplateNumber").val(subActObj.subActComplateNumber);
    $("#subActExchangeConsume").val(subActObj.subActExchangeConsume);
    $("#subActCanExchangeConsumeTs").val(subActObj.subActCanExchangeConsumeTs);
    $("#subActCompletePlayerLevel").val(subActObj.subActCompletePlayerLevel);
    $("#subActCompletePlayerVipLevel").val(subActObj.subActCompletePlayerVipLevel);
    $("#selectedItemsTxt").val(subActObj.selectedItemsTxt);
    $("#selectedItems").val(subActObj.selectedItems);
}


var SubActObj = function () {
    this.subActTitle = "";                         // 子活动内容
    this.subActComplateType = "";                  // 子活动完成类型
    this.subActComplateNumber = "";                // 子活动完成数量
    this.subActExchangeConsume = "";               // 兑换消耗
    this.subActCanExchangeConsumeTs = "";          // 可以兑换的次数
    this.subActCompletePlayerLevel = "";           // 完成条件（玩家等级）
    this.subActCompletePlayerVipLevel = "";        // 完成条件（玩家VIP等级）
    this.selectedItemsTxt = "";                    // idAndCout
    this.selectedItems = "";                       // 任务完成奖励 lable
}


var GroupActObj = function () {
    this.groupAcId = "";              // 组活动Id
    this.groupActType = "";           // 组活动类型
    this.tagTitle = "";               // 活动标签标题
    this.contentTitle = "";           // 活动内容标题
    this.contentDesc = "";            // 活动内容的描述
    this.tagIcon = "";                // 活动标签图标
}


$(document).ready(function () {
    queryGroupActConfig();
subActObj
    $.jgrid.defaults.styleUI = 'Bootstrap';
    var mydata = [
        {
            groupActId: "13",
            groupTitle: "aaaaaaaaaaaaaaaaaaaaaaaa"
        },
        {
            groupActId: "2123",
            groupTitle: "bbbbbbbbbbbbbbbbbbbbbb"
        },
        {
            groupActId: "31231",
            groupTitle: "ccccccccccccccccccccc"
        },
        {
            groupActId: "41",
            groupTitle: "ddddddddddddddddddddd"
        },
        {
            groupActId: "51",
            groupTitle: "ffffffffffffffffffffffff"
        }
    ];

    $("#table_list_1").jqGrid({
        data: mydata,
        datatype: "local",
        height: 650,
        autowidth: true,
        shrinkToFit: true,
        rowNum: 14,
        rowList: [10, 30, 50],
        colNames: ['组活动Id', '组活动标题'],
        colModel: [
            {
                name: 'groupActId',
                index: 'groupActId',
                width: 60,
                sorttype: "int"
            },
            {
                name: 'groupTitle',
                index: 'groupTitle',
                width: 90,
            }
        ],
        pager: "#pager_list_1",
        viewrecords: true,
        hidegrid: false,
        onSelectRow: function (rowId) {
            var rowData = $('#table_list_1').jqGrid('getRowData', rowId);
            alert(rowId + "xxxxxxxxxx  --> " + rowData.groupActId + " --> " + rowData.groupTitle)
        }
    });

    $(window).bind('resize', function () {
        var width = $('.jqGrid_wrapper').width();
        $('#table_list_1').setGridWidth(width);
    });
});


function getSubActArrContent() {
    var all = [];
    var subActTable = document.getElementById("subActTable");
    var len = subActTable.rows.length;
    for (var i = 1; i < len; i++) {
        var row = subActTable.rows[i];
        var inputObj = row.childNodes.item(2).childNodes.item(4);
        var subActObj = JSON.parse(inputObj.value);
        all[i - 1] = subActObj;
    }
    return all;
}


function getGroupActContent() {
    var groupActObj = new GroupActObj();
    groupActObj.groupAcId = $("#groupAcId").val();
    groupActObj.groupActType = $("#groupActType").val();
    groupActObj.tagTitle = $("#tagTitle").val();
    groupActObj.contentTitle = $("#contentTitle").val();
    groupActObj.contentDesc = $("#contentDesc").val();
    groupActObj.tagIcon = $("#tagIcon").val();
    return groupActObj;
}


function fillGroupActContent(rowId) {
    var rowData = $('#table_list_1').jqGrid('getRowData', rowId);
    alert(rowId + "xxxxxxxxxx  --> " + rowData.groupActId + " --> " + rowData.groupTitle)

}