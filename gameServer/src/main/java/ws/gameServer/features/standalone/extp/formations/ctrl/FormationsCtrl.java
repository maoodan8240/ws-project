package ws.gameServer.features.standalone.extp.formations.ctrl;


import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.protos.EnumsProtos.FormationTypeEnum;
import ws.protos.EnumsProtos.HeroPositionEnum;
import ws.protos.FormationsProtos.Sm_Formations_OneFormation;
import ws.relationship.topLevelPojos.formations.Formation;
import ws.relationship.topLevelPojos.formations.Formations;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayerMfHero;

import java.util.Map;

public interface FormationsCtrl extends PlayerExteControler<Formations> {

    //================================Cm_Formation Start===============================


    /**
     * 获取某个阵容
     *
     * @param type
     * @return
     */
    Formation getOneFormation(FormationTypeEnum type);

    /**
     * 部署阵容
     *
     * @param smFormation
     */
    void deployment(Sm_Formations_OneFormation smFormation);

    //================================Cm_Formation End===============================

    /**
     * 是否包含某个阵容
     *
     * @param type
     * @return
     */
    boolean containsFormation(FormationTypeEnum type);


    /**
     * 是否在阵容上
     *
     * @param heroId
     * @return
     */
    boolean isIdInFormation(FormationTypeEnum type, int heroId);


    /**
     * 获取阵容上的武将信息
     *
     * @param formationType
     * @return
     */
    Map<HeroPositionEnum, SimplePlayerMfHero> getFormationMfHeros(FormationTypeEnum formationType);

}
