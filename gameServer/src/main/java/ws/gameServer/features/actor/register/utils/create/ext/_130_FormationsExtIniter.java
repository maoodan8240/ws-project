
package ws.gameServer.features.actor.register.utils.create.ext;

import ws.gameServer.features.actor.register.utils.create.ext.base.ExtCommonData;
import ws.gameServer.features.actor.register.utils.create.ext.base.ExtensionIniter;
import ws.gameServer.features.standalone.extp.formations.utils.FormationsCtrlUtils;
import ws.protos.EnumsProtos.FormationTypeEnum;
import ws.protos.EnumsProtos.HeroPositionEnum;
import ws.relationship.topLevelPojos.formations.Formation;
import ws.relationship.topLevelPojos.formations.FormationPos;
import ws.relationship.topLevelPojos.formations.Formations;

public class _130_FormationsExtIniter implements ExtensionIniter {
    @Override
    public void init(ExtCommonData commonData) throws Exception {
        Formations formations = new Formations(commonData.getPlayer().getPlayerId());

        Formation formation = new Formation(FormationTypeEnum.F_MAIN);
        FormationsCtrlUtils.initFormation(formation);
        // 草稚京 默认位置
        formation.getPosToFormationPos().put(HeroPositionEnum.HERO_POSITION_TWO, new FormationPos(HeroPositionEnum.HERO_POSITION_TWO, commonData.getHeroId1()));
        // 坂崎百合 默认位置
        formation.getPosToFormationPos().put(HeroPositionEnum.HERO_POSITION_FOUR, new FormationPos(HeroPositionEnum.HERO_POSITION_FOUR, commonData.getHeroId2()));
        formations.getTypeToFormation().put(FormationTypeEnum.F_MAIN, formation);

        commonData.addPojo(formations);
    }
}
