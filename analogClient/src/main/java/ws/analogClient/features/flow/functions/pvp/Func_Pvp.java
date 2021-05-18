package ws.analogClient.features.flow.functions.pvp;

import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PvpProtos.Cm_Pvp;
import ws.protos.PvpProtos.Sm_Pvp;
import ws.analogClient.features.utils.ClientUtils;

/**
 * Created by lee on 17-3-6.
 */
public class Func_Pvp {

    public static void execute() {
        test6();
    }


    /**
     * 刷新
     */
    public static void test1() {
        Func_Pvp_Utils.sync();
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.REFRESH);
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_REFRESH);
        ClientUtils.check(response);
    }

    /**
     * 测试更改Pvp宣言
     */
    public static void test2() {
        Func_Pvp_Utils.sync();
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.MODIFY_DECLARATION);
        b.setDeclaration("你特么才是大流氓");
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_MODIFY_DECLARATION);
        ClientUtils.check(response);
    }

    /**
     * 测试更改Pvp头像
     */
    public static void test3() {
        Func_Pvp_Utils.sync();
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.MODIFY_PVP_ICON);
        b.setPvpIcon(3);
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_MODIFY_PVP_ICON);
        ClientUtils.check(response);
    }


    /**
     * 测试更改Pvp宣言,传空的字符串
     * 服务器报错
     */
    public static void test4() {
        Func_Pvp_Utils.sync();
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.MODIFY_DECLARATION);
        b.setDeclaration("");
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_MODIFY_DECLARATION);
        ClientUtils.check(response);
    }

    /**
     * 测试更改Pvp头像,id送负数
     * 服务器报错
     */
    public static void test5() {
        Func_Pvp_Utils.sync();
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.MODIFY_PVP_ICON);
        b.setPvpIcon(-1);
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_MODIFY_PVP_ICON);
        ClientUtils.check(response);
    }


    /**
     * 测试更改Pvp头像，第一次更改头像id为3，第二次还送3
     * 服务器报错，不能更改同样的头像id
     */
    public static void test6() {
        Func_Pvp_Utils.sync();
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.MODIFY_PVP_ICON);
        b.setPvpIcon(3);
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_MODIFY_PVP_ICON);
        ClientUtils.check(response);
        response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_MODIFY_PVP_ICON);
        ClientUtils.check(response);
    }

}
