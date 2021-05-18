package ws.gameServer.features.standalone.extp.dataCenter.permanent.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.dataCenter.enums.NotifyScopeEnum;
import ws.gameServer.features.standalone.extp.dataCenter.enums.PrivateNotifyTypeEnum;
import ws.gameServer.features.standalone.extp.dataCenter.msg.NotifyObj;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_NotifyMsg;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_UpdateRegNotifyMsg;
import ws.gameServer.features.standalone.extp.dataCenter.stageDaliy.utils.PeriodicDataUtils;
import ws.gameServer.features.standalone.extp.heros.HerosExtp;
import ws.gameServer.features.standalone.extp.heros.ctrl.HerosCtrl;
import ws.gameServer.features.standalone.extp.itemBag.ItemBagExtp;
import ws.gameServer.features.standalone.extp.itemBag.ctrl.ItemBagCtrl;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.relationship.topLevelPojos.dataCenter.permanentData.PermanentData;


public class _PermanentDataCtrl extends AbstractPlayerExteControler<PermanentData> implements PermanentDataCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_PermanentDataCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;
    private HerosCtrl herosCtrl;
    private ItemBagCtrl itemBagCtrl;

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
        herosCtrl = getPlayerCtrl().getExtension(HerosExtp.class).getControlerForQuery();
        itemBagCtrl = getPlayerCtrl().getExtension(ItemBagExtp.class).getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
    }


    @Override
    public void sync() {
    }

    @Override
    public void dealNotifyMsg(Pr_NotifyMsg notifyMsg) {
        LogicCheckUtils.requireNonNull(notifyMsg, Pr_NotifyMsg.class);
        LogicCheckUtils.requireNonNull(notifyMsg.getType(), PrivateNotifyTypeEnum.class);
        NotifyScopeEnum scope = notifyMsg.getType().getScope();
        if (scope == NotifyScopeEnum.DAILY) {
            return;
        }
        PeriodicDataUtils.dealNotifyMsgToTiv(notifyMsg, target.getData().getTiv());
        sendPrivateMsg(new Pr_UpdateRegNotifyMsg(notifyMsg.getType()));
        save();
    }


    @Override
    public void updateRegNotifyMsgHasDone(NotifyObj notifyObj) {
        LogicCheckUtils.requireNonNull(notifyObj, NotifyObj.class);
        LogicCheckUtils.requireNonNull(notifyObj.getType(), PrivateNotifyTypeEnum.class);
        if (target.getData().getTiv().containsKey(notifyObj.getTypeCode())) {
            long valueFinal = PeriodicDataUtils.calcuHasCompleteValue(notifyObj, target.getData().getTiv());
            notifyObj.setValue(valueFinal);
        }
    }
}
