package ws.analogClient.features.flow.functions.gm;

import ws.analogClient.features.utils.ClientUtils;
import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.common.table.table.utils.CellParser;
import ws.protos.CommonProtos.Sm_Common_IdMaptoCount;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PlayerProtos.Cm_GmCommand;
import ws.protos.PlayerProtos.Sm_GmCommand;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.gm.GmCommandGroupNameConstants;
import ws.relationship.gm.GmCommandGroupNameConstants.PveGmSupport;
import ws.relationship.gm.GmCommandGroupNameConstants.ShopsGmSupportEnum;
import ws.relationship.utils.ProtoUtils;

/**
 * Created by leetony on 16-10-26.
 */
public class Func_Gm {

    public static void execute() {
        setLv(25);
        openAllChapter();
    }


    public static Sm_Common_IdMaptoCount create_Sm_Common_IdMaptoCount(String resource) {
        try {
            TupleListCell<Integer> rs = CellParser.parseTupleListCell("", resource, Integer.class);
            IdMaptoCount idMaptoCount = IdMaptoCount.parse(rs);
            return ProtoUtils.create_Sm_Common_IdMaptoCount(idMaptoCount);
        } catch (CellParseFailedException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 添加资源
     *
     * @param resourceArr
     */
    public static void addResource(String... resourceArr) {
        String resource = "";
        for (int i = 0; i < resourceArr.length; i++) {
            String s = resourceArr[i];
            if (i != resourceArr.length - 1) {
                resource += s + ",";
            } else {
                resource += s;
            }
        }
        resource = resource.replaceAll(" ", "");
        Cm_GmCommand.Builder b = Cm_GmCommand.newBuilder();
        b.setAction(Cm_GmCommand.Action.SEND);
        b.setContent(GmCommandGroupNameConstants.ItemIo + " " + GmCommandGroupNameConstants.ItemIoGmSupport.Op.getStr() + " " + resource);
        Response response = ClientUtils.send(b.build(), Sm_GmCommand.Action.RESP_SEND);
        if (!response.getResult()) {
            throw new RuntimeException("增加资源出错！");
        }
    }


    /**
     * 设置玩家等级
     *
     * @param lv
     */
    public static void setLv(int lv) {
        Cm_GmCommand.Builder gmb = Cm_GmCommand.newBuilder();
        gmb.setAction(Cm_GmCommand.Action.SEND);
        gmb.setContent(GmCommandGroupNameConstants.Player + " " + GmCommandGroupNameConstants.PlayerIoGmSupport.Lv.getStr() + " " + lv);
        ClientUtils.send(gmb.build(), Sm_GmCommand.Action.RESP_SEND);

    }


    /**
     * 添加玩家等级
     *
     * @param lv
     */
    public static void addLv(int lv) {
        Cm_GmCommand.Builder gmb = Cm_GmCommand.newBuilder();
        gmb.setAction(Cm_GmCommand.Action.SEND);
        gmb.setContent(GmCommandGroupNameConstants.Player + " " + GmCommandGroupNameConstants.PlayerIoGmSupport.ALv.getStr() + " " + lv);
        ClientUtils.send(gmb.build(), Sm_GmCommand.Action.RESP_SEND);

    }

    /**
     * 设置vip等级
     *
     * @param vipLv
     */
    public static void setVipLv(int vipLv) {
        Cm_GmCommand.Builder gmb = Cm_GmCommand.newBuilder();
        gmb.setAction(Cm_GmCommand.Action.SEND);
        gmb.setContent(GmCommandGroupNameConstants.Player + " " + GmCommandGroupNameConstants.PlayerIoGmSupport.VpLv.getStr() + " " + vipLv);
        ClientUtils.send(gmb.build(), Sm_GmCommand.Action.RESP_SEND);

    }

    /**
     * 增加玩家经验
     *
     * @param exp
     */
    public static void addExp(int exp) {
        Cm_GmCommand.Builder gmb = Cm_GmCommand.newBuilder();
        gmb.setAction(Cm_GmCommand.Action.SEND);
        gmb.setContent(GmCommandGroupNameConstants.Player + " " + GmCommandGroupNameConstants.PlayerIoGmSupport.Exp.getStr() + " " + exp);
        Response response = ClientUtils.send(gmb.build(), Sm_GmCommand.Action.RESP_SEND);
        if (!response.getResult()) {
            throw new RuntimeException("增加玩家经验出错");
        }
    }

    /**
     * 触发神秘商店
     */
    public static void triggerMysterious() {
        Cm_GmCommand.Builder gmb = Cm_GmCommand.newBuilder();
        gmb.setAction(Cm_GmCommand.Action.SEND);
        gmb.setContent(GmCommandGroupNameConstants.Shops + " " + ShopsGmSupportEnum.TriggerMysterious.getStr());
        Response response = ClientUtils.send(gmb.build(), Sm_GmCommand.Action.RESP_SEND);
        if (!response.getResult()) {
            throw new RuntimeException("触发神秘商店出错");
        }
    }

    /**
     * 开通单个关卡
     *
     * @param mapId
     */
    public static void openOneChapter(int mapId) {
        Cm_GmCommand.Builder gmb = Cm_GmCommand.newBuilder();
        gmb.setAction(Cm_GmCommand.Action.SEND);
        gmb.setContent(GmCommandGroupNameConstants.Pve + " " + PveGmSupport.OpenOneChapter.getStr() + " " + mapId);
        ClientUtils.send(gmb.build(), Sm_GmCommand.Action.RESP_SEND);
    }

    /**
     * 开通所有关卡
     */
    public static void openAllChapter() {
        Cm_GmCommand.Builder gmb = Cm_GmCommand.newBuilder();
        gmb.setAction(Cm_GmCommand.Action.SEND);
        gmb.setContent(GmCommandGroupNameConstants.Pve + " " + PveGmSupport.OpenAllChapter.getStr());
        ClientUtils.send(gmb.build(), Sm_GmCommand.Action.RESP_SEND);
    }
}


