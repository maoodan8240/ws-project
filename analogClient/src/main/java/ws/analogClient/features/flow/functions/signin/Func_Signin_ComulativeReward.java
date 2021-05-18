package ws.analogClient.features.flow.functions.signin;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.SigninProtos.Cm_Signin;
import ws.protos.SigninProtos.Cm_Signin.Action;
import ws.protos.SigninProtos.Sm_Signin;

public class Func_Signin_ComulativeReward {

    public static void execute() {
        test1();
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
     * 测试点说明： 获取累计签到奖励，需求签到3次
     * 第一次： 正常领取
     * 第二次： 不符合领取的累计奖励的条件! signCount=3 needDays=7
     */
    public static void test1() {
        signin();
        // -------------------------  资源需求 ---------------------------

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 2; i++) {
            Cm_Signin.Builder b1 = Cm_Signin.newBuilder();
            b1.setAction(Action.COMULATIVE_REWARD);
            Response response1 = ClientUtils.send(b1.build(), Sm_Signin.Action.RESP_COMULATIVE_REWARD);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }

}

