package ws.gameServer.features.standalone.actor.playerIO.ctrl._module;

import akka.actor.ActorRef;
import ws.gameServer.features.standalone.actor.playerIO.ctrl._module.actions.FriendsOfflineMsgHandle;
import ws.gameServer.features.standalone.actor.playerIO.ctrl._module.actions.MailOfflineMsgHandle;

public enum OfflineMsgContainer {
    /**
     * 邮件
     */
    MAIL(new MailOfflineMsgHandle()),

    /**
     * 好友
     */
    Friends(new FriendsOfflineMsgHandle()),

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
