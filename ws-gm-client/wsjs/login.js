// -------------------------------------------------------------------------------------------------------------------------------------
var sessionId;

// 注册
function register() {
    var msgContent =
        {
            "account": $("#userName").val(), // 注册帐号
            "password": $("#password").val() // 注册密码
        };
    var msgContentJsonStr = JSON.stringify(msgContent);
    sendRequest(UserRegisterRequestMsg, msgContentJsonStr, registerReturn);
}

function registerReturn(dataJson) {
    sessionId = dataJson.sessionId;
    alert("注册成功！sessionId=" + sessionId);
}

// -------------------------------------------------------------------------------------------------------------------------------------

// 登录
function login() {
    var msgContent =
        {
            "account": $("#userName").val(), // 注册帐号
            "password": $("#password").val() // 注册密码
        };
    var msgContentJsonStr = JSON.stringify(msgContent);
    sendRequest(UserLoginRequestMsg, msgContentJsonStr, loginReturn);
}

function loginReturn(dataJson) {
    sessionId = dataJson.sessionId;
    delCookie("sessionId");
    setCookie("sessionId", sessionId, 1000);
    sessionId = getCookie("sessionId");
    window.location.href = "index.html";
    console.log("loginReturn 返回的消息:" + dataJson);
}

// -------------------------------------------------------------------------------------------------------------------------------------
