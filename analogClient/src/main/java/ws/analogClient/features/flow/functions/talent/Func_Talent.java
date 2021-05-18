package ws.analogClient.features.flow.functions.talent;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.TalentProtos.Cm_Talent;
import ws.protos.TalentProtos.Sm_Talent;

/**
 * Created by lee on 17-2-7.
 */
public class Func_Talent {
    public static void execute() {
        test7();
    }


    /**
     * 测试同步
     */
    public static void test1() {
        Func_Gm.setLv(28);
        Cm_Talent.Builder b = Cm_Talent.newBuilder();
        b.setAction(Cm_Talent.Action.SYNC);
        Response response = ClientUtils.send(b.build(), Sm_Talent.Action.RESP_SYNC);
        ClientUtils.check(response);
    }


    /**
     * 带GM命令用来测试正常升级天赋值
     */
    public static void test2() {
        Func_Gm.setLv(28);
        Func_Gm.addResource("6:100");
        Cm_Talent.Builder b = Cm_Talent.newBuilder();
        b.setAction(Cm_Talent.Action.UP_LEVEL);
        b.setTalentLevelId(400101);
        Response response = ClientUtils.send(b.build(), Sm_Talent.Action.RESP_UP_LEVEL);
        ClientUtils.check(response);
    }


    /**
     * 测试等级不足
     */
    public static void test3() {
        Cm_Talent.Builder b = Cm_Talent.newBuilder();
        b.setAction(Cm_Talent.Action.SYNC);
        Response response = ClientUtils.send(b.build(), Sm_Talent.Action.RESP_SYNC);
        ClientUtils.check(response);
    }


    /**
     * 测试缺少前置天赋等级升级失败
     */
    public static void test4() {
        Func_Gm.setLv(28);
        Func_Gm.addResource("6:100");
        Cm_Talent.Builder b = Cm_Talent.newBuilder();
        b.setAction(Cm_Talent.Action.UP_LEVEL);
        b.setTalentLevelId(400102);
        Response response = ClientUtils.send(b.build(), Sm_Talent.Action.RESP_UP_LEVEL);
        ClientUtils.check(response);
    }

    /**
     * 测试缺少资源
     */
    public static void test5() {
        Func_Gm.setLv(28);
        Cm_Talent.Builder b = Cm_Talent.newBuilder();
        b.setAction(Cm_Talent.Action.UP_LEVEL);
        b.setTalentLevelId(400101);
        Response response = ClientUtils.send(b.build(), Sm_Talent.Action.RESP_UP_LEVEL);
        ClientUtils.check(response);
    }

    /**
     * 测试重置天赋
     * 服务器报错，没升级过天赋，不需要重置
     */
    public static void test6() {
        Func_Gm.addResource("2:550");
        Cm_Talent.Builder b = Cm_Talent.newBuilder();
        b.setAction(Cm_Talent.Action.RESET);
        Response response = ClientUtils.send(b.build(), Sm_Talent.Action.RESP_RESET);
        ClientUtils.check(response);
    }


    /**
     * 测试重置天赋
     */
    public static void test7() {
        Func_Gm.setLv(28);
        Func_Gm.addResource("1:9999999");
        Func_Gm.addResource("2:6666");
        Func_Gm.addResource("6:200");
        Cm_Talent.Builder b = Cm_Talent.newBuilder();
        b.setAction(Cm_Talent.Action.UP_LEVEL);
        b.setTalentLevelId(400101);
        Response response = ClientUtils.send(b.build(), Sm_Talent.Action.RESP_UP_LEVEL);
        ClientUtils.check(response);
        b.setAction(Cm_Talent.Action.UP_LEVEL);
        b.setTalentLevelId(400102);
        response = ClientUtils.send(b.build(), Sm_Talent.Action.RESP_UP_LEVEL);
        ClientUtils.check(response);
        b.setAction(Cm_Talent.Action.UP_LEVEL);
        b.setTalentLevelId(400103);
        response = ClientUtils.send(b.build(), Sm_Talent.Action.RESP_UP_LEVEL);
        ClientUtils.check(response);
        b.setAction(Cm_Talent.Action.UP_LEVEL);
        b.setTalentLevelId(400104);
        response = ClientUtils.send(b.build(), Sm_Talent.Action.RESP_UP_LEVEL);
        ClientUtils.check(response);
        Cm_Talent.Builder b1 = Cm_Talent.newBuilder();
        b1.setAction(Cm_Talent.Action.RESET);
        response = ClientUtils.send(b1.build(), Sm_Talent.Action.RESP_RESET);
        ClientUtils.check(response);
    }

    public static void testUplevelById(int talentLevelId) {
        Func_Gm.addResource("6:100");

        Cm_Talent.Builder b = Cm_Talent.newBuilder();
        b.setAction(Cm_Talent.Action.UP_LEVEL);
        b.setTalentLevelId(talentLevelId);
        Response response = ClientUtils.send(b.build(), Sm_Talent.Action.RESP_UP_LEVEL);
        ClientUtils.check(response);
    }
}
