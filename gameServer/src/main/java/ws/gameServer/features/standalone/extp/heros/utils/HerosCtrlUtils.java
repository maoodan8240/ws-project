package ws.gameServer.features.standalone.extp.heros.utils;

import ws.common.table.table.interfaces.cell.TupleCell;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.gameServer.features.standalone.extp.itemBag.utils.ItemBagUtils;
import ws.gameServer.features.standalone.extp.utils.UpgradeLevel;
import ws.protos.EnumsProtos.EquipmentPositionEnum;
import ws.protos.EnumsProtos.SkillPositionEnum;
import ws.protos.EnumsProtos.SystemModuleTypeEnum;
import ws.protos.EnumsProtos.WarSoulPositionEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.enums.item.IdItemTypeEnum;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_CardYoke_Row;
import ws.relationship.table.tableRows.Table_Exp_Row;
import ws.relationship.table.tableRows.Table_Item_Row;
import ws.relationship.table.tableRows.Table_New_Card_Row;
import ws.relationship.topLevelPojos.common.LevelUpObj;
import ws.relationship.topLevelPojos.formations.Formation;
import ws.relationship.topLevelPojos.heros.Equipment;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.topLevelPojos.heros.Heros;
import ws.relationship.topLevelPojos.talent.Talent;
import ws.relationship.utils.attrs.HeroAttrsContainer;
import ws.relationship.utils.attrs.HeroAttrsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HerosCtrlUtils {


    /**
     * 获取最基础的HeroTpId,因为武将突破后id会变。
     * 如 1110108的baseId为：1110100
     *
     * @param heroTpId
     * @return
     */
    public static int getHeroBaseId(int heroTpId) {
        return (heroTpId / 100) * 100;
    }

    /**
     * 获取下一个HeroId
     *
     * @param heros
     * @param tpId
     * @return
     */
    public static int getNextHeroId(Heros heros, int tpId) {
        heros.setMaxHeroIdSeq(heros.getMaxHeroIdSeq() + 1);
        return IdItemTypeEnum.genItemId(tpId, heros.getMaxHeroIdSeq());
    }


    /**
     * 获取道具提供的所有经验值
     *
     * @param consumeTpIds
     * @return
     */
    public static long calcuSumItemExp(List<IdAndCount> consumeTpIds, GetItemExp getItemExp) {
        long sum = 0;
        for (IdAndCount idc : consumeTpIds) {
            int id = idc.getId();
            long count = idc.getCount();
            if (!ItemBagUtils.isSpecialItemId(id)) {
                IdItemTypeEnum type = IdItemTypeEnum.parseByItemTemplateId(id);
                if (type == IdItemTypeEnum.ITEM) {
                    Table_Item_Row row = RootTc.get(Table_Item_Row.class, id);
                    sum += getItemExp.getExp(row) * count;
                }
            }
        }
        return sum;
    }


    /**
     * 获取武将
     *
     * @param heros
     * @param heroId
     * @return
     */
    public static Hero getHero(Heros heros, int heroId) {
        return heros.getIdToHero().get(heroId);
    }

    /**
     * 增加武将
     *
     * @param heros
     * @param heroTpId
     * @return
     */
    public static Hero addHero(Heros heros, int heroTpId) {
        int nextHeroId = getNextHeroId(heros, heroTpId);
        Hero hero = new Hero(nextHeroId, heroTpId);
        heros.getIdToHero().put(nextHeroId, hero);
        Table_New_Card_Row cardRow = RootTc.get(Table_New_Card_Row.class, heroTpId);
        hero.setStarLv(cardRow.getCardStar());
        hero.setQualityLv(MagicNumbers.DefaultValue_Hero_Quality_Level);
        unlockSkill(hero);
        initHeroEquipments(hero);
        return hero;
    }


    /**
     * 武将升级
     *
     * @param hero
     * @param expOffered
     * @param curMaxLevel
     */
    public static void upgradeHeroLevel(Hero hero, long expOffered, int curMaxLevel) {
        LevelUpObj levelUpObj = new LevelUpObj(hero.getLv(), hero.getOvfExp());
        UpgradeLevel.levelUp(levelUpObj, expOffered, curMaxLevel, (oldLevel) -> {
            Table_New_Card_Row cardRow = RootTc.get(Table_New_Card_Row.class, hero.getTpId());
            return Table_Exp_Row.getHeroUpgradeNeedExp(cardRow.getCardAptitude(), oldLevel);
        });
        hero.setLv(levelUpObj.getLevel());
        hero.setOvfExp(levelUpObj.getOvfExp());
    }


    /**
     * 战魂升级
     *
     * @param soulPosition
     * @param lvObj
     * @param expOffered
     * @param curMaxLevel
     */
    public static void upgradeHeroWarSoulLevel(WarSoulPositionEnum soulPosition, LevelUpObj lvObj, long expOffered, int curMaxLevel) {
        LevelUpObj levelUpObj = lvObj.clone();
        UpgradeLevel.levelUp(levelUpObj, expOffered, curMaxLevel, (oldLevel) -> {
            return Table_Exp_Row.getWarSoulUpgradeNeedExp(soulPosition, oldLevel);
        });
        lvObj.setLevel(levelUpObj.getLevel());
        lvObj.setOvfExp(levelUpObj.getOvfExp());
    }


    /**
     * 计算武将的总的经验值
     *
     * @param hero
     * @return
     */
    public static long calcuHeroExp(Hero hero) {
        Table_New_Card_Row card_row = RootTc.get(Table_New_Card_Row.class, hero.getTpId());
        return 0;
    }


    /**
     * 初始化武将的装备信息
     *
     * @param hero
     * @return
     */
    public static void initHeroEquipments(Hero hero) {
        hero.getEquips().put(EquipmentPositionEnum.POS_A, new Equipment());
        hero.getEquips().put(EquipmentPositionEnum.POS_B, new Equipment());
        hero.getEquips().put(EquipmentPositionEnum.POS_C, new Equipment());
        hero.getEquips().put(EquipmentPositionEnum.POS_D, new Equipment());
        hero.getEquips().put(EquipmentPositionEnum.POS_E, new Equipment());
        hero.getEquips().put(EquipmentPositionEnum.POS_F, new Equipment());
    }


    /**
     * <pre>
     * 解锁技能：
     * 1星->小技能，必杀技
     * 2星->小技能，必杀技
     * 3星->小技能，必杀技，被动1
     * 4星->小技能，必杀技，被动1，被动2
     * 5星->小技能，必杀技，被动1，被动2，被动3    * 6星->小技能，必杀技，被动1，被动2，被动3，被动4
     * </pre>
     *
     * @param hero
     */
    public static void unlockSkill(Hero hero) {
        int starLv = hero.getStarLv();
        TupleListCell<Integer> tupleListCell = AllServerConfig.Heros_Skill_UnlocSkill_NeedStar.getConfig();
        for (TupleCell<Integer> tupleCell : tupleListCell.getAll()) {
            int skillPos = tupleCell.get(TupleCell.FIRST);
            int needStar = tupleCell.get(TupleCell.SECOND);
            SkillPositionEnum skillPosEnum = SkillPositionEnum.valueOf(skillPos);
            if (skillPosEnum == null) {
                String msg = String.format("配置出错，无此skillPos=%s ！ 配置为：%s ", skillPos, tupleListCell);
                throw new BusinessLogicMismatchConditionException(msg);
            }
            if (needStar <= starLv) {
                _addSkill(hero, skillPosEnum);
            }
        }
    }


    /**
     * 增加武将导致缘分完成的武将
     *
     * @param heroAdd
     * @param idToHero
     * @return
     */
    public static List<Hero> addHeroMakeYokeDoneHeros(Hero heroAdd, Map<Integer, Hero> idToHero) {
        List<Hero> heroes = new ArrayList<>();
        Map<Integer, Integer> heroTpIdToYokeId = Table_CardYoke_Row.getEffectHeroTpIdToYokeId().get(heroAdd.getTpId());
        if (heroTpIdToYokeId != null) {
            for (Entry<Integer, Integer> entry : heroTpIdToYokeId.entrySet()) {
                int yokeId = entry.getValue();
                Table_CardYoke_Row yokeRow = RootTc.get(Table_CardYoke_Row.class, yokeId);
                if (HeroAttrsUtils.contailsAllHeroIpId(idToHero, yokeRow.getMainCondition())) {
                    Hero doneYoke = getHeroByTpId(idToHero, entry.getKey());
                    if (doneYoke != null) {
                        heroes.add(doneYoke);
                    }
                }
            }
        }
        return heroes;
    }


    /**
     * 根据tpid获取武将，因为武将是唯一的，一个tpid只存在一个对应的hero实例
     *
     * @param idToHero
     * @param tpId
     * @return
     */
    public static Hero getHeroByTpId(Map<Integer, Hero> idToHero, int tpId) {
        for (Entry<Integer, Hero> entry : idToHero.entrySet()) {
            if (entry.getValue().getTpId() == tpId) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 解锁战魂
     *
     * @param hero
     */
    public static void unlockSpirit(Hero hero) {
        TupleListCell<Integer> needColor = AllServerConfig.Heros_Spirit_Unlock_Need_Color.getConfig();
        for (TupleCell<Integer> color : needColor.getAll()) {
            if (hero.getQualityLv() >= color.get(TupleCell.FIRST)) {
                _addSpirit(hero, WarSoulPositionEnum.valueOf(color.get(TupleCell.SECOND)));
            }
        }
    }

    private static void _addSkill(Hero hero, SkillPositionEnum skillPos) {
        if (!hero.getSkills().containsKey(skillPos)) {
            hero.getSkills().put(skillPos, MagicNumbers.DefaultValue_Skill_Level);
        }
    }

    private static void _addSpirit(Hero hero, WarSoulPositionEnum soulPosition) {
        if (!hero.getSouls().containsKey(soulPosition)) {
            hero.getSouls().put(soulPosition, new LevelUpObj(MagicNumbers.DefaultValue_Spirit_Level, 0));
        }
    }


    /**
     * 构建所有武将的属性容器，主阵容
     *
     * @param heros
     * @param talent
     * @param formation_Main
     * @return
     */
    public static HeroAttrsContainer createHeroAttrsContainer_FMain_AllHeros(Heros heros, Talent talent, Formation formation_Main) {
        HeroAttrsContainer attrsContainer = new HeroAttrsContainer();
        attrsContainer.initAll(talent, heros, formation_Main);
        attrsContainer.calcuAllHeroAllAttrs_Formation_Main();
        return attrsContainer;
    }


    /**
     * 构建阵容上面的武将的属性容器
     *
     * @param heros
     * @param talent
     * @param formation
     * @return
     */
    public static HeroAttrsContainer createHeroAttrsContainer_F_OnlyInF(Heros heros, Talent talent, Formation formation) {
        HeroAttrsContainer attrsContainer = new HeroAttrsContainer();
        attrsContainer.initFormation(talent, heros, formation);
        attrsContainer.calcuAllHeroAllAttrs(SystemModuleTypeEnum.MODULE_ALL, formation);
        return attrsContainer;
    }

    public interface GetItemExp {
        int getExp(Table_Item_Row row);
    }
}
