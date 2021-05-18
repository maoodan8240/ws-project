
function queryPlayerBasicInfo() {
    clear();
    var sessionId = getCookie("sessionId");
    console.log(" sessionId ======:" + sessionId);
    var msgContent =
        {
            "simpleId": $("#simpleId").val() // 玩家简单Id
        };
    var msgContentJsonStr = JSON.stringify(msgContent);
    sendRequest(QueryPlayerBasicInfoRequestMsg , msgContentJsonStr, queryPlayerBasicInfoReturn);
}

function queryPlayerBasicInfoReturn(dataJsonArr) {
    console.log(" dataJson返回的消息:" + dataJsonArr);
    if (dataJsonArr == null) {
        return;
    }
    var playerInfo = dataJsonArr[0];
    var itembagInfo = dataJsonArr[1];
    var resourcePointInfo = dataJsonArr[2];
    var herosInfo = dataJsonArr[3];

    showPlayerInfo(playerInfo);
    showItemBagPlainInfo(itembagInfo);
    showItemBagSpecialInfo(itembagInfo);
    showResourcePointInfo(resourcePointInfo);
    showHerosInfo(herosInfo);
}

function showPlayerInfo(playerInfo) {
    str = "";
    for (var k in  PlayerInfoKey) {
        var ks = k.split(".");
        var obj = playerInfo[ks[0]];
        if (!isObject(obj)) {
            str += "<tr><td style='text-align:right;' width='150px'>" + PlayerInfoKey[k] + "：</td><td style='text-align:left;'> " + toNotNullAndUndefined(obj) + "</td></tr>"
            continue;
        }
        for (var i = 1; i < ks.length; i++) {
            obj = obj[ks[i]];
            if (!isObject(obj)) {
                str += "<tr><td style='text-align:right;' width='150px'>" + PlayerInfoKey[k] + "：</td><td style='text-align:left;'> " + toNotNullAndUndefined(obj) + "</td></tr>"
                continue;
            }
        }
    }
    $("#playerInfo").html(str);
}

function showResourcePointInfo(resourcePointInfo) {
    str = "";
    for (var k in  ResourcePointInfoKey) {
        var ks = k.split(".");
        var obj = resourcePointInfo[ks[0]];
        if (!isObject(obj)) {
            str += "<tr><td style='text-align:right;' width='150px'>" + ResourcePointInfoKey[k] + "：</td><td style='text-align:left;'> " + toNotNullAndUndefined(obj) + "</td></tr>"
            continue;
        }
        for (var i = 1; i < ks.length; i++) {
            obj = obj[ks[i]];
            if (!isObject(obj)) {
                str += "<tr><td style='text-align:right;' width='150px'>" + ResourcePointInfoKey[k] + "：</td><td style='text-align:left;'> " + toNotNullAndUndefined(obj) + "</td></tr>"
                continue;
            }
        }
    }
    $("#resourcePointInfo").html(str);
}


function showItemBagPlainInfo(itembagInfo) {
    var spli = 0;
    var content = "";
    if (itembagInfo.tpIdToPlainCell != null) {
        for (var k in itembagInfo.tpIdToPlainCell) {
            spli++;
            if (spli % 3 == 1) {
                content += "<tr>";
            }
            var v = itembagInfo.tpIdToPlainCell[k].stackSize;
            content += "<td style='text-align:right;' >" + (k + " - " + getKey(k, ItemBagKey)) + "：</td><td style='text-align:left;' width='150px'> " + toNotNullAndUndefined(v) + "</td>";
            if (spli % 3 == 0) {
                content += "<tr/>";
            }
        }
    }
    if (spli > 0) {
        $("#plainCellSp").css("display", "block");
        $("#plainCell").html(content);
    }
}


function showItemBagSpecialInfo(itembagInfo) {
    var spli = 0;
    var content = "";
    if (itembagInfo.idToSpecialCell != null) {
        for (var k in itembagInfo.idToSpecialCell) {
            spli++;
            if (spli % 3 == 1) {
                content += "<tr>";
            }
            var tpId = itembagInfo.idToSpecialCell[k].tpId;
            var extInfo = itembagInfo.idToSpecialCell[k].extInfo;
            content += "<td style='text-align:right;' >" + (k + " - " + getKey(tpId, ItemBagKey)) + "：</td><td style='text-align:left;' width='150px'> " + toNotNullAndUndefined(extInfo) + "</td>";
            if (spli % 3 == 0) {
                content += "<tr/>";
            }
        }
    }
    if (spli > 0) {
        $("#specialCellSp").css("display", "block");
        $("#specialCell").html(content);
    }
}


function showHerosInfo(herosInfo) {
    var content = "";
    if (herosInfo.idToHero != null) {
        for (var k in herosInfo.idToHero) {
            var objTmp = herosInfo.idToHero[k];
            var tpId = objTmp["tpId"];
            var lv = objTmp["lv"];
            var qualityLv = objTmp["qualityLv"];
            var starLv = objTmp["starLv"];
            var heroName = getKey(tpId, ItemBagKey);
            content += "<tr><td style='text-align:right;' width='300px'>" + (tpId + " - " + heroName) + "：</td>" +
                "<td style='text-align:center;' width='150px'> " + toNotNullAndUndefined(lv) + "</td>" +
                "<td style='text-align:center;' width='150px'> " + toNotNullAndUndefined(qualityLv) + "</td>" +
                "<td style='text-align:center;' width='150px'> " + toNotNullAndUndefined(starLv) + "</td>" +
                "<td style='text-align:center;' > </td>" +
                "<tr/>";
        }
        $("#herosInfo").html(content);
    }
}


function clear() {
    $("#playerInfo").html("");
    $("#resourcePointInfo").html("");
    $("#plainCell").html("");
    $("#specialCell").html("");
    $("#herosInfo").html("");


    $("#plainCellSp").css("display", "none");
    $("#specialCellSp").css("display", "none");
}
