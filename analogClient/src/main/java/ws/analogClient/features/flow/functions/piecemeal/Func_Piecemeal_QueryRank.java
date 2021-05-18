package ws.analogClient.features.flow.functions.piecemeal;

import ws.analogClient.features.utils.ClientUtils;
import ws.protos.CommonRankProtos.Cm_CommonRank;
import ws.protos.CommonRankProtos.Cm_CommonRank.Action;
import ws.protos.CommonRankProtos.Sm_CommonRank;
import ws.protos.EnumsProtos.CommonRankTypeEnum;
import ws.protos.MessageHandlerProtos.Response;

public class Func_Piecemeal_QueryRank {

    public static void execute() {
        test1();
        test2();
    }


    /**
     * 测试点说明： 正常查询排行
     */
    public static void test1() {
        // -------------------------  资源需求 ---------------------------

        // -------------------------  功能测试 ---------------------------
        Cm_CommonRank.Builder b1 = Cm_CommonRank.newBuilder();
        b1.setAction(Action.QUERY_RANK);
        b1.setRankTypeEnum(CommonRankTypeEnum.RK_HERO_COUNT);
        b1.setRankStart(1);
        b1.setRankCount(20);
        Response response1 = ClientUtils.send(b1.build(), Sm_CommonRank.Action.RESP_QUERY_RANK);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明： 正常查询排行
     */
    public static void test2() {
        // -------------------------  资源需求 ---------------------------

        // -------------------------  功能测试 ---------------------------
        Cm_CommonRank.Builder b1 = Cm_CommonRank.newBuilder();
        b1.setAction(Action.QUERY_RANK);
        b1.setRankTypeEnum(CommonRankTypeEnum.RK_PVE_STAR);
        b1.setRankStart(1);
        b1.setRankCount(20);
        Response response1 = ClientUtils.send(b1.build(), Sm_CommonRank.Action.RESP_QUERY_RANK);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }

    }
}
