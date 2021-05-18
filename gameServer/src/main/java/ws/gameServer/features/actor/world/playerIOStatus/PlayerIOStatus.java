package ws.gameServer.features.actor.world.playerIOStatus;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Kill;
import akka.actor.Props;
import ws.gameServer.features.actor.world.ctrl.WorldCtrl;
import ws.gameServer.features.actor.world.msg.In_PrepareToKillPlayerActorRequest;
import ws.gameServer.features.actor.world.playerIOStatus.actions.PlayerIOMsgHandleEnums;
import ws.gameServer.features.standalone.actor.playerIO.PlayerIOActor;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.CheckPlayerOnlineMsgRequest;
import ws.relationship.base.msg.MultiCheckPlayerOnlineMsg;

import java.util.ArrayList;
import java.util.List;

public class PlayerIOStatus {
    private WorldCtrl worldCtrl;
    private ActorContext worldActorContext;

    private String playerId;
    private int outerRealmId;
    private int innerRealmId;
    private ActorRef playerIOActorRef;
    private Status status = Status.OutMemory;
    private IOStatus ioStatus = IOStatus.NULL;
    private List<CheckPlayerOnlineMsgRequest<?>> waitingSendCheckOnlineMsgs = new ArrayList<>();

    private long lastOfflineMsgTime = System.currentTimeMillis();
    private long lastHandleCheckOnlineMsgTime = System.currentTimeMillis();
    private long lastIntermediateStateTime = System.currentTimeMillis();

    public PlayerIOStatus(WorldCtrl worldCtrl, ActorContext worldActorContext, String playerId) {
        this.worldCtrl = worldCtrl;
        this.worldActorContext = worldActorContext;
        this.playerId = playerId;
    }

    public ActorRef createPlayerIOActor() {
        if (playerIOActorRef == null) {
            String actorName = ActorSystemPath.WS_GameServer_PlayerIO + playerId;
            playerIOActorRef = worldActorContext.actorOf(Props.create(PlayerIOActor.class, playerId), actorName);
            worldActorContext.watch(playerIOActorRef);
        }
        return playerIOActorRef;
    }

    public void prepareToKillingPlayerIOActor() {
        playerIOActorRef.tell(new In_PrepareToKillPlayerActorRequest(playerId), worldActorContext.self());
    }

    public void killingPlayerIOActor() {
        playerIOActorRef.tell(Kill.getInstance(), worldActorContext.self());
        setIoStatus(IOStatus.Killing);
        playerIOActorRef = null;
    }

    public void killendPlayerIOActor() throws Exception {
        setIoStatus(IOStatus.Killend);
        if (waitingSendCheckOnlineMsgs.size() > 0) {
            handleMsg(waitingSendCheckOnlineMsgs.get(0));
        }
    }

    public void sendCheckPlayerOnlineMsg() {
        setIoStatus(IOStatus.OfflineOperating);
        MultiCheckPlayerOnlineMsg.Request multiMsg = new MultiCheckPlayerOnlineMsg.Request(playerId, waitingSendCheckOnlineMsgs, worldActorContext.self());
        playerIOActorRef.tell(multiMsg, worldActorContext.self());
        waitingSendCheckOnlineMsgs.clear();
        this.lastHandleCheckOnlineMsgTime = System.currentTimeMillis();
    }

    public Status getStatus() {
        switch (ioStatus) {
            case NULL:
                return Status.OutMemory;
            case Ining:
                return Status.OutMemory;
            case Inend:/**
             * 由
             * In_PlayerOfflineRequest // 离线通知
             * In_PlayerHeartBeatingRequest // 心跳信息通知
             * In_PlayerDisconnectedRequest // 断开连接通知
             * 决定
             */
                return status; // 可能是[Status.Online]也可能时[Status.Offline];
            case Killing:
                return Status.Offline;
            case Killend:
                return Status.OutMemory;
            case OfflineOperating: // 离线操作中的前一个状态只会为 NULL Inend Killend 中的一个
                return Status.OutMemory;
        }
        return status;
    }

    public void handleMsg(Object msg) throws Exception {
        PlayerIOMsgHandleEnums.onRecv(msg, this);
    }

    public ActorRef getPlayerIOActorRef() {
        return playerIOActorRef;
    }

    public void setStatus(Status status) {
        this.status = status;
        if (this.status == Status.Offline) {
            this.lastOfflineMsgTime = System.currentTimeMillis();
        }
        this.lastHandleCheckOnlineMsgTime = System.currentTimeMillis();
    }

    public IOStatus getIoStatus() {
        return ioStatus;
    }

    public void setIoStatus(IOStatus ioStatus) {
        this.ioStatus = ioStatus;
        if (IOStatus.isIntermediateStatus(ioStatus)) {
            this.lastIntermediateStateTime = System.currentTimeMillis();
        }
    }

    public long getLastHandleCheckOnlineMsgTime() {
        return lastHandleCheckOnlineMsgTime;
    }

    public void setLastHandleCheckOnlineMsgTime(long lastHandleCheckOnlineMsgTime) {
        this.lastHandleCheckOnlineMsgTime = lastHandleCheckOnlineMsgTime;
    }

    public long getLastOfflineMsgTime() {
        return lastOfflineMsgTime;
    }

    public WorldCtrl getWorldCtrl() {
        return worldCtrl;
    }

    public ActorContext getWorldActorContext() {
        return worldActorContext;
    }

    public List<CheckPlayerOnlineMsgRequest<?>> getWaitingSendCheckOnlineMsgs() {
        return waitingSendCheckOnlineMsgs;
    }

    public String getPlayerId() {
        return playerId;
    }

    public long getLastIntermediateStateTime() {
        return lastIntermediateStateTime;
    }

    public int getOuterRealmId() {
        return outerRealmId;
    }

    public void setOuterRealmId(int outerRealmId) {
        this.outerRealmId = outerRealmId;
    }

    public int getInnerRealmId() {
        return innerRealmId;
    }

    public void setInnerRealmId(int innerRealmId) {
        this.innerRealmId = innerRealmId;
    }
}
