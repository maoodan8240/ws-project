package ws.analogClient.features.flow.functions.signin;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.SigninProtos.Cm_Signin;
import ws.protos.SigninProtos.Sm_Signin;

public class Func_Signin_Sync {

    public static void execute() {
        test1();
    }

    /**
     * 测试点说明： 同步
     */
    public static void test1() {
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

}

