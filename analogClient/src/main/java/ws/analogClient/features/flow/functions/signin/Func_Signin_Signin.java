package ws.analogClient.features.flow.functions.signin;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.SigninProtos.Cm_Signin;
import ws.protos.SigninProtos.Cm_Signin.Action;
import ws.protos.SigninProtos.Sm_Signin;

public class Func_Signin_Signin {

    public static void execute() {
        test2();
    }


    /**
     * 测试点说明： 同步
     */
    public static void signin() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------

        // -------------------------  功能测试 ---------------------------
        Cm_Signin.Builder b1 = Cm_Signin.newBuilder();
        b1.setAction(Cm_Signin.Action.SYNC);
        Response response1 = ClientUtils.send(b1.build(), Sm_Signin.Action.RESP_SYNC);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * 第一次： 成功签到
     * 第二次： 本日已签到
     */
    public static void test2() {
        signin();
        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 2; i++) {
            Cm_Signin.Builder b1 = Cm_Signin.newBuilder();
            b1.setAction(Action.SIGNIN);
            Response response1 = ClientUtils.send(b1.build(), Sm_Signin.Action.RESP_SIGNIN);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }
}

