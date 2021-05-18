package ws.gameServer.features.actor.world;

import ws.common.utils.di.GlobalInjector;
import ws.gameServer.features.actor.world._msgModule.WorldActorMsgHandleEnums;
import ws.gameServer.features.actor.world.ctrl.WorldCtrl;
import ws.gameServer.features.actor.world.pojo.World;
import ws.gameServer.features.actor.world.utils.IntermediateStateHandle;
import ws.relationship.base.actor.WsActor;

public class WorldActor extends WsActor {
    private WorldCtrl worldCtrl;

    public WorldActor() {
        this.enableWsActorLogger = false;
        WorldCtrl worldCtrl = GlobalInjector.getInstance(WorldCtrl.class);
        worldCtrl.setTarget(new World());
        this.worldCtrl = worldCtrl;
        new IntermediateStateHandle(worldCtrl).run();
    }

    @Override
    public void onRecv(Object msg) throws Exception {
        WorldActorMsgHandleEnums.onRecv(msg, worldCtrl, getContext(), getSender());
    }
}
