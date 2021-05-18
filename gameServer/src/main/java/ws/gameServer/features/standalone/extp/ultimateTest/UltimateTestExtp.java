package ws.gameServer.features.standalone.extp.ultimateTest;


import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.ultimateTest.ctrl.UltimateTestCtrl;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.EnumsProtos.HardTypeEnum;
import ws.protos.EnumsProtos.UltimateTestBuffIndexTypeEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.UltimateTestProtos.Cm_UltimateTest;
import ws.protos.UltimateTestProtos.Cm_UltimateTest.Action;
import ws.protos.UltimateTestProtos.Sm_UltimateTest;
import ws.protos.UltimateTestProtos.Sm_UltimateTest.Builder;
import ws.protos.UltimateTestProtos.Sm_UltimateTest_Hero_Info;
import ws.protos.UltimateTestProtos.Sm_UltimateTest_Use_Buff;
import ws.relationship.topLevelPojos.ultimateTest.UltimateTest;
import ws.relationship.utils.ProtoUtils;

import java.util.List;


public class UltimateTestExtp extends AbstractPlayerExtension<UltimateTestCtrl> {
    public static boolean useExtension = true;

    private Object speReward;
    private Object buff;

    public UltimateTestExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(UltimateTestCtrl.class, UltimateTest.class);
    }


    @Override
    public void _initReference() throws Exception {
        getControlerForQuery()._initReference();

    }

    @Override
    public void _postInit() throws Exception {
        getControlerForQuery()._initAll();
    }

    @Override
    public void onRecvMyNetworkMsg(Message clientMsg) throws Exception {
        if (clientMsg instanceof Cm_UltimateTest) {
            Cm_UltimateTest cm = (Cm_UltimateTest) clientMsg;
            onCm_Example(cm);
        }
    }

    @Override
    public void onRecvInnerMsg(InnerMsg innerMsg) throws Exception {
        if (innerMsg instanceof In_PlayerLoginedRequest) {
            onPlayerLogined((In_PlayerLoginedRequest) innerMsg);
        } else if (innerMsg instanceof In_DayChanged) {
            onDayChanged();
        }
    }

    @Override
    public void onRecvPrivateMsg(PrivateMsg privateMsg) throws Exception {
    }

    private void onCm_Example(Cm_UltimateTest cm) throws Exception {
        Builder b = Sm_UltimateTest.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Action.SYNC_VALUE:
                    b.setAction(Sm_UltimateTest.Action.RESP_SYNC);
                    sync();
                    break;
                case Action.BEGIN_ATTACK_VALUE:
                    b.setAction(Sm_UltimateTest.Action.RESP_BEGIN_ATTACK);
                    beginAttack(cm.getHardLevel());
                    break;
                case Action.END_ATTACK_VALUE:
                    b.setAction(Sm_UltimateTest.Action.RESP_END_ATTACK);
                    endAttack(cm.getFlag(), cm.getStageLevel(), cm.getHeroIdsList(), cm.getStar(), cm.getIsWin(), cm.getHardLevel());
                    break;
                case Action.GET_ENEMY_VALUE:
                    b.setAction(Sm_UltimateTest.Action.RESP_GET_ENEMY);
                    getEnemy();
                    break;
                case Action.OPEN_BOX_VALUE:
                    b.setAction(Sm_UltimateTest.Action.RESP_OPEN_BOX);
                    openBox(cm.getStageLevel());
                    break;
                case Action.BUFF_VALUE:
                    b.setAction(Sm_UltimateTest.Action.RESP_BUFF);
                    buyBuff(cm.getBuffIndex());
                    break;
                case Action.OPEN_ALL_BOX_VALUE:
                    b.setAction(Sm_UltimateTest.Action.RESP_OPEN_ALL_BOX);
                    openAllBox(cm.getTimes());
                    break;
                case Action.GET_REWARD_VALUE:
                    b.setAction(Sm_UltimateTest.Action.RESP_GET_REWARD);
                    getReward();
                    break;
                case Action.SPE_REWARD_VALUE:
                    b.setAction(Sm_UltimateTest.Action.RESP_SPE_REWARD);
                    speReward();
                    break;
                case Action.GET_SPE_REWARD_VALUE:
                    b.setAction(Sm_UltimateTest.Action.RESP_GET_SPE_REWARD);
                    getSpeReward(cm.getRewardScore());
                    break;
                case Action.GO_TO_LEVEL_VALUE:
                    b.setAction(Sm_UltimateTest.Action.RESP_GO_TO_LEVEL);
                    goToLevel();
                    break;
                case Action.GET_BUFF_VALUE:
                    b.setAction(Sm_UltimateTest.Action.RESP_GET_BUFF);
                    getBuff();
                    break;
                case Action.USE_BUFF_VALUE:
                    b.setAction(Sm_UltimateTest.Action.RESP_USE_BUFF);
                    useBuff(cm.getUseBuffsList(), cm.getBuffIndex());
                    break;
                case Action.CHANGE_ICON_VALUE:
                    b.setAction(Sm_UltimateTest.Action.RESP_CHANGE_ICON);
                    changeIcon(cm.getIcon());
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_UltimateTest, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
    }


    private void useBuff(List<Sm_UltimateTest_Use_Buff> useBuffs, UltimateTestBuffIndexTypeEnum buffIndex) {
        getControlerForQuery().useBuff(useBuffs, buffIndex);
    }

    private void goToLevel() {
        getControlerForQuery().goToLevel();
    }

    private void openAllBox(int times) {
        getControlerForQuery().openAllBox(times);
    }

    private void speReward() {
        getControlerForQuery().speReward();
    }

    private void buyBuff(UltimateTestBuffIndexTypeEnum buffIndex) {
        getControlerForQuery().buyBuff(buffIndex);
    }

    private void openBox(int stageLevel) {
        getControlerForQuery().openBox(stageLevel);
    }


    private void endAttack(long flag, int stageLevel, List<Sm_UltimateTest_Hero_Info> heroIdsList, int star, boolean isWin, HardTypeEnum hardLevel) {
        getControlerForQuery().endAttack(flag, stageLevel, heroIdsList, star, isWin, hardLevel);
    }

    private void beginAttack(HardTypeEnum hardLevel) {
        getControlerForQuery().beginAttack(hardLevel);
    }

    private void onPlayerLogined(In_PlayerLoginedRequest im_PlayerLogined) throws Exception {
        if (im_PlayerLogined.getPlayerId().equals(ownerCtrl.getTarget().getPlayerId())) {
            // 本玩家, 同步信息
            // sync();
        }
    }

    private void onDayChanged() throws Exception {
        getControlerForQuery()._resetDataAtDayChanged();
        getControlerForQuery().sync();
    }

    private void sync() {
        getControlerForQuery().sync();
    }


    public void getEnemy() {
        getControlerForQuery().getEnemy();
    }

    public void getReward() {
        getControlerForQuery().getReward();
    }

    public void getSpeReward(int rewardScore) {
        getControlerForQuery().getSpeReward(rewardScore);
    }

    public void getBuff() {
        getControlerForQuery().getBuffInfo();
    }

    private void changeIcon(int icon) {
        getControlerForQuery().changeIcon(icon);
    }
}
