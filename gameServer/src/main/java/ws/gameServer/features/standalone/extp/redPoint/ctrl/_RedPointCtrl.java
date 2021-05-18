package ws.gameServer.features.standalone.extp.redPoint.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.di.GlobalInjector;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.redPoint.msg.Pr_CheckRedPointMsg;
import ws.gameServer.features.standalone.extp.redPoint.msg.Pr_NotifyRedPointMsg;
import ws.gameServer.features.standalone.extp.redPoint.utils.RedPointCtrlProtos;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.gameServer.system.date.dayChanged.DayChanged;
import ws.protos.EnumsProtos.RedPointEnum;
import ws.protos.RedPointProtos.Sm_RedPoint;
import ws.relationship.topLevelPojos.common.TopLevelHolder;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

public class _RedPointCtrl extends AbstractPlayerExteControler<TopLevelHolder> implements RedPointCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_RedPointCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;
    private HashMap<RedPointEnum, Boolean> redPointToShow = new HashMap<>();
    private Timer timer = new Timer();
    private static final long delay = 30;                   //延迟30毫秒

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
        if (!GlobalInjector.getInstance(DayChanged.class).getDayChangedStr().equals(target.getLastResetDay())) {
            target.setLastResetDay(GlobalInjector.getInstance(DayChanged.class).getDayChangedStr());
            save();
        }
    }

    @Override
    public void sync() {
        Pr_CheckRedPointMsg.Request request = new Pr_CheckRedPointMsg.Request();
        sendPrivateMsg(request);
    }

    private void syncSendRedPoint(HashMap<RedPointEnum, Boolean> redPointToShow) {
        for (Entry<RedPointEnum, Boolean> entry : redPointToShow.entrySet()) {
            getRedPointToShow().put(entry.getKey(), entry.getValue());
        }
        timer.cancel();
        timer = new Timer();
        timer.schedule(new SendRedPoint(this), delay);
    }

    public HashMap<RedPointEnum, Boolean> getRedPointToShow() {
        return redPointToShow;
    }

    private class SendRedPoint extends TimerTask {

        private _RedPointCtrl redPointCtrl;

        public SendRedPoint(_RedPointCtrl redPointCtrl) {
            this.redPointCtrl = redPointCtrl;
        }

        @Override
        public void run() {
            SenderFunc.sendInner(redPointCtrl, Sm_RedPoint.class, Sm_RedPoint.Builder.class, Sm_RedPoint.Action.RESP_SYNC, (b, br) -> {
                b.addAllRedPointInfos(RedPointCtrlProtos.create_Sm_RedPoint_Info_List(redPointCtrl.getRedPointToShow()));
                redPointCtrl.getRedPointToShow().clear();
            });
        }
    }

    @Override
    public void notifyRedPoint(Pr_NotifyRedPointMsg.Request privateMsg) {
        SenderFunc.sendInner(this, Sm_RedPoint.class, Sm_RedPoint.Builder.class, Sm_RedPoint.Action.RESP_NOTIFY, (b, br) -> {
            b.addAllRedPointInfos(RedPointCtrlProtos.create_Sm_RedPoint_Info_List(privateMsg.getRedPointToShow()));
        });
    }

    @Override
    public void checkRedPointMsg(Pr_CheckRedPointMsg.Response privateMsg) {
        syncSendRedPoint(privateMsg.getRedPointToShow());
    }
}