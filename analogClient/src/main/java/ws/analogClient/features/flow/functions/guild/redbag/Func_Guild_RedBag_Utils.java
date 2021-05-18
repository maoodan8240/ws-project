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
public class Func_Guild_RedBag_Utils {
    public static void sync() {
        Cm_NewGuildRedBag.Builder b = Cm_NewGuildRedBag.newBuilder();
        b.setAction(Cm_NewGuildRedBag.Action.SYNC);
        Response response = ClientUtils.send(b.build(), Sm_NewGuildRedBag.Action.RESP_SYNC);
        ClientUtils.check(response);
    }

    /**
     * 发高级金币红包
     */
    public static void sendSeniorMoneyRedBag() {
        Func_Guild_Utils.createGuild();
        Func_Gm.setVipLv(9);
        Func_Gm.addResource("1:500000,2:50000");
        Cm_NewGuildRedBag.Builder b = Cm_NewGuildRedBag.newBuilder();
        b.setAction(Cm_NewGuildRedBag.Action.SEND);
        b.setRedBagType(GuildRedBagTypeEnum.PRT_MONEY);
        b.setSendRedBagType(GuildSendRedBagTypeEnum.GSRT_SENIOR);
        Response response = ClientUtils.send(b.build(), Sm_NewGuildRedBag.Action.RESP_SEND);
        ClientUtils.check(response);
    }

    /**
     * 发高级钻石红包
     */
    public static void sendSeniorVipMoneyRedBag() {
        Func_Guild_Utils.createGuild();
        Func_Gm.setVipLv(9);
        Func_Gm.addResource("1:500000,2:50000");
        Cm_NewGuildRedBag.Builder b = Cm_NewGuildRedBag.newBuilder();
        b.setAction(Cm_NewGuildRedBag.Action.SEND);
        b.setRedBagType(GuildRedBagTypeEnum.PRT_V_MONEY);
        b.setSendRedBagType(GuildSendRedBagTypeEnum.GSRT_SENIOR);
        Response response = ClientUtils.send(b.build(), Sm_NewGuildRedBag.Action.RESP_SEND);
        ClientUtils.check(response);
    }

    /**
     * 发至尊金币红包
     */
    public static void sendExtremeMoneyRedBag() {
        Func_Guild_Utils.createGuild();
        Func_Gm.setVipLv(9);
        Func_Gm.addResource("1:500000,2:50000");
        Cm_NewGuildRedBag.Builder b = Cm_NewGuildRedBag.newBuilder();
        b.setAction(Cm_NewGuildRedBag.Action.SEND);
        b.setRedBagType(GuildRedBagTypeEnum.PRT_MONEY);
        b.setSendRedBagType(GuildSendRedBagTypeEnum.GSRT_EXTREME);
        Response response = ClientUtils.send(b.build(), Sm_NewGuildRedBag.Action.RESP_SEND);
        ClientUtils.check(response);
    }

    /**
     * 发至尊钻石红包
     */
    public static void sendExtremeVipMoneyRedBag() {
        Func_Guild_Utils.createGuild();
        Func_Gm.setVipLv(9);
        Func_Gm.addResource("1:500000,2:50000");
        Cm_NewGuildRedBag.Builder b = Cm_NewGuildRedBag.newBuilder();
        b.setAction(Cm_NewGuildRedBag.Action.SEND);
        b.setRedBagType(GuildRedBagTypeEnum.PRT_V_MONEY);
        b.setSendRedBagType(GuildSendRedBagTypeEnum.GSRT_EXTREME);
        Response response = ClientUtils.send(b.build(), Sm_NewGuildRedBag.Action.RESP_SEND);
        ClientUtils.check(response);
    }

    public static void grab(String redBagId, GuildRedBagTypeEnum redBagType) {
        Func_Guild_Utils.createGuild();
        Func_Gm.addResource("1:500000,2:50000");
        Cm_NewGuildRedBag.Builder b = Cm_NewGuildRedBag.newBuilder();
        b.setAction(Cm_NewGuildRedBag.Action.GRAB);
        b.setRedBagType(redBagType);
        b.setRedBagId(redBagId);
        Response response = ClientUtils.send(b.build(), Sm_NewGuildRedBag.Action.RESP_GRAB);
        ClientUtils.check(response);
    }


    public static void getRedBagList() {
        Cm_NewGuildRedBag.Builder b = Cm_NewGuildRedBag.newBuilder();
        b.setAction(Cm_NewGuildRedBag.Action.GET_REDBAG_LIST);
        Response response = ClientUtils.send(b.build(), Sm_NewGuildRedBag.Action.RESP_GET_REDBAG_LIST);
        ClientUtils.check(response);
    }


}
