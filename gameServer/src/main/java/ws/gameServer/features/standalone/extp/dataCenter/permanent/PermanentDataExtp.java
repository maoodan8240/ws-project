package ws.gameServer.features.standalone.extp.dataCenter.permanent;

import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_NotifyMsg;
import ws.gameServer.features.standalone.extp.dataCenter.permanent.ctrl.PermanentDataCtrl;
import ws.relationship.topLevelPojos.dataCenter.permanentData.PermanentData;

/**
 * 阶段性每日数据
 */
public class PermanentDataExtp extends AbstractPlayerExtension<PermanentDataCtrl> {
    public static boolean useExtension = true;

    public PermanentDataExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(PermanentDataCtrl.class, PermanentData.class);
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
    }


    @Override
    public void onRecvInnerMsg(InnerMsg innerMsg) throws Exception {
    }


    @Override
    public void onRecvPrivateMsg(PrivateMsg privateMsg) throws Exception {
        if (privateMsg instanceof Pr_NotifyMsg) {
            onNotifyMsg((Pr_NotifyMsg) privateMsg);
        }
    }


    private void onNotifyMsg(Pr_NotifyMsg notifyMsg) {
        getControlerForQuery().dealNotifyMsg(notifyMsg);
    }
}
