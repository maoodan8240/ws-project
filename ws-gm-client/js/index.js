$(function () {
    //菜单点击
    $(".J_menuItem").on('click', function () {
        clearAll_J_menuItem_Color();
        var menu = $(this)["0"];
        menu.style.color = "rgb(42, 218, 0)";
        var url = $(this).attr('href');
        $("#J_iframe").attr('src', url);
        return false;
    });
});


function clearAll_J_menuItem_Color() {
    var all = document.getElementsByName("menuItemTab");
    for (var j in  all) {
        var menu = all[j];
        if (typeof menu === "object") {
            menu.style.color = "";
        }
    }
}