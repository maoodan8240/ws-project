package ws.analogClient.features.flow.functions.friends;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.FriendProtos.Cm_Friend;
import ws.protos.FriendProtos.Cm_Friend.Action;
import ws.protos.FriendProtos.Sm_Friend;
import ws.protos.MessageHandlerProtos.Response;

import java.util.ArrayList;
import java.util.List;

public class Func_Friends_Delete {

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
        b1.setAction(Action.DELETE);
        List<String> playerIdLis = new ArrayList<>();
        playerIdLis.add("5919384d784ba361a5e7a449");
        b1.addAllPlayerIdLis(playerIdLis);
        Response response1 = ClientUtils.send(b1.build(), Sm_Friend.Action.RESP_DELETE);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }
}

