package ws.gameServer.features.standalone.extp.hof.ctrl;


import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.protos.HOFProtos.Cm_HOF_HeroAndFood;
import ws.relationship.topLevelPojos.hof.HOF;

import java.util.List;

public interface HOFCtrl extends PlayerExteControler<HOF> {


    /**
     * 喂养
     * @param heroAndFoodList
     */
    void eat(List<Cm_HOF_HeroAndFood> heroAndFoodList);

    /**
     * 突破
     * @param heroId
     */
    void breakthrough(int heroId);

    /**
     * 一键喂养
     * @param heroId
     */
    void eatAndBreak(int heroId);
}
