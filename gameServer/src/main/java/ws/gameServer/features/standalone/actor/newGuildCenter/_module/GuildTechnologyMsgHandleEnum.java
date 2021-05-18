package ws.gameServer.features.standalone.actor.newGuildCenter._module;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import ws.gameServer.features.standalone.actor.newGuildCenter._module.actions.Action_GuildRedBagMsgHandle;
import ws.gameServer.features.standalone.actor.newGuildCenter._module.actions.Action_GuildResearchMsgHandle;
import ws.gameServer.features.standalone.actor.newGuildCenter._module.actions.Action_GuildTrainMsgHandle;
import ws.gameServer.features.standalone.actor.newGuildCenter.ctrl.NewGuildCenterCtrl;

/**
 * Created by lee on 6/8/17.
 */
public enum GuildTechnologyMsgHandleEnum {
    Action_RedBagMsgHandle_(new Action_GuildRedBagMsgHandle()),      // 社团红包
    Action_ResearchMsgHandle_(new Action_GuildResearchMsgHandle()),  //社团研究所
    Action_TrainMsgHandle_(new Action_GuildTrainMsgHandle()),        //社团训练所
    NULL(null);

    private Action action;

    GuildTechnologyMsgHandleEnum(Action action) {
        this.action = action;
    }

    public static void onRecv(NewGuildCenterCtrl actorCtrl, Object msg, ActorContext context, ActorRef sender) throws Exception {
        for (GuildTechnologyMsgHandleEnum handleEnum : GuildTechnologyMsgHandleEnum.values()) {
            if (handleEnum == NULL || handleEnum == null) {
                continue;
            }
            handleEnum.action.onRecv(actorCtrl, msg, context, sender);
        }
    }
}
