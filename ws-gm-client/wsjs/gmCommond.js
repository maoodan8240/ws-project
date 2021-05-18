// 发送Gm命令
function sendGmCommand() {
    var command = toNotNullAndUndefined($('#selectedItemsTxt').val());
    var simpleIdLis = toNotNullAndUndefined(getSimpleIdLis());
    if (command == "") {
        alert("发送的命令不能为空！");
        return;
    }
    if (simpleIdLis == "") {
        alert("发送的玩家simpleIdLis不能为空！");
        return;
    }
    var msgContent =
        {
            "simpleIdLis": getSimpleIdLis(),
            "command": $("#selectedItemsTxt").val() // 命令
        };
    var msgContentJsonStr = JSON.stringify(msgContent);
    sendRequest(SendGmCommandMsgRequest, msgContentJsonStr, sendGmCommandReturn);
}

function sendGmCommandReturn(dataJson) {
    window.wxc.xcConfirm(dataJson, window.wxc.xcConfirm.typeEnum.info);
}


function getSimpleIdLis() {
    var zx = toNotNullAndUndefined($('#simpleIds').val());
    zx = String(zx);
    zx = zx.replace(new RegExp(/(\n)/g), ' ');
    zx = zx.replace(new RegExp(/(\s+)/g), ' ');
    return zx;
}