package ws.gameServer.features.standalone.extp.pickCards.ctrl;


import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.relationship.topLevelPojos.pickCards.PickCards;

public interface PickCardsCtrl extends PlayerExteControler<PickCards> {

    // ============================Cm_PickCards start==================================

    /**
     * 免费抽卡
     *
     * @param pickId
     */
    void freePick(int pickId);

    /**
     * 单次抽卡
     *
     * @param pickId
     */
    void pick(int pickId);

    /**
     * 10连抽卡
     *
     * @param pickId
     */

    void tenPick(int pickId);


    /**
     * 100连抽
     *
     * @param pickId
     */
    void hundredPick(int pickId);

    // ============================Cm_PickCards start==================================

}
