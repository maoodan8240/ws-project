package ws.gameServer.features.standalone.extp.heros.ctrl;

import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.newBattle.NewBattleHeroContainer;
import ws.protos.EnumsProtos.ColorDetailTypeEnum;
import ws.protos.EnumsProtos.FormationTypeEnum;
import ws.protos.EnumsProtos.WarSoulPositionEnum;
import ws.protos.HerosProtos.Sm_Heros_SkillUpgradeInfo;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.topLevelPojos.heros.Heros;
import ws.relationship.utils.attrs.HeroAttrsContainer;

import java.util.List;

public interface HerosCtrl extends PlayerExteControler<Heros> {

    // ====================================================Cm_Heros Action Start======================================

    /**
     * 武将升级
     *
     * @param heroId
     * @param consumeTpIds
     */
    void upgradeLevel(int heroId, List<IdAndCount> consumeTpIds);

    /**
     * 给武将添加经验值
     *
     * @param heroId
     * @param expOffered
     */
    void addExpToHero(int heroId, long expOffered);

    /**
     * 升品
     *
     * @param heroId
     */
    void upgradeQualityLevel(int heroId);

    /**
     * 使用万能碎片
     *
     * @param heroId
     * @param num
     */
    void useUniversalFragments(int heroId, int num);

    /**
     * 升星
     *
     * @param heroId
     */
    void upgradeStarLevel(int heroId);


    /**
     * 购买技能点
     */
    void buySkillPoint();

    /**
     * 升级技能
     * sh
     *
     * @param heroId
     * @param skillUpgradeInfos
     */
    void upgradeSkillLevel(int heroId, List<Sm_Heros_SkillUpgradeInfo> skillUpgradeInfos);


    /**
     * 升级战魂
     *
     * @param heroId
     * @param soulPosition
     * @param consumeTpIds
     */
    void upgradeWarSoulLevel(int heroId, WarSoulPositionEnum soulPosition, List<IdAndCount> consumeTpIds);


    /**
     * 钻石升级战魂
     *
     * @param heroId
     * @param soulPos
     */
    void vipMoneyUpgradeFightingSpiritLevel(int heroId, WarSoulPositionEnum soulPos);


    /**
     * 所有武将中最高的品级
     *
     * @return
     */
    ColorDetailTypeEnum maxColorOfAllHeros();

    // ====================================================Cm_Heros Action End -======================================

    /**
     * 是否可以添加武将，heroId
     * param idMaptoCount
     * return
     */
    boolean canAddHeros(IdMaptoCount idMaptoCount);

    /**
     * 添加武将，heroId
     * param idMaptoCount
     */
    IdMaptoCount addHeros(IdMaptoCount idMaptoCount);

    /**
     * 是否可以删除武将，heroId
     * param idMaptoCount
     * return
     */
    boolean canRemoveHeros(IdMaptoCount idMaptoCount);

    /**
     * 删除武将，heroId
     * param idMaptoCount
     */
    IdMaptoCount removeHeros(IdMaptoCount idMaptoCount);

    /**
     * 是否包含武将
     * param heroId 武将唯一Id
     * return
     */
    boolean containsHeroById(int heroId);

    /**
     * 是否包含武将
     *
     * @param heroTpId 武将模版Id
     * @return
     */
    boolean containsHeroByTpId(int heroTpId);

    /**
     * 获取武将
     * <p>
     * param heroId
     * return
     */
    Hero getHero(int heroId);


    /**
     * 获取武将
     *
     * @param heroTpId
     * @return
     */
    Hero getHeroByTpId(int heroTpId);


    /**
     * 武将属性容器
     *
     * @return
     */
    HeroAttrsContainer getAttrsContainer();

    /**
     * 整点通知
     *
     * @param hour
     */
    void onBroadcastEachHour(int hour);

    /**
     * 获取武将阵容
     *
     * @param type
     */
    NewBattleHeroContainer onQueryBattleHeroContainerInFormation(FormationTypeEnum type);
}
