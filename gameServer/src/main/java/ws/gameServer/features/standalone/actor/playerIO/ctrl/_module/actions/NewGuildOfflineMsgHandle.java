package ws.gameServer.features.standalone.actor.playerIO.ctrl._module.actions;

import akka.actor.ActorRef;
import ws.gameServer.features.standalone.actor.playerIO.ctrl._module.Action;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildAgree;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildKick;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildOneKeyRefuse;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildOneKeyRefuse.Response;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildRefuse;
import ws.gameServer.features.standalone.extp.newGuild.utils.NewGuildCtrlOfflineUtils;

/**
 * Created by lee on 16-12-2.
 */
public class NewGuildOfflineMsgHandle implements Action {
    @Override
    public void handleOfflineMsg(String selfPlayerId, Object msg, ActorRef oriSender) {
        if (msg instanceof In_NewGuildAgree.Response) {
            offlineIn_NewGuildAgree((In_NewGuildAgree.Response) msg);
        } else if (msg instanceof In_NewGuildRefuse.Response) {
            offlineIn_NewGuildRefuse((In_NewGuildRefuse.Response) msg);
        } else if (msg instanceof In_NewGuildKick.Response) {
            offlineIn_NewGuildKick((In_NewGuildKick.Response) msg);
        } else if (msg instanceof In_NewGuildOneKeyRefuse.Response) {
            offlineIn_NewGuildOneKeyRefuse((In_NewGuildOneKeyRefuse.Response) msg);
        }
    }

    private void offlineIn_NewGuildOneKeyRefuse(Response msg) {
        NewGuildCtrlOfflineUtils.offlineIn_NewGuildOneKeyRefuse(msg);
    }

    private void offlineIn_NewGuildKick(In_NewGuildKick.Response msg) {
        NewGuildCtrlOfflineUtils.offlineIn_NewGuildKick(msg);
    }

    private void offlineIn_NewGuildRefuse(In_NewGuildRefuse.Response msg) {
        NewGuildCtrlOfflineUtils.offlineIn_NewGuildRefuse(msg);
    }

    private void offlineIn_NewGuildAgree(In_NewGuildAgree.Response msg) {
        NewGuildCtrlOfflineUtils.offlineIn_NewGuildAgree(msg);
    }


}
