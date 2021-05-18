// var url = "http://192.168.0.56:18888";
var url = "http://192.168.0.109:18888";

function sendRequest(msgType, msgContentJsonStr, successFunc) {
    console.log("发送的内容:" + msgContentJsonStr);
    showWaittiongBg();
    $.ajax({
        url: url,
        type: "POST",
        data: {"msgType": msgType, "sessionId": getSessionId(), "data": msgContentJsonStr},
        success: checkResponse,
        error: function (errObj) {
            console.log("发送消息:" + msgType + ", 错误！error:" + errObj);
        }
    });
    function checkResponse(resp) {
        console.log("返回的内容:" + resp);
        closeWaittiongBg();
        var respJson = JSON.parse(resp);
        var errorCode = respJson.errorCode;
        if (errorCode === 0) {
            var jsonarr = respJson.data.split("&&**&&");
            var dataJsonArr = [];
            for (var i = 0; i < jsonarr.length; i++) {
                var one = jsonarr[i];
                if (one.length > 0) {
                    dataJsonArr.push(JSON.parse(one));
                }
            }
            var argsPass = dataJsonArr.length > 1 ? dataJsonArr : dataJsonArr[0];
            successFunc(argsPass);
        } else {
            alert("消息处理失败！errorCode=" + errorCode + ", errMsg=" + respJson.data);
        }
    }
}

function sendSyncRequest(msgType, msgContentJsonStr, successFunc) {
    console.log("发送的内容:" + msgContentJsonStr);
    $.ajax({
        url: url,
        type: "POST",
        async: false,
        data: {"msgType": msgType, "sessionId": getSessionId(), "data": msgContentJsonStr},
        success: checkResponse,
        error: function (errObj) {
            console.log("发送消息:" + msgType + ", 错误！error:" + errObj);
        }
    });
    function checkResponse(resp) {
        console.log("返回的内容:" + resp);
        var respJson = JSON.parse(resp);
        var errorCode = respJson.errorCode;
        if (errorCode == 0) {
            var jsonarr = respJson.data.split("&&**&&");
            var dataJsonArr = new Array();
            for (var i = 0; i < jsonarr.length; i++) {
                var one = jsonarr[i];
                if (one.length > 0) {
                    dataJsonArr.push(JSON.parse(one));
                }
            }
            var argsPass = dataJsonArr.length > 1 ? dataJsonArr : dataJsonArr[0];
            successFunc(argsPass);
        } else {
            alert("消息处理失败！errorCode=" + errorCode + ", errMsg=" + respJson.data);
        }
    }
}

function getSessionId() {
    var sessionId = getCookie("sessionId");
    if (sessionId == null) {
        return "NULL";
    }
    return sessionId;
}

function setCookie(cookieName, cookieValue, minute) {
    cookieValue = escape(cookieValue);//编码latin-1
    var exdate = new Date();
    exdate.setDate(exdate.getDate() + minute);
    document.cookie = cookieName + "=" + cookieValue + "; expires=" + exdate.toGMTString();
}


function getCookie(cookieName) {
    var cookieValue = document.cookie;
    var cookieStartAt = cookieValue.indexOf("" + cookieName + "=");
    if (cookieStartAt == -1) {
        cookieStartAt = cookieValue.indexOf(cookieName + "=");
    }
    if (cookieStartAt == -1) {
        cookieValue = null;
    } else {
        cookieStartAt = cookieValue.indexOf("=", cookieStartAt) + 1;
        cookieEndAt = cookieValue.indexOf(";", cookieStartAt);
        if (cookieEndAt == -1) {
            cookieEndAt = cookieValue.length;
        }
        cookieValue = unescape(cookieValue.substring(cookieStartAt, cookieEndAt));//解码latin-1  
    }
    return cookieValue;
}

function delCookie(cookieName) {
    var cookieValue = getCookie(cookieName);
    if (cookieValue != null) {
        setCookie(cookieName, cookieValue, -1);
    }
}


Date.prototype.format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1,                 //月份
        "d+": this.getDate(),                    //日
        "h+": this.getHours(),                   //小时
        "m+": this.getMinutes(),                 //分
        "s+": this.getSeconds(),                 //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds()             //毫秒
    };
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
    }
    return fmt;
}


/*
 * formatMoney(s,type)
 * 功能：金额按千位逗号分割
 * 参数：s，需要格式化的金额数值.
 * 参数：type,判断格式化后的金额是否需要小数位.
 * 返回：返回格式化后的数值字符串.
 */
function formatMoney(s, type) {
    if (/[^0-9\.]/.test(s))
        return "0";
    if (s == null || s == "")
        return "0";
    s = s.toString().replace(/^(\d*)$/, "$1.");
    s = (s + "00").replace(/(\d*\.\d\d)\d*/, "$1");
    s = s.replace(".", ",");
    var re = /(\d)(\d{3},)/;
    while (re.test(s))
        s = s.replace(re, "$1,$2");
    s = s.replace(/,(\d\d)$/, ".$1");
    if (type == 0) {// 不带小数位(默认是有小数位)
        var a = s.split(".");
        if (a[1] == "00") {
            s = a[0];
        }
    }
    return s;
}


function dateFormat(fdate) {
    if (fdate === "") {
        return "";
    }
    var yyyy = fdate.substring(0, 4);
    var mth = fdate.substring(4, 6);
    var dd = fdate.substring(6, 8);
    var hh = fdate.substring(8, 10);
    var mm = fdate.substring(10, 12);
    var ss = fdate.substring(12, 14);
    var date = new Date(yyyy, mth, dd, hh, mm, ss);
    return date.format("yyyy-MM-dd hh:mm:ss");
}


function isString(str) {
    return (typeof str == 'string') && str.constructor == String;
}


function isNumber(obj) {
    return (typeof obj == 'number') && obj.constructor == Number;
}


function isObject(obj) {
    return (typeof obj == 'object') && obj.constructor == Object;
}

function toNotNullAndUndefined(value) {
    if (value == null || value == undefined) {
        return "";
    }
    return value;
}


function openChooseItemPage() {
    var width = window.screen.availWidth * 0.75;
    var height = window.screen.availHeight * 0.81;
    var top = window.screen.availHeight * 0.1;
    var left = window.screen.availHeight * 0.22;
    window.open('itemChoose.html', '选择[&nbsp;资源&nbsp;/&nbsp;武将&nbsp;/道具&nbsp;]', 'height=' + height + ',width=' + width + ',top=' + top + ',left=' + left + ',toolbar=no,' +
        'menubar=no,scrollbars=no,resizable=no,location=no,status=no');
}

function showWaittiongBg() {
    $(this).jqLoading({height: 70, width: 240, text: "加载中，请耐心等待...."});
    setTimeout(function () {
        $(this).jqLoading("destroy");
    }, 10000);
}

function closeWaittiongBg() {
    $(this).jqLoading("destroy");
}


function checkDate() {
    var d1 = parseInt($("#bgDate").val());
    var d2 = parseInt($("#edDate").val());
    if ($("#bgDate").val() === "" || d1 === 0) {
        alert("时间范围不对，前者不能为空！")
        return false;
    }
    if ($("#edDate").val() === "" || d2 === 0) {
        alert("时间范围不对，后者不能为空！")
        return false;
    }
    if (d1 > d2) {
        alert("时间范围不对，后者须>=前者！")
    }
    return true;
}


// ===============================msgType======================================
var UserRegisterRequestMsg = "UserRegisterRequestMsg";
var UserLoginRequestMsg = "UserLoginRequestMsg";
var QueryPlayerBasicInfoRequestMsg = "QueryPlayerBasicInfoMsg$Request";
var SendMailToPlayersMsg = "MailMsg$SendMailToPlayers";
var SendMailToAllMsg = "MailMsg$SendMailToAll";
var SendGmCommandMsgRequest = "SendGmCommandMsg$Request";
