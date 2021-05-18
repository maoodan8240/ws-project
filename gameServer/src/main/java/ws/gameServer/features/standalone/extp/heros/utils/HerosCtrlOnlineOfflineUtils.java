package ws.gameServer.features.standalone.extp.heros.utils;

import akka.actor.ActorRef;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.extp.formations.FormationsExtp;
import ws.gameServer.features.standalone.extp.heros.HerosExtp;
import ws.gameServer.features.standalone.extp.newGuild.msg.train.In_NewGuildTrainBeAccelerate;
import ws.gameServer.features.standalone.extp.newGuild.utils.NewGuildCtrlUtils;
import ws.newBattle.NewBattleHeroContainer;
import ws.protos.EnumsProtos.FormationTypeEnum;
import ws.relationship.base.HeroAttrs;
import ws.relationship.base.msg.heros.In_QueryBattleHeroContainerInFormation;
import ws.relationship.topLevelPojos.formations.Formation;
import ws.relationship.topLevelPojos.formations.Formations;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.topLevelPojos.heros.Heros;
import ws.relationship.topLevelPojos.player.Player;
import ws.relationship.topLevelPojos.talent.Talent;
import ws.relationship.utils.DBUtils;
import ws.relationship.utils.attrs.HeroAttrsContainer;

import java.util.Map;

/**
 * Created by zhangweiwei on 17-4-5.
 */
public class HerosCtrlOnlineOfflineUtils {

    public static void onQueryBattleHeroContainerInFormation(FormationTypeEnum type, int outerRealmId, String playerId, ActorRef oriSender) {
        Heros heros = DBUtils.getHashPojo(playerId, outerRealmId, Heros.class);
        Talent talent = DBUtils.getHashPojo(playerId, outerRealmId, Talent.class);
        Formations formations = DBUtils.getHashPojo(playerId, outerRealmId, Formations.class);
        Formation formation = formations.getTypeToFormation().get(type);
        NewBattleHeroContainer heroContainer = createFormationBattleHeroContainer_offline(heros, talent, formation);
        oriSender.tell(new In_QueryBattleHeroContainerInFormation.Response(heroContainer), ActorRef.noSender());
    }


    // online
    public static NewBattleHeroContainer createFormationBattleHeroContainer_Online(FormationTypeEnum type, PlayerCtrl playerCtrl) {
        Heros heros = playerCtrl.getExtension(HerosExtp.class).getControlerForQuery().getTarget();
        Formation formation = playerCtrl.getExtension(FormationsExtp.class).getControlerForQuery().getOneFormation(type);
        Map<Integer, HeroAttrs> attrsMap = playerCtrl.getExtension(HerosExtp.class).getControlerForQuery().getAttrsContainer().getHeroAttrs(formation);
        return new NewBattleHeroContainer(formation, heros, attrsMap);
    }


    // offline
    public static NewBattleHeroContainer createFormationBattleHeroContainer_offline(Heros heros, Talent talent, Formation formation) {
        HeroAttrsContainer attrsContainer = HerosCtrlUtils.createHeroAttrsContainer_F_OnlyInF(heros, talent, formation);
        Map<Integer, HeroAttrs> attrsMap = attrsContainer.getHeroAttrs(formation);
        return new NewBattleHeroContainer(formation, heros, attrsMap);
    }


    public static void onTrainerAccelerate_offline(int heroId, ActorRef oriSender, String selfPlayerId, int outRealmId) {
        Heros heros = DBUtils.getHashPojo(selfPlayerId, outRealmId, Heros.class);
        Hero hero = heros.getIdToHero().get(heroId);
        int exp = NewGuildCtrlUtils.mathAccelerateHeroExp(hero);
        Player player = DBUtils.getHashPojo(selfPlayerId, outRealmId, Player.class);
        HerosCtrlUtils.upgradeHeroLevel(hero, exp, player.getBase().getLevel());
        In_NewGuildTrainBeAccelerate.Response response = new In_NewGuildTrainBeAccelerate.Response(heros);
        oriSender.tell(response, ActorRef.noSender());
        DBUtils.saveHashPojo(outRealmId, heros);
    }
}
