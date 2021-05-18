package ws.gameServer.features.actor.register.utils.create.ext;

import ws.gameServer.features.actor.register.utils.create.ext.base.ExtCommonData;
import ws.gameServer.features.actor.register.utils.create.ext.base.ExtensionIniter;
import ws.gameServer.features.standalone.extp.heros.utils.HerosCtrlUtils;
import ws.relationship.table.AllServerConfig;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.topLevelPojos.heros.Heros;

public class _120_HerosExtIniter implements ExtensionIniter {
    @Override
    public void init(ExtCommonData commonData) throws Exception {
        Heros heros = new Heros(commonData.getPlayer().getPlayerId());

        int heroTpId1 = AllServerConfig.PlayerInit_Default_Hero_TpId_1.getConfig();
        int heroTpId2 = AllServerConfig.PlayerInit_Default_Hero_TpId_2.getConfig();
        Hero hero1 = HerosCtrlUtils.addHero(heros, heroTpId1);
        Hero hero2 = HerosCtrlUtils.addHero(heros, heroTpId2);

        commonData.setHeroId1(hero1.getId());
        commonData.setHeroId2(hero2.getId());

        commonData.addPojo(heros);
    }
}
