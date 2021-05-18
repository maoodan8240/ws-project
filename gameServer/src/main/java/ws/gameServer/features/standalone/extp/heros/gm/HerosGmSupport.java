package ws.gameServer.features.standalone.extp.heros.gm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.extp.heros.ctrl.HerosCtrl;
import ws.relationship.base.HeroAttrs;
import ws.relationship.enums.GmCommandFromTypeEnum;
import ws.relationship.gm.GmCommand;
import ws.relationship.gm.GmCommandGroupNameConstants;
import ws.relationship.gm.GmCommandUtils;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.utils.attrs.HeroAttrsUtils;
import ws.relationship.utils.attrs.OneHeroAttrs;

public class HerosGmSupport implements GmCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(HerosGmSupport.class);
    private HerosCtrl herosCtrl;

    public HerosGmSupport(HerosCtrl herosCtrl) {
        this.herosCtrl = herosCtrl;
    }

    @Override
    public void exec(GmCommandFromTypeEnum fromTypeEnum, String commandName, String[] args) {
        if (GmCommandGroupNameConstants.HerosGmSupport.calcu_Hero_attr.getStr().equals(commandName)) {
            onOp(fromTypeEnum, args);
        }
    }

    private void onOp(GmCommandFromTypeEnum fromTypeEnum, String[] args) {
        int heroTpId = GmCommandUtils.parseInt(args)[0];
        Hero hero = herosCtrl.getHeroByTpId(heroTpId);
        OneHeroAttrs oneHeroAttrs = herosCtrl.getAttrsContainer().getOneHeroAttrs(hero);
        LOGGER.info("\n{} 属性细节---> \n{} ", hero.getTpId(), oneHeroAttrs.print());
        HeroAttrs heroAttrs = herosCtrl.getAttrsContainer().getHeroAttrs(hero.getId());
        LOGGER.info("\n最终属性---> \n{} ", HeroAttrsUtils.printHeroAttrs(heroAttrs));
        long battleValue = herosCtrl.getAttrsContainer().getHeroBattleValue(hero.getId());
        LOGGER.info("\n战斗力---> {} ", battleValue);
    }
}
