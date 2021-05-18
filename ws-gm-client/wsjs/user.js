
var AddUserMsg = "UserRequestMsg$AddUserMsg";
var DeleteUserMsg = "UserRequestMsg$DeleteUserMsg";
var RequestAllUserMsg = "UserRequestMsg$RequestAllUserMsg";
var ModifyUserMsg = "UserRequestMsg$ModifyUserMsg";
var RequestPermissionMsg = "UserRequestMsg$RequestPermissionMsg";


var permissionList = [];
function addUser() {
    var permission_value = [];
    $('input[name="permission"]:checked').each(function () {
        permission_value.push($(this).val());
    });
    var msgContent =
        {
            "account": $("#user").val(),
            "password": $("#passwd").val(),
            "permission": permission_value
        };

    var msgContentJsonStr = JSON.stringify(msgContent);
    console.log("msgContentJsonStr ======:" + msgContentJsonStr);
    sendRequest(AddUserMsg, msgContentJsonStr, addUserReturn);
}

function addUserReturn(dataJsonArr) {
    alert("添加成功")
}


function modifyUser(id) {
    console.log("modify user:" + id);

    var permission_value = [];
    $('input[name="' + id + '_permission"]:checked').each(function () {
        permission_value.push($(this).val());
    });
    var msgContent =
        {
            "id": id,
            "account": $("#" + id + "_account").text(),
            "password": $("#" + id + "_passwd").val(),
            "permission": permission_value
        };

    var msgContentJsonStr = JSON.stringify(msgContent);
    console.log("msgContentJsonStr ======:" + msgContentJsonStr);
    sendRequest(ModifyUserMsg, msgContentJsonStr, modifyUserReturn);
}

function modifyUserReturn() {
    getAllUser();
    alert("修改成功")
}


function permissionInit() {
    getPermission();
    var htmlStr = "";
    for (var i = 0; i < permissionList.length; i++) {
        htmlStr += '<div class="checkbox i-checks"> <label> <input type="checkbox" name="permission" value="' + i + '"> <i></i>' + permissionList[i] + '</label> </div>';
    }
    $("#permissionContent").html(htmlStr);
}

function userList(users) {
    var htmlStr = "";
    for (var i = 0; i < users.length; i++) {
        var user = users[i];
        var id = user["_id"];
        var account = user["account"]
        var permission = user["permission"]
        var permissionStr = "";
        for (var j = 0; j < permissionList.length; j++) {
            console.log($.inArray(i, permission));
            if ($.inArray(j, permission) != -1) {
                permissionStr += '<input type="checkbox" checked="checked" name="' + id + '_permission" value="' + j + '">' + permissionList[j]+"&nbsp;&nbsp;";
            } else {
                permissionStr += '<input type="checkbox" name="' + id + '_permission" value="' + j + '">' + permissionList[j]+"&nbsp;&nbsp;";
            }
        }

        htmlStr += ' <tr> <td id="' + id + '">' + id + '</td><td id="' + id + '_account">' + account + '</td> ' +
            '<td><div class="form-group"> <div class="col-sm-8"> <input type="password" name="passwd" id="' + id + '_passwd"  class="form-control"placeholder="如果填入新密码则修改"> </div> </div> </td>' +
            '<td>' + permissionStr + '</td> <td>' +
            '<button class="btn btn-primary" type="button" onclick="modifyUser(\'' + id + '\')">修改用户</button> <button class="btn btn-primary" type="button" onclick="deleteUser(\'' + id + '\')">删除用户</button>' +
            '</td></tr>';
    }
    $("#userListContent").html(htmlStr);
}


function getAllUser() {
    getPermission();
    var msgContent =
        {};
    var msgContentJsonStr = JSON.stringify(msgContent);
    sendRequest(RequestAllUserMsg, msgContentJsonStr, RequestAllUserReturn);
}

function RequestAllUserReturn(data) {
    userList(data["users"]);
}

function deleteUser(id) {
    var msgContent =
        {
            "id": id
        };
    var msgContentJsonStr = JSON.stringify(msgContent);
    sendRequest(DeleteUserMsg, msgContentJsonStr, deleteUserReturn);

}
function deleteUserReturn(dataJsonArr) {
    getAllUser();
}

function getPermission() {
    var msgContent =
        {};
    var msgContentJsonStr = JSON.stringify(msgContent);
    sendSyncRequest(RequestPermissionMsg, msgContentJsonStr, requestPermissionReturn);
}

function requestPermissionReturn(data) {
    permissionList = data["permissions"];
    console.log(permissionList);
}

