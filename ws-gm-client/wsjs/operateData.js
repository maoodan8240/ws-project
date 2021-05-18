var queryTodayDataRequest = "QueryTodayData$Request";


function queryTodayData() {
    var msgContent =
        {};
    var msgContentJsonStr = JSON.stringify(msgContent);
    sendRequest(queryTodayDataRequest, msgContentJsonStr, queryTodayDataReturn);
}


function queryTodayDataReturn(argsPass) {
    document.getElementById("newAddPlayerCount").innerHTML = argsPass.newCount;                 // 今日新增人数
    document.getElementById("activeDeviceCount").innerHTML = argsPass.activeDeviceCount;        // 今日活跃设备
    document.getElementById("activePlayerCount").innerHTML = argsPass.activeAccount;            // 今日活跃设备
    document.getElementById("newRechargePlayerCount").innerHTML = argsPass.newPlayerNewPayment; // 今日新增玩家中充值的玩家
    document.getElementById("sumRechargePlayerCount").innerHTML = argsPass.todayPaymentCount;   // 今日新增玩家中充值的玩家
    document.getElementById("sumRecharge").innerHTML = argsPass.todayPaymentSum;                // 今日新增玩家中充值的玩家
}


$(document).ready(function () {
    queryTodayData();
})
