package ws.gameServer.features.standalone.extp.pyActivities.soulBox.ctrl;


import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.relationship.topLevelPojos.soulBox.SoulBox;

public interface SoulBoxCtrl extends PlayerExteControler<SoulBox> {

    void pick(int pickTimes, int pickId);

    void select(int heroId);
}
