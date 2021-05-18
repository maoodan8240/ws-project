package ws.analogClient.features.flow.functions.missions;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.EnumsProtos.MissionTypeEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.MissionsProtos.Cm_Missions;
import ws.protos.MissionsProtos.Sm_Missions;
import ws.analogClient.features.flow.functions.Pve.Func_Pve_Utils;

public class Func_Missions_GetRewards {

    public static void execute() {
        Func_Missions_Sync.test1();
        test8();
    }


    public static void pveAttack(int times) {
        for (int i = 0; i < times; i++) {
            Func_Pve_Utils.fightPve(true, 3);
        }
    }

    /**
     * 测试点说明： 正常领取奖励
     */
    public static void test1() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:100";
        String resource_2 = "3:10000";

        Func_Gm.addResource(resource_1, resource_2);

        pveAttack(10);
        // -------------------------  功能测试 ---------------------------
        Cm_Missions.Builder b1 = Cm_Missions.newBuilder();
        b1.setAction(Cm_Missions.Action.GET_REWARDS);
        b1.setMissionType(MissionTypeEnum.MISS_DALIY);
        b1.setMid(100);
        Response response1 = ClientUtils.send(b1.build(), Sm_Missions.Action.RESP_GET_REWARDS);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
        Func_Missions_Sync.test1();
    }

    /**
     * 测试点说明：
     * 任务已经完成，已经领取了奖励！ mid=100 !
     */
    public static void test2() {
        // -------------------------  资源需求 ---------------------------
        test1();
        test1();
    }

    /**
     * 测试点说明：
     * 尚未完成任务，不可领取奖励 mid=100 ! hasDone=5 need=10
     */
    public static void test3() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:100";
        String resource_2 = "3:10000";

        Func_Gm.addResource(resource_1, resource_2);

        pveAttack(5);
        // -------------------------  功能测试 ---------------------------
        Cm_Missions.Builder b1 = Cm_Missions.newBuilder();
        b1.setAction(Cm_Missions.Action.GET_REWARDS);
        b1.setMissionType(MissionTypeEnum.MISS_DALIY);
        b1.setMid(100);
        Response response1 = ClientUtils.send(b1.build(), Sm_Missions.Action.RESP_GET_REWARDS);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
        Func_Missions_Sync.test1();
    }

    /**
     * 测试点说明：
     * 任务表 Table_Missions_Row 中不存在此mid=1000 !
     */
    public static void test4() {
        // -------------------------  资源需求 ---------------------------
        // -------------------------  功能测试 ---------------------------
        Cm_Missions.Builder b1 = Cm_Missions.newBuilder();
        b1.setAction(Cm_Missions.Action.GET_REWARDS);
        b1.setMissionType(MissionTypeEnum.MISS_DALIY);
        b1.setMid(1000);
        Response response1 = ClientUtils.send(b1.build(), Sm_Missions.Action.RESP_GET_REWARDS);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
        Func_Missions_Sync.test1();
    }

    /**
     * 测试点说明：
     * missionType=MISS_DALIY有误，表中配置的为=MISS_COMMON mid=205 !
     */
    public static void test5() {
        // -------------------------  资源需求 ---------------------------
        // -------------------------  功能测试 ---------------------------
        Cm_Missions.Builder b1 = Cm_Missions.newBuilder();
        b1.setAction(Cm_Missions.Action.GET_REWARDS);
        b1.setMissionType(MissionTypeEnum.MISS_DALIY);
        b1.setMid(205);
        Response response1 = ClientUtils.send(b1.build(), Sm_Missions.Action.RESP_GET_REWARDS);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
        Func_Missions_Sync.test1();
    }


    /**
     * 测试点说明：
     * 尚未领取该任务 mid=205 !
     */
    public static void test6() {
        // -------------------------  资源需求 ---------------------------
        // -------------------------  功能测试 ---------------------------
        Cm_Missions.Builder b1 = Cm_Missions.newBuilder();
        b1.setAction(Cm_Missions.Action.GET_REWARDS);
        b1.setMissionType(MissionTypeEnum.MISS_COMMON);
        b1.setMid(205);
        Response response1 = ClientUtils.send(b1.build(), Sm_Missions.Action.RESP_GET_REWARDS);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
        Func_Missions_Sync.test1();
    }


    /**
     * 需要设置当前时间对应的体力任务id
     * 测试点说明：成功领取体力
     */
    public static void test7() {
        // -------------------------  资源需求 ---------------------------
        // -------------------------  功能测试 ---------------------------
        Cm_Missions.Builder b1 = Cm_Missions.newBuilder();
        b1.setAction(Cm_Missions.Action.GET_REWARDS);
        b1.setMissionType(MissionTypeEnum.MISS_DALIY);
        b1.setMid(105); // 12 点的体力
        Response response1 = ClientUtils.send(b1.build(), Sm_Missions.Action.RESP_GET_REWARDS);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
        Func_Missions_Sync.test1();
    }


    /**
     * 测试点说明：
     * 由于体力任务过期，领取前自动移除了，所以失败
     * 尚未领取该任务 mid=105 !
     */
    public static void test8() {
        Func_Missions_Sync.test1();
        try {
            Thread.sleep(60 * 1000l); // 睡眠一分钟取修改时间，时得当前的体力任务过期
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // -------------------------  资源需求 ---------------------------
        // -------------------------  功能测试 ---------------------------
        Cm_Missions.Builder b1 = Cm_Missions.newBuilder();
        b1.setAction(Cm_Missions.Action.GET_REWARDS);
        b1.setMissionType(MissionTypeEnum.MISS_DALIY);
        b1.setMid(105); // 12 点的体力
        Response response1 = ClientUtils.send(b1.build(), Sm_Missions.Action.RESP_GET_REWARDS);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
        Func_Missions_Sync.test1();
    }

}

