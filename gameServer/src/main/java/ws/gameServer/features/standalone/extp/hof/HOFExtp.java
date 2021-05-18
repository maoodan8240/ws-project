package ws.gameServer.features.standalone.extp.hof;


import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.hof.ctrl.HOFCtrl;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.HOFProtos.Cm_HOF;
import ws.protos.HOFProtos.Cm_HOF_HeroAndFood;
import ws.protos.HOFProtos.Sm_HOF;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.topLevelPojos.hof.HOF;
import ws.relationship.utils.ProtoUtils;

import java.util.List;


public class HOFExtp extends AbstractPlayerExtension<HOFCtrl> {
    public static boolean useExtension = false;

    public HOFExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(HOFCtrl.class, HOF.class);
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
        if (clientMsg instanceof Cm_HOF) {
            Cm_HOF cm = (Cm_HOF) clientMsg;
            onCm_HOF(cm);
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

    private void onCm_HOF(Cm_HOF cm) throws Exception {
        Sm_HOF.Builder b = Sm_HOF.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_HOF.Action.SYNC_VALUE:
                    b.setAction(Sm_HOF.Action.RESP_SYNC);
                    sync();
                    break;
                case Cm_HOF.Action.EAT_VALUE:
                    eat(cm.getHeroAndFoodList());
                    break;
                case Cm_HOF.Action.BREAK_VALUE:
                    breakthrough(cm.getHeroId());
                    break;
                case Cm_HOF.Action.EAT_AND_BREAK_VALUE:
                    eatAndBreak(cm.getHeroId());
                    break;
                case Cm_HOF.Action.QUICK_EAT_AND_BREAK_VALUE:
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_HOF, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
    }

    private void eatAndBreak(int heroId) {
        getControlerForQuery().eatAndBreak(heroId);
    }

    private void breakthrough(int heroId) {
        getControlerForQuery().breakthrough(heroId);
    }

    private void eat(List<Cm_HOF_HeroAndFood> heroAndFoodList) {
        getControlerForQuery().eat(heroAndFoodList);
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


}
