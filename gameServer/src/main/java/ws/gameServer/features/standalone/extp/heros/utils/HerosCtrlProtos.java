package ws.gameServer.features.standalone.extp.heros.utils;

import ws.gameServer.features.standalone.extp.heros.ctrl.HerosCtrl;
import ws.protos.HerosProtos.Sm_Heros;
import ws.protos.HerosProtos.Sm_Heros.Action;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.utils.ProtoUtils;

public class HerosCtrlProtos {

    public static Sm_Heros.Builder create_Sm_Heros_Dynamic(HerosCtrl herosCtrl, IdMaptoCount idMaptoCount) {
        Sm_Heros.Builder b = Sm_Heros.newBuilder();
        b.setAction(Action.RESP_SYNC_PART);
        for (Integer mid : idMaptoCount.getAllKeys()) {
            if (herosCtrl.containsHeroById(mid)) {
                Hero hero = herosCtrl.getHero(mid);
                b.addHeros(ProtoUtils.create_Sm_Common_Hero(hero));
            } else {
                b.addDeleteIds(mid);
            }
        }
        return b;
    }

}
