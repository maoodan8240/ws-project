function sendMailToPlayers() {
    if ($("#content").val() === "") {
        alert("内容不能为空！");
        return;
    }
    var msgContent =
        {
            "ids": $("#ids").val(),
            "title": $("#title").val(),
            "content": $("#content").val(),
            "expireTime": $("#expireTime").val(),
            // "deleteAfterRead": $('input:radio:checked').val(),
            "deleteAfterRead": $("input[name='deleteAfterRead']:checked").val(),
            "attachments": $("#selectedItemsTxt").val(),
            "senderName": $("#senderName").val()
        };

    var msgContentJsonStr = JSON.stringify(msgContent);
    console.log("msgContentJsonStr ======:" + msgContentJsonStr);
    sendRequest(SendMailToPlayersMsg, msgContentJsonStr, sendMailToPlayersReturn);
}

function sendMailToPlayersReturn(dataJsonArr) {
    alert('邮件发送成功');
}

function sendMailToAll() {
    if ($("#content").val() === "") {
        alert("[内容]不能为空！");
        return;
    }
    if ($("#expireTime").val() === "") {
        alert("[过期日期]不能为空！");
        return;
    }
    var msgContent =
        {
            "title": $("#title").val(),
            "content": $("#content").val(),
            "expireTime": $("#expireTime").val(),
            "deleteAfterRead": $("input[name='deleteAfterRead']:checked").val(),
            "attachments": $("#selectedItemsTxt").val(),
            "senderName": $("#senderName").val(),
            "outerRealmIdsIn": $("#outerRealmIdsIn").val(),
            "outerRealmIdsOut": $("#outerRealmIdsOut").val(),
            "limitPlatforms": $("#limitPlatforms").val(),
            "limitCreateAtMin": $("#limitCreateAtMin").val(),
            "limitCreateAtMax": $("#limitCreateAtMax").val(),
            "limitLevelMin": $("#limitLevelMin").val(),
            "limitLevelMax": $("#limitLevelMax").val(),
            "limitVipLevelMin": $("#limitVipLevelMin").val(),
            "limitVipLevelMax": $("#limitVipLevelMax").val()
        };
    var msgContentJsonStr = JSON.stringify(msgContent);
    console.log("msgContentJsonStr ======:" + msgContentJsonStr);
    sendRequest(SendMailToAllMsg, msgContentJsonStr, sendMailToAllReturn);
}

function sendMailToAllReturn(dataJsonArr) {
    alert('邮件发送成功');
    // console.log(" dataJson返回的消息:" + dataJsonArr);
    // if (dataJsonArr == null) {
    //     return;
    // }
}