package ws.gameServer.features.standalone.extp.talent.ctrl;


import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.relationship.topLevelPojos.talent.Talent;

public interface TalentCtrl extends PlayerExteControler<Talent> {


    /**
     * 升级天赋
     *
     * @param talentLevelId
     */
    void upLevel(int talentLevelId);

    /**
     * 重置天赋点
     */
    void reset();
}
