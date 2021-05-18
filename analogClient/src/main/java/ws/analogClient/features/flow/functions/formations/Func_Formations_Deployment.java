package ws.analogClient.features.flow.functions.formations;

import ws.protos.EnumsProtos.FormationTypeEnum;
import ws.protos.EnumsProtos.HeroPositionEnum;
import ws.protos.FormationsProtos.Cm_Formations;
import ws.protos.FormationsProtos.Cm_Formations.Action;
import ws.protos.FormationsProtos.Sm_Formations;
import ws.protos.FormationsProtos.Sm_Formations_OneFormation;
import ws.protos.FormationsProtos.Sm_Formations_PosToHeroId;
import ws.protos.MessageHandlerProtos.Response;
import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;

public class Func_Formations_Deployment {

    public static void execute() {
        test6();
    }


    /**
     * 测试点说明： 正常步阵
     */
    public static void test1() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1000001:1";
        String resource_2 = "1000002:1,1000003:1,1000004:1,1000005:1,1000006:1,1000007:1";

        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        Cm_Formations.Builder b1 = Cm_Formations.newBuilder();
        b1.setAction(Action.DEPLOYMENT);
        b1.setFormation(create_Sm_Formations_OneFormation());
        Response response1 = ClientUtils.send(b1.build(), Sm_Formations.Action.RESP_DEPLOYMENT);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：
     * 异常点： 空步阵
     */
    public static void test2() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1000001:1";
        String resource_2 = "1000002:1,1000003:1,1000004:1,1000005:1,1000006:1,1000007:1";

        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        Cm_Formations.Builder b1 = Cm_Formations.newBuilder();
        b1.setAction(Action.DEPLOYMENT);
        Response response1 = ClientUtils.send(b1.build(), Sm_Formations.Action.RESP_DEPLOYMENT);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * 异常点： 阵容类型为=F_MAIN 阵容中包含相同的Id=100000001！
     */
    public static void test3() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1000001:1";
        String resource_2 = "1000002:1,1000003:1,1000004:1,1000005:1,1000006:1,1000007:1";

        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        Cm_Formations.Builder b1 = Cm_Formations.newBuilder();
        b1.setAction(Action.DEPLOYMENT);
        b1.setFormation(create_Sm_Formations_OneFormation1());
        Response response1 = ClientUtils.send(b1.build(), Sm_Formations.Action.RESP_DEPLOYMENT);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * 异常点： 阵容类型为=F_MAIN 武将Id必须>0！
     */
    public static void test4() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1000001:1";
        String resource_2 = "1000002:1,1000003:1,1000004:1,1000005:1,1000006:1,1000007:1";

        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        Cm_Formations.Builder b1 = Cm_Formations.newBuilder();
        b1.setAction(Action.DEPLOYMENT);
        b1.setFormation(create_Sm_Formations_OneFormation2());
        Response response1 = ClientUtils.send(b1.build(), Sm_Formations.Action.RESP_DEPLOYMENT);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * 异常点： 阵容类型为=F_MAIN 阵容中包含相同的Pos=HERO_POSITION_ONE！
     */
    public static void test5() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1000001:1";
        String resource_2 = "1000002:1,1000003:1,1000004:1,1000005:1,1000006:1,1000007:1";

        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        Cm_Formations.Builder b1 = Cm_Formations.newBuilder();
        b1.setAction(Action.DEPLOYMENT);
        b1.setFormation(create_Sm_Formations_OneFormation3());
        Response response1 = ClientUtils.send(b1.build(), Sm_Formations.Action.RESP_DEPLOYMENT);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：部分上阵
     */
    public static void test6() {
        // -------------------------  资源需求 ---------------------------
        test1();
        // -------------------------  功能测试 ---------------------------
        Cm_Formations.Builder b1 = Cm_Formations.newBuilder();
        b1.setAction(Action.DEPLOYMENT);
        b1.setFormation(create_Sm_Formations_OneFormation4());
        Response response1 = ClientUtils.send(b1.build(), Sm_Formations.Action.RESP_DEPLOYMENT);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    private static Sm_Formations_OneFormation create_Sm_Formations_OneFormation() {
        Sm_Formations_OneFormation.Builder b = Sm_Formations_OneFormation.newBuilder();
        b.setType(FormationTypeEnum.F_MAIN);
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_ONE, 100000001));
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_TWO, 100000002));
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_THREE, 100000003));
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_FOUR, 100000004));
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_FIVE, 100000005));
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_SIX, 100000006));
        return b.build();
    }

    private static Sm_Formations_OneFormation create_Sm_Formations_OneFormation1() {
        Sm_Formations_OneFormation.Builder b = Sm_Formations_OneFormation.newBuilder();
        b.setType(FormationTypeEnum.F_MAIN);
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_ONE, 100000001));
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_TWO, 100000002));
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_THREE, 100000003));
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_FOUR, 100000004));
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_FIVE, 100000005));
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_SIX, 100000001));
        return b.build();
    }

    private static Sm_Formations_OneFormation create_Sm_Formations_OneFormation2() {
        Sm_Formations_OneFormation.Builder b = Sm_Formations_OneFormation.newBuilder();
        b.setType(FormationTypeEnum.F_MAIN);
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_ONE, 100000001));
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_TWO, 100000002));
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_THREE, -1));
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_FOUR, -1));
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_FIVE, -1));
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_SIX, -1));
        return b.build();
    }


    private static Sm_Formations_OneFormation create_Sm_Formations_OneFormation3() {
        Sm_Formations_OneFormation.Builder b = Sm_Formations_OneFormation.newBuilder();
        b.setType(FormationTypeEnum.F_MAIN);
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_ONE, 100000001));
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_TWO, 100000002));
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_ONE, 100000003));
        return b.build();
    }


    private static Sm_Formations_OneFormation create_Sm_Formations_OneFormation4() {
        Sm_Formations_OneFormation.Builder b = Sm_Formations_OneFormation.newBuilder();
        b.setType(FormationTypeEnum.F_MAIN);
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_TWO, 100000004));
        b.addPosLis(create_Sm_Formations_PosToHeroId(HeroPositionEnum.HERO_POSITION_FOUR, 100000003));
        return b.build();
    }


    private static Sm_Formations_PosToHeroId create_Sm_Formations_PosToHeroId(HeroPositionEnum pos, int heroId) {
        Sm_Formations_PosToHeroId.Builder b = Sm_Formations_PosToHeroId.newBuilder();
        b.setHeroId(heroId);
        b.setPos(pos);
        return b.build();
    }

}

