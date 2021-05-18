package ws.gameServer.features.standalone.actor.playerIO.ctrl._module;

import akka.actor.ActorRef;
import ws.gameServer.features.standalone.actor.playerIO.ctrl._module.actions.ArenaOfflineMsgHandle;
import ws.gameServer.features.standalone.actor.playerIO.ctrl._module.actions.FriendsOfflineMsgHandle;
import ws.gameServer.features.standalone.actor.playerIO.ctrl._module.actions.HerosOfflineMsgHandle;
import ws.gameServer.features.standalone.actor.playerIO.ctrl._module.actions.MailOfflineMsgHandle;
import ws.gameServer.features.standalone.actor.playerIO.ctrl._module.actions.NewGuildOfflineMsgHandle;

public enum OfflineMsgContainer {
    /**
     * 新社团
     */
    NEW_GUI(new NewGuildOfflineMsgHandle()),
    /**
     * 邮件
     */
    MAIL(new MailOfflineMsgHandle()),

    /**
     * 好友
     */
    Friends(new FriendsOfflineMsgHandle()),

    /**
     * 格斗家
     */
    Heros(new HerosOfflineMsgHandle()),
    /**
     * 竞技场
     */
    Arena(new ArenaOfflineMsgHandle()),
    /**
     * defalut
     */
    NULL(null);

    private Action action;

    OfflineMsgContainer(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public static void handleOfflineMsg(String selfPlayerId, Object msg, ActorRef oriSender) {
        for (OfflineMsgContainer container : values()) {
            if (container != null && container != NULL) {
                container.getAction().handleOfflineMsg(selfPlayerId, msg, oriSender);
            }
        }
    }
}
