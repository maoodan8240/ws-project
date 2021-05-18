var allData

/**
 * 构建itemId-name-名称 数组对象
 * @returns {Array}
 */
function chooseItemAndSetCount() {
    var data = new Array();
    var num = 0;
    for (var tpId in  ItemBagKey) {
        num++;
        var name = ItemBagKey[tpId];
        var one = {};
        one["index"] = num;
        one["tpId"] = tpId;
        one["name"] = toNotNullAndUndefined(name);
        one["count1"] = 0;
        one["count2"] = num;
        data.push(one)
    }
    return data;
}

$(document).ready(function () {
    allData = chooseItemAndSetCount();
    loadTableData(allData);
});


/**
 * 表格数据加载
 * @param dataSource
 */
function loadTableData(dataSource) {
    $('#sdfsdfs').dataTable({
        "data": dataSource,
        "columns": [{"data": 'index'}, {"data": 'tpId'}, {"data": 'name'}, {"data": 'count1'}, {"data": 'count2'}],
        "columnDefs": [
            {
                "targets": [4], "data": "count2", "render": function (data, type, full) { // 返回自定义内容
                return "<input type='text' id='ct" + data + "' class='form-control' onkeyup='onlyIntegerKeyUp(event)' onchange='countObjOnchange(this)'/>";
            },
            }],
        'aaSorting': [[0, 'asc'], [3, 'desc']],
    });
}


var allChangedData = {}

/**
 * 设置已经填写的数量
 * @param self
 */
function countObjOnchange(self) {
    var numValue = parseInt(String(self.value), 10);
    var indexObj = self.parentNode.parentNode.childNodes.item(0)
    var index = indexObj.innerHTML;
    console.log(self.id + "--" + numValue)
    var one = allData[index - 1];
    one["count1"] = numValue;
    var count1Obj = self.parentNode.parentNode.childNodes.item(3);
    count1Obj.innerHTML = numValue;
    allChangedData[index] = numValue;
    if (numValue == 0) {
        delete  allChangedData[index]
    }
    updateSelectedItem();

}

function updateSelectedItem() {
    var strLb = "";
    var strTxt = "";
    for (var k in allChangedData) {
        var one = allData[k - 1];
        strLb += one["name"] + ":" + allChangedData[k] + ", "
        strTxt += one["tpId"] + ":" + allChangedData[k] + ",";
    }
    strLb = strLb.substring(0, strLb.lastIndexOf(","));
    strTxt = strTxt.substring(0, strTxt.lastIndexOf(","));
    $('#selectedItems').html(strLb)
    setDataBack($('#selectedItems').html(), strTxt);
}


function clearTable() {
    allChangedData = {};
    setDataBack("", "");
    $('#selectedItems').html("");
    allData = chooseItemAndSetCount();
    sortTableCountFiled();
}


function sortTableCountFiled() {
    $('#sdfsdfs').dataTable().fnClearTable();
    $('#sdfsdfs').dataTable().fnDestroy();
    loadTableData(allData);
    $('#sdfsdfs').dataTable().fnSort([[3, 'desc']]);
}


function setDataBack(strLb, strTxt) {
    var parentHtml = window.opener;
    parentHtml.document.getElementById("selectedItems").value = strLb;
    parentHtml.document.getElementById("selectedItemsTxt").value = strTxt;

}