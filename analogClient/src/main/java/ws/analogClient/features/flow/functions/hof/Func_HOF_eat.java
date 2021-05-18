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
public class Func_HOF_eat {
    public static void execute() {
        test_7();
    }


    /**
     * 测试英雄资源不足
     */
    public static void test_3() {
        Func_Gm.setLv(36);
        Cm_HOF.Builder b = Cm_HOF.newBuilder();
        b.setAction(Action.EAT);
        Cm_HOF_HeroAndFood.Builder smHerAndFood = Cm_HOF_HeroAndFood.newBuilder();
        smHerAndFood.setHeroId(100000001);
        IdAndCount idAndCount_1 = new IdAndCount(4060001, 1);
        IdAndCount idAndCount_2 = new IdAndCount(4060501, 1);
        IdAndCount idAndCount_3 = new IdAndCount(4061001, 1);
        IdMaptoCount idMaptoCount = new IdMaptoCount().add(idAndCount_1).add(idAndCount_2).add(idAndCount_3);
        smHerAndFood.setFoodIdAndCount(ProtoUtils.create_Sm_Common_IdMaptoCount(idMaptoCount));
        b.addHeroAndFood(smHerAndFood);
        Response response = ClientUtils.send(b.build(), Sm_HOF.Action.RESP_EAT);
        ClientUtils.check(response);
    }


    /**
     * 测试食品资源不足
     */
    public static void test_4() {
        Func_Gm.setLv(36);
        Func_Gm.addResource("1000001:1");
        Cm_HOF.Builder b = Cm_HOF.newBuilder();
        b.setAction(Action.EAT);
        Cm_HOF_HeroAndFood.Builder smHerAndFood = Cm_HOF_HeroAndFood.newBuilder();
        smHerAndFood.setHeroId(100000001);
        IdAndCount idAndCount_1 = new IdAndCount(4060001, 1);
        IdAndCount idAndCount_2 = new IdAndCount(4060501, 1);
        IdAndCount idAndCount_3 = new IdAndCount(4061001, 1);
        IdMaptoCount idMaptoCount = new IdMaptoCount().add(idAndCount_1).add(idAndCount_2).add(idAndCount_3);
        smHerAndFood.setFoodIdAndCount(ProtoUtils.create_Sm_Common_IdMaptoCount(idMaptoCount));
        b.addHeroAndFood(smHerAndFood);
        Response response = ClientUtils.send(b.build(), Sm_HOF.Action.RESP_EAT);
        ClientUtils.check(response);
    }

    /**
     * 测试正常喂养
     */
    public static void test_5() {
        Func_HOF_Utiles.legalisation();
        Cm_HOF.Builder b = Cm_HOF.newBuilder();
        b.setAction(Action.EAT);
        Cm_HOF_HeroAndFood.Builder smHerAndFood = Cm_HOF_HeroAndFood.newBuilder();
        smHerAndFood.setHeroId(100000001);
        IdAndCount idAndCount_1 = new IdAndCount(4060001, 1);
        IdAndCount idAndCount_2 = new IdAndCount(4060501, 1);
        IdAndCount idAndCount_3 = new IdAndCount(4061001, 1);
        IdMaptoCount idMaptoCount = new IdMaptoCount().add(idAndCount_1).add(idAndCount_2).add(idAndCount_3);
        smHerAndFood.setFoodIdAndCount(ProtoUtils.create_Sm_Common_IdMaptoCount(idMaptoCount));
        b.addHeroAndFood(smHerAndFood);
        Response response = ClientUtils.send(b.build(), Sm_HOF.Action.RESP_EAT);
        ClientUtils.check(response);
    }

    /**
     * 测试经验未满是突破失败的异常
     */
    public static void test_6() {
        Func_HOF_Utiles.legalisation();
        Cm_HOF.Builder b = Cm_HOF.newBuilder();
        b.setAction(Action.EAT);
        Cm_HOF_HeroAndFood.Builder smHerAndFood = Cm_HOF_HeroAndFood.newBuilder();
        smHerAndFood.setHeroId(100000001);
        IdAndCount idAndCount_1 = new IdAndCount(4060001, 1);
        IdAndCount idAndCount_2 = new IdAndCount(4060501, 1);
        IdAndCount idAndCount_3 = new IdAndCount(4061001, 1);
        IdMaptoCount idMaptoCount = new IdMaptoCount().add(idAndCount_1).add(idAndCount_2).add(idAndCount_3);
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
     * 测试喂养到经验满然后突破(吃超过本阶段最高级的经验，剩余经验不累计，模拟客户端不校验喂养食品经验总和)
     */
    public static void test_7() {
        Func_HOF_Utiles.legalisation();
        Cm_HOF.Builder b = Cm_HOF.newBuilder();
        b.setAction(Action.EAT);
        Cm_HOF_HeroAndFood.Builder smHerAndFood = Cm_HOF_HeroAndFood.newBuilder();
        smHerAndFood.setHeroId(100000001);
        IdAndCount idAndCount_1 = new IdAndCount(4060001, 99);
        IdAndCount idAndCount_2 = new IdAndCount(4060501, 99);
        IdAndCount idAndCount_3 = new IdAndCount(4061001, 99);
        IdMaptoCount idMaptoCount = new IdMaptoCount().add(idAndCount_1).add(idAndCount_2).add(idAndCount_3);
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
     * 测试喂养不匹配的食品
     */
    public static void test_8() {
        Func_HOF_Utiles.legalisation_2();
        Cm_HOF.Builder b = Cm_HOF.newBuilder();
        b.setAction(Action.EAT);
        Cm_HOF_HeroAndFood.Builder smHerAndFood = Cm_HOF_HeroAndFood.newBuilder();
        smHerAndFood.setHeroId(100000001);
        IdAndCount idAndCount_1 = new IdAndCount(4060002, 99);
        IdAndCount idAndCount_2 = new IdAndCount(4060502, 99);
        IdAndCount idAndCount_3 = new IdAndCount(4061002, 99);
        IdMaptoCount idMaptoCount = new IdMaptoCount().add(idAndCount_1).add(idAndCount_2).add(idAndCount_3);
        smHerAndFood.setFoodIdAndCount(ProtoUtils.create_Sm_Common_IdMaptoCount(idMaptoCount));
        b.addHeroAndFood(smHerAndFood);
        Response response = ClientUtils.send(b.build(), Sm_HOF.Action.RESP_EAT);
        ClientUtils.check(response);
    }


    /**
     * 测试喂养到经验满然继续喂养的异常
     */
    public static void test_9() {
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
        // legalisation();
        Cm_HOF.Builder b1 = Cm_HOF.newBuilder();
        b1.setAction(Action.EAT);
        Cm_HOF_HeroAndFood.Builder smHerAndFood1 = Cm_HOF_HeroAndFood.newBuilder();
        smHerAndFood1.setHeroId(100000001);
        IdAndCount idAndCount_1 = new IdAndCount(4060001, 99);
        IdMaptoCount idMaptoCount2 = new IdMaptoCount().add(idAndCount_1);
        smHerAndFood1.setFoodIdAndCount(ProtoUtils.create_Sm_Common_IdMaptoCount(idMaptoCount2));
        b1.addHeroAndFood(smHerAndFood1);
        Response response1 = ClientUtils.send(b1.build(), Sm_HOF.Action.RESP_EAT);
        ClientUtils.check(response1);
    }


}
