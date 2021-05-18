package ws.gameServer.features.standalone.actor.player.gm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.enums.PlayerEnums;
import ws.gameServer.features.standalone.actor.player.msg.Pr_PlayerLvChanged;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PlayerProtos.Sm_GmCommand;
import ws.relationship.enums.GmCommandFromTypeEnum;
import ws.relationship.gm.GmCommand;
import ws.relationship.gm.GmCommandUtils;
import ws.relationship.table.AllServerConfig;

public class PlayerGmSupport implements GmCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerGmSupport.class);

    private PlayerCtrl playerCtrl;

    public PlayerGmSupport(PlayerCtrl playerCtrl) {
        this.playerCtrl = playerCtrl;
    }

    @Override
    public void exec(GmCommandFromTypeEnum fromTypeEnum, String commandName, String[] args) {
        try {
            if (commandName.equals("lv")) {
                onSetLevel(commandName, args);
            } else if (commandName.equals("alv")) {
                onAddLevel(commandName, args);
            } else if (commandName.equals("vplv")) {
                onSetVipLevel(commandName, args);
            } else if (commandName.equals("recharge")) {
                onRecharge(args);
            } else {
                sendResult(false);
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            sendResult(false);
        }
    }

    /**
     * Gm 充值
     */
    private void onRecharge(String[] args) {
        long value = GmCommandUtils.parseInt(args)[0];
        getPlayerCtrl().addVipMoneyByRecharge((int) value, PlayerEnums.GM_RECHARGE);
    }

    /**
     * 更改玩家等级
     *
     * @param commandName
     * @param args
     */
    private void onSetLevel(String commandName, String[] args) {
        int maxLv = AllServerConfig.Player_CurMaxLv.getConfig();
        long valueToSet = GmCommandUtils.parseInt(args)[0];
        valueToSet = Math.min(maxLv, valueToSet);
        playerCtrl.getTarget().getBase().setLevel((int) valueToSet);
        getPlayerCtrl().statisticsPlayerLevelUp();
        getPlayerCtrl().sendPrivateMsg(new Pr_PlayerLvChanged());
        playerCtrl.sync();
        sendResult(true);
        playerCtrl.save();
    }

    /**
     * 增加玩家的等级
     *
     * @param commandName
     * @param args
     */
    private void onAddLevel(String commandName, String[] args) {
        long valueToAdd = 1;
        if (args.length != 0) {
            valueToAdd = GmCommandUtils.parseInt(args)[0];
        }
        int maxLv = AllServerConfig.Player_CurMaxLv.getConfig();
        int oldLevel = playerCtrl.getTarget().getBase().getLevel();
        int newLevel = (int) (oldLevel + valueToAdd);
        newLevel = Math.min(maxLv, newLevel);
        playerCtrl.getTarget().getBase().setLevel(newLevel);
        getPlayerCtrl().statisticsPlayerLevelUp();
        getPlayerCtrl().sendPrivateMsg(new Pr_PlayerLvChanged());
        playerCtrl.sync();
        sendResult(true);
        playerCtrl.save();
    }


    /**
     * 设置玩家vip等级
     *
     * @param commandName
     * @param args
     */
    private void onSetVipLevel(String commandName, String[] args) {
        long valueToSet = GmCommandUtils.parseInt(args)[0];
        int maxVipLv = AllServerConfig.Player_Max_VIPLv.getConfig();
        valueToSet = Math.min(maxVipLv, valueToSet);
        playerCtrl.getTarget().getPayment().setVipLevel((int) valueToSet);
        playerCtrl.sync();
        sendResult(true);
        playerCtrl.save();
    }


    private void sendResult(boolean rs) {
        Response response = SenderFunc.buildResponse(Sm_GmCommand.class, Sm_GmCommand.Builder.class, Sm_GmCommand.Action.RESP_SEND, (b, br) -> {
            br.setResult(rs);
        });
        playerCtrl.sendResponse(response);
    }

    public PlayerCtrl getPlayerCtrl() {
        return playerCtrl;
    }

    public void setPlayerCtrl(PlayerCtrl playerCtrl) {
        this.playerCtrl = playerCtrl;
    }

}
