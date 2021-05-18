package ws.analogClient.features.flow.functions.friends;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.FriendProtos.Cm_Friend;
import ws.protos.FriendProtos.Cm_Friend.Action;
import ws.protos.FriendProtos.Sm_Friend;
import ws.protos.MessageHandlerProtos.Response;

public class Func_Friends_Give {

    public static void execute() {
        test1();
    }

    /**
     * 测试点说明：
     */
    public static void test1() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------

        // -------------------------  功能测试 ---------------------------
        Cm_Friend.Builder b1 = Cm_Friend.newBuilder();
        b1.setAction(Action.GIVE);
        b1.setPlayerId("");
        Response response1 = ClientUtils.send(b1.build(), Sm_Friend.Action.RESP_GIVE);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }
}

