package ws.gameServer.features.standalone.actor.playerIO.ctrl._module.actions;

import akka.actor.ActorRef;
import ws.gameServer.features.standalone.actor.playerIO.ctrl._module.Action;
import ws.gameServer.features.standalone.extp.heros.utils.HerosCtrlOnlineOfflineUtils;
import ws.gameServer.features.standalone.extp.newGuild.msg.train.In_NewGuildTrainBeAccelerate;
import ws.relationship.base.msg.heros.In_QueryBattleHeroContainerInFormation;

/**
 * Created by lee on 5/23/17.
 */
public class HerosOfflineMsgHandle implements Action {
    @Override
    public void handleOfflineMsg(String selfPlayerId, Object msg, ActorRef oriSender) {
        if (msg instanceof In_NewGuildTrainBeAccelerate.Request) {
            onIn_NewGuildTrainAccelerate((In_NewGuildTrainBeAccelerate.Request) msg, oriSender, selfPlayerId);
        } else if (msg instanceof In_QueryBattleHeroContainerInFormation.Request) {
            onIn_QueryBattleHeroContainerInFormation((In_QueryBattleHeroContainerInFormation.Request) msg, oriSender);
        }
    }

    private void onIn_QueryBattleHeroContainerInFormation(In_QueryBattleHeroContainerInFormation.Request request, ActorRef oriSender) {
        HerosCtrlOnlineOfflineUtils.onQueryBattleHeroContainerInFormation(request.getType(), request.getOuterRealmId(), request.getPlayerId(), oriSender);
    }

    private void onIn_NewGuildTrainAccelerate(In_NewGuildTrainBeAccelerate.Request msg, ActorRef oriSender, String selfPlayerId) {
        // 这个消息是社团训练所的英雄被动加速是发送过来的
        HerosCtrlOnlineOfflineUtils.onTrainerAccelerate_offline(msg.getHeroId(), oriSender, selfPlayerId, msg.getOutRealmId());
    }
}
