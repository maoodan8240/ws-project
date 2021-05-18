package ws.gameServer.features.standalone.extp.resourcePoint;

import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.resourcePoint.ctrl.ResourcePointCtrl;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PlayerProtos.Cm_ResourcePoint;
import ws.protos.PlayerProtos.Sm_ResourcePoint;
import ws.relationship.topLevelPojos.resourcePoint.ResourcePoint;
import ws.relationship.utils.ProtoUtils;

public class ResourcePointExtp extends AbstractPlayerExtension<ResourcePointCtrl> {
    public static boolean useExtension = true;

    public ResourcePointExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(ResourcePointCtrl.class, ResourcePoint.class);
    }

    @Override
    public void _initReference() throws Exception {
        getControlerForQuery()._initReference();
    }

    @Override
    public void _postInit() throws Exception {
        getControlerForQuery()._initAll();

    }

    private void onPlayerLogined(In_PlayerLoginedRequest im_PlayerLogined) throws Exception {
        if (im_PlayerLogined.getPlayerId().equals(ownerCtrl.getTarget().getPlayerId())) {
            sync();
        }
    }

    /**
     * 同步资源信息
     */
    public void sync() throws Exception {
        getControlerForQuery().sync();
    }

    @Override
    public void onRecvMyNetworkMsg(Message clientMsg) throws Exception {
        if (clientMsg instanceof Cm_ResourcePoint) {
            Cm_ResourcePoint cm = (Cm_ResourcePoint) clientMsg;
            onCm_ResourcePoint(cm);
        }
    }

    private void onCm_ResourcePoint(Cm_ResourcePoint cm) throws Exception {
        Sm_ResourcePoint.Builder b = Sm_ResourcePoint.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_ResourcePoint.Action.SYNC_VALUE:
                    b.setAction(Sm_ResourcePoint.Action.RESP_SYNC);
                    sync();
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_ResourcePoint, b.getAction());

            getControlerForQuery().send(br.build());
            throw e;
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

    private void onDayChanged() throws Exception {
        getControlerForQuery()._resetDataAtDayChanged();
        getControlerForQuery().sync();
    }

    @Override
    public void onRecvPrivateMsg(PrivateMsg privateMsg) throws Exception {
    }
}
