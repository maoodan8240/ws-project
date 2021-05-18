package ws.analogClient.features.flow.functions.friends;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.CommonProtos.Sm_Common_Round;
import ws.protos.FriendProtos.Cm_Friend;
import ws.protos.FriendProtos.Cm_Friend.Action;
import ws.protos.FriendProtos.Sm_Friend;
import ws.protos.MessageHandlerProtos.Response;

public class Func_Friends_GetFriend {

    public static void execute() {
        test1();
    }

    /**
     * 测试点说明：
     */
    public static void test1() {
        Func_Friends_Sync.test1();
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------

        // -------------------------  功能测试 ---------------------------
        Cm_Friend.Builder b1 = Cm_Friend.newBuilder();
        b1.setAction(Action.GET_FRIEND);
        Sm_Common_Round.Builder b = Sm_Common_Round.newBuilder();
        b.setMin(0);
        b.setMax(15);
        b1.setRound(b);
        Response response1 = ClientUtils.send(b1.build(), Sm_Friend.Action.RESP_GET_FRIEND);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }
}

