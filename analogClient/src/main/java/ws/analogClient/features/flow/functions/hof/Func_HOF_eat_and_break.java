package ws.analogClient.features.flow.functions.hof;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.HOFProtos.Cm_HOF;
import ws.protos.HOFProtos.Cm_HOF.Action;
import ws.protos.HOFProtos.Cm_HOF_HeroAndFood;
import ws.protos.HOFProtos.Sm_HOF;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.utils.ProtoUtils;

/**
 * Created by lee on 17-2-7.
 */
public class Func_HOF_eat_and_break {
    public static void execute() {
        test_12();
    }


    /**
     * 测试正常突破
     */
    public static void test_10() {
        Func_HOF_Utiles.legalisation();
        Cm_HOF.Builder b = Cm_HOF.newBuilder();
        b.setAction(Action.EAT);
        Cm_HOF_HeroAndFood.Builder smHerAndFood = Cm_HOF_HeroAndFood.newBuilder();
        smHerAndFood.setHeroId(100000001);
        IdAndCount idAndCount_3 = new IdAndCount(4061001, 999);
        IdMaptoCount idMaptoCount = new IdMaptoCount().add(idAndCount_3);
        smHerAndFood.setFoodIdAndCount(ProtoUtils.create_Sm_Common_IdMaptoCount(idMaptoCount));
        b.addHeroAndFood(smHerAndFood);
        Response response = ClientUtils.send(b.build(), Sm_HOF.Action.RESP_EAT);
        ClientUtils.check(response);
        Cm_HOF.Builder b1 = Cm_HOF.newBuilder();
        b1.setAction(Action.BREAK);
        b1.setHeroId(100000001);
        Response response1 = ClientUtils.send(b1.build(), Sm_HOF.Action.RESP_BREAK);
        ClientUtils.check(response1);
    }

    /**
     * 测试经验没满突破的异常
     */
    public static void test_11() {
        Func_HOF_Utiles.legalisation();
        Cm_HOF.Builder b1 = Cm_HOF.newBuilder();
        b1.setAction(Action.BREAK);
        b1.setHeroId(100000001);
        Response response1 = ClientUtils.send(b1.build(), Sm_HOF.Action.RESP_BREAK);
        ClientUtils.check(response1);
    }


    /**
     * 测试已经突破到最高阶段，继续突破的异常
     */
    public static void test_12() {
        Func_HOF_Utiles.legalisation();
        Cm_HOF.Builder b = Cm_HOF.newBuilder();
        b.setAction(Action.EAT_AND_BREAK);
        b.setHeroId(100000001);
        Response response = ClientUtils.send(b.build(), Sm_HOF.Action.RESP_EAT_AND_BREAK);
        ClientUtils.check(response);
        Cm_HOF.Builder b2 = Cm_HOF.newBuilder();
        b2.setAction(Action.BREAK);
        b2.setHeroId(100000001);
        Response response2 = ClientUtils.send(b2.build(), Sm_HOF.Action.RESP_BREAK);
        ClientUtils.check(response2);
    }

    /**
     * 测试一键喂养（喂养突破）直接升到最高好感阶段最高等级
     */
    public static void test_13() {
        Func_HOF_Utiles.legalisation();
        Cm_HOF.Builder b = Cm_HOF.newBuilder();
        b.setAction(Action.EAT_AND_BREAK);
        b.setHeroId(100000001);
        Response response = ClientUtils.send(b.build(), Sm_HOF.Action.RESP_EAT_AND_BREAK);
        ClientUtils.check(response);
    }

    /**
     * 测试一键喂养但经验不满只喂养升级没突破（喂养突破）
     */
    public static void test_14() {
        Func_Gm.setLv(36);
        Func_Gm.addResource("1000001:1,4060001:99,4060501:99,4061001:50");
        Cm_HOF.Builder b = Cm_HOF.newBuilder();
        b.setAction(Action.EAT_AND_BREAK);
        b.setHeroId(100000001);
        Response response = ClientUtils.send(b.build(), Sm_HOF.Action.RESP_EAT_AND_BREAK);
        ClientUtils.check(response);


    }

    /**
     * 测试一键喂养但经验不满只喂养没升级没突破（喂养突破）
     */
    public static void test_15() {
        Func_Gm.setLv(36);
        Func_Gm.addResource("1000001:1,4060001:2");
        Cm_HOF.Builder b = Cm_HOF.newBuilder();
        b.setAction(Action.EAT_AND_BREAK);
        b.setHeroId(100000001);
        Response response = ClientUtils.send(b.build(), Sm_HOF.Action.RESP_EAT_AND_BREAK);
        ClientUtils.check(response);
    }


    /**
     * 测试一键喂养（喂养突破）直接升到最高好感阶段最高等级 再继续一键喂养
     */
    public static void test_16() {
        test_13();
        Func_Gm.addResource("1000001:1,4060001:2");
        Cm_HOF.Builder b = Cm_HOF.newBuilder();
        b.setAction(Action.EAT_AND_BREAK);
        b.setHeroId(100000001);
        Response response = ClientUtils.send(b.build(), Sm_HOF.Action.RESP_EAT_AND_BREAK);
        ClientUtils.check(response);
    }


}
