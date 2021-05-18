package ws.analogClient.features.flow.functions.guild.redbag;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.flow.functions.guild.base.Func_Guild_Utils;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.EnumsProtos.GuildRedBagTypeEnum;
import ws.protos.EnumsProtos.GuildSendRedBagTypeEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.NewGuildRedBagProtos.Cm_NewGuildRedBag;
import ws.protos.NewGuildRedBagProtos.Sm_NewGuildRedBag;

/**
 * Created by lee on 6/5/17.
 */
public class Func_Guild_RedBag {
    public static void execute() {
        // test2();
        Func_Guild_Utils.createGuild();
        Func_Gm.setVipLv(9);
        Func_Gm.addResource("1:500000,2:50000");
        Cm_NewGuildRedBag.Builder b = Cm_NewGuildRedBag.newBuilder();
        b.setAction(Cm_NewGuildRedBag.Action.SEND);
        b.setRedBagType(GuildRedBagTypeEnum.PRT_V_MONEY);
        b.setSendRedBagType(GuildSendRedBagTypeEnum.GSRT_EXTREME);
        Response response1 = ClientUtils.send(b.build(), Sm_NewGuildRedBag.Action.RESP_SEND);
        ClientUtils.check(response1);
    }

    public static void test1() {
        Func_Guild_RedBag_Utils.sync();
    }

    /**
     * 发至尊金币红包
     */
    public static void test2() {
        Func_Guild_RedBag_Utils.sendExtremeMoneyRedBag();
    }

    /**
     * 发至尊钻石红包
     */
    public static void test3() {
        Func_Guild_RedBag_Utils.sendExtremeVipMoneyRedBag();
    }

    /**
     * 发高级金币红包
     */
    public static void test4() {
        Func_Guild_RedBag_Utils.sendSeniorMoneyRedBag();
    }

    /**
     * 发高级钻石红包
     */
    public static void test5() {
        Func_Guild_RedBag_Utils.sendSeniorVipMoneyRedBag();
    }


    public static void test6() {
        Func_Guild_RedBag_Utils.grab("", GuildRedBagTypeEnum.PRT_MONEY);
    }

    public static void test7() {
        Func_Guild_RedBag_Utils.getRedBagList();
    }
}
