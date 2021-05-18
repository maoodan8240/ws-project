package ws.analogClient.features.flow.functions.heros;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.EnumsProtos.SkillPositionEnum;
import ws.protos.HerosProtos.Cm_Heros;
import ws.protos.HerosProtos.Cm_Heros.Action;
import ws.protos.HerosProtos.Sm_Heros;
import ws.protos.HerosProtos.Sm_Heros_SkillUpgradeInfo;
import ws.protos.MessageHandlerProtos.Response;

public class Func_Heros_UpgradeSkillLevel {

    public static void execute() {
        test2();
    }

    /**
     * 测试点说明： 【武将等级】正常升级
     */
    public static void test_Up_Hero(int level) {
        Func_Gm.setLv(level);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1000001:1";
        String resource_2 = "4080001:30000,4080002:30000,4080003:30000,4080004:30000";

        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_LV);
        b1.setHeroId(100000001);
        b1.setConsumeTpIds(Func_Gm.create_Sm_Common_IdMaptoCount("4080001:30000,4080002:30000,4080003:30000,4080004:30000"));
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：
     * 1)  正常升级技能
     */
    public static void test_Up_HeroSkilll(int lv) {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_SKILL_LV);
        b1.setHeroId(100000001);
        b1.addSkillUpgradeInfos(create_Sm_Heros_SkillUpgradeInfo4(lv));
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_SKILL_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：
     * 正常升级技能
     */
    public static void test1() {
        test_Up_Hero(20);
        test_Up_HeroSkilll(11);
    }


    /**
     * 测试点说明：
     * 无法获得 skillTypeEnum=SIMPLE 解锁需要的星级！ 配置为：TupleList[Tuple[1:1],Tuple[2:2],Tuple[3:3],Tuple[4:4],Tuple[5:5],Tuple[6:6],Tuple[7:2]]
     */
    public static void test2() {
        test_Up_Hero(20);

        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_SKILL_LV);
        b1.setHeroId(100000001);
        b1.addSkillUpgradeInfos(create_Sm_Heros_SkillUpgradeInfo2());
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_SKILL_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * 升级次数times=0，必须大于0！ skillUpgradeInfos=[skillType: SMALL
     */
    public static void test3() {
        test_Up_Hero(20);

        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_SKILL_LV);
        b1.setHeroId(100000001);
        b1.addSkillUpgradeInfos(create_Sm_Heros_SkillUpgradeInfo4(0));
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_SKILL_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * heroId=1000001，skillType=SMALL, 已经达到英雄的等级=11，不能再升级技能！
     */
    public static void test4() {
        test_Up_Hero(11);
        test_Up_HeroSkilll(10);

        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_SKILL_LV);
        b1.setHeroId(100000001);
        b1.addSkillUpgradeInfos(create_Sm_Heros_SkillUpgradeInfo4(1));
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_SKILL_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：
     * heroId=1000001，skillType=SMALL, heroLv=11, oldSkillLv=6, times=7 ,(oldSkillLv + times 不能大于heroLv)！
     */
    public static void test5() {
        test_Up_Hero(11);
        test_Up_HeroSkilll(5);

        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_SKILL_LV);
        b1.setHeroId(100000001);
        b1.addSkillUpgradeInfos(create_Sm_Heros_SkillUpgradeInfo4(7));
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_SKILL_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * heroId=1000001，skillType=ULTIMATE, 需求星级=2，当前星级=1，不能升级技能！
     */
    public static void test6() {
        test_Up_Hero(11);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_SKILL_LV);
        b1.setHeroId(100000001);
        b1.addSkillUpgradeInfos(create_Sm_Heros_SkillUpgradeInfo5(SkillPositionEnum.Skill_POS_2, 7));
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_SKILL_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * 升级技能所需要的技能点数=21，当前总的技能点为=20！
     */
    public static void test7() {
        test_Up_Hero(22);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_SKILL_LV);
        b1.setHeroId(100000001);
        b1.addSkillUpgradeInfos(create_Sm_Heros_SkillUpgradeInfo5(SkillPositionEnum.Skill_POS_1, 21));
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_SKILL_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：
     * 1) 正常升级技能
     * 2) 技能点正常恢复
     * 3) 第三此升级  Item=[1:280200]，资源不足！
     */
    public static void test8() {
        test_Up_Hero(70);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:100000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 3; i++) {
            Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
            b1.setAction(Action.UP_SKILL_LV);
            b1.setHeroId(100000001);
            b1.addSkillUpgradeInfos(create_Sm_Heros_SkillUpgradeInfo5(SkillPositionEnum.Skill_POS_1, 20));
            Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_SKILL_LV);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
            try {
                Thread.sleep(1000l * 60 * 21);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("====================================================");
        }
    }


    private static Sm_Heros_SkillUpgradeInfo create_Sm_Heros_SkillUpgradeInfo2() {
        Sm_Heros_SkillUpgradeInfo.Builder b = Sm_Heros_SkillUpgradeInfo.newBuilder();
        b.setSkillPos(null);
        b.setTimes(10);
        return b.build();
    }


    private static Sm_Heros_SkillUpgradeInfo create_Sm_Heros_SkillUpgradeInfo4(int times) {
        Sm_Heros_SkillUpgradeInfo.Builder b = Sm_Heros_SkillUpgradeInfo.newBuilder();
        b.setSkillPos(SkillPositionEnum.Skill_POS_1);
        b.setTimes(times);
        return b.build();
    }

    private static Sm_Heros_SkillUpgradeInfo create_Sm_Heros_SkillUpgradeInfo5(SkillPositionEnum skillPosition, int times) {
        Sm_Heros_SkillUpgradeInfo.Builder b = Sm_Heros_SkillUpgradeInfo.newBuilder();
        b.setSkillPos(skillPosition);
        b.setTimes(times);
        return b.build();
    }
}

