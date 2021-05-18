package ws.gameServer.features.standalone.actor.player.mc.controler;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import com.google.protobuf.TextFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.redis.RedisOpration;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.mc.controler.AbstractControler;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.system.date.dayChanged.DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.msg.In_MessagePassToGatewayServer;
import ws.relationship.topLevelPojos.PlayerTopLevelPojo;
import ws.relationship.topLevelPojos.common.TopLevelHolder;

import java.util.List;

public abstract class AbstractPlayerExteControler<T extends PlayerTopLevelPojo> extends AbstractControler<T> implements PlayerExteControler<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPlayerExteControler.class);
    protected static final RedisOpration REDIS_OPRATION = GlobalInjector.getInstance(RedisOpration.class);
    private PlayerCtrl playerCtrl;
    protected transient ActorSelection ownerActorSelection;

    @Override
    public PlayerCtrl getPlayerCtrl() {
        return playerCtrl;
    }

    @Override
    public void setPlayerCtrl(PlayerCtrl playerCtrl) {
        this.playerCtrl = playerCtrl;
    }

    @Override
    public final void _initAll() throws Exception {
        _initBeforeChanged();
        _resetDataAtDayChanged0();
        _initAfterChanged();
    }

    public abstract void _initReference() throws Exception;

    public void _resetDataAtDayChanged0() throws Exception {
        if (target == null || target instanceof TopLevelHolder) {
            return;
        }
        if (!GlobalInjector.getInstance(DayChanged.class).getDayChangedStr().equals(target.getLastResetDay())) {
            _resetDataAtDayChanged();
            target.setLastResetDay(GlobalInjector.getInstance(DayChanged.class).getDayChangedStr());
        }
    }

    public void _initAfterChanged() throws Exception {
    }

    public abstract void _resetDataAtDayChanged() throws Exception;

    public void _initBeforeChanged() throws Exception {
    }

    @Override
    public abstract void sync();

    @Override
    public void refreshItem(IdMaptoCount idMaptoCount) {
    }

    @Override
    public void refreshItemAddToResponse(IdMaptoCount idMaptoCount, Response.Builder br) {
    }

    @Override
    public void send(Response response) {
        In_MessagePassToGatewayServer messagePassToGatewayServer = new In_MessagePassToGatewayServer(playerCtrl.getConnFlag(), response, response.getSmMsgAction());
        if (LOGGER.isDebugEnabled()) {
            String actionName = response.getSmMsgAction().replaceAll("Sm_", "Cm_").replaceAll("RESP_", "");
            messagePassToGatewayServer.getTimes().addAll(playerCtrl.getNetworkMsgTimes(actionName));
            if (messagePassToGatewayServer.getTimes().size() > 0) {
                List<Long> times = playerCtrl.getNetworkMsgTimes(actionName);
                long timeRev = times.get(times.size() - 1); // PlayerActor 接收信息的时间点
                long timeEnd = System.nanoTime();
                messagePassToGatewayServer.getTimes().add(timeEnd);
                messagePassToGatewayServer.getTimes().add(timeEnd);
                double cost = ((timeEnd - timeRev) * 1.0) / 1000000;
                LOGGER.debug("\nPlayerActor中处理客户端请求耗时={}毫秒，请求为={}\n", cost, actionName);
            }
            if (response.getMsgCode() != Code.Sm_HeartBeat) {
                LOGGER.debug("\n服务器响应的内容为:\nto-->playerId;{}\n《\n{}》\n\n\n", getPlayerCtrl().getPlayerId(), TextFormat.printToUnicodeString(response));
            }
        }
        getPlayerCtrl().getGatewaySender().tell(messagePassToGatewayServer, playerCtrl.getActorRef());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void save() {
        this.playerCtrl.addNeedSavePojo(this.target);
    }

    @Override
    public void sendPrivateMsg(PrivateMsg privateMsg) {
        this.playerCtrl.getActorRef().tell(privateMsg, ActorRef.noSender());
    }
}
