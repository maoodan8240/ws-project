package ws.gameServer.features.standalone.extp.formations.utils;

import ws.protos.EnumsProtos.HeroPositionEnum;
import ws.relationship.topLevelPojos.formations.Formation;
import ws.relationship.topLevelPojos.formations.FormationPos;

public class FormationsCtrlUtils {

    /**
     * 初始化一个阵容
     *
     * @param formation
     */
    public static void initFormation(Formation formation) {
        for (HeroPositionEnum positionEnum : HeroPositionEnum.values()) {
            if (formation.getPosToFormationPos().containsKey(positionEnum)) {
                continue;
            }
            formation.getPosToFormationPos().put(positionEnum, new FormationPos(positionEnum));
        }
    }

}
