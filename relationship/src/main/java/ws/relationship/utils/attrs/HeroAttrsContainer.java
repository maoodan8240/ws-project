package ws.relationship.utils.attrs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.protos.EnumsProtos.HeroPositionEnum;
import ws.protos.EnumsProtos.SystemModuleTypeEnum;
import ws.relationship.base.HeroAttrs;
import ws.relationship.topLevelPojos.formations.Formation;
import ws.relationship.topLevelPojos.formations.FormationPos;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.topLevelPojos.heros.Heros;
import ws.relationship.topLevelPojos.talent.Talent;
import ws.relationship.utils.RelationshipCommonUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by lee on 17-4-27.
 */
public class HeroAttrsContainer {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeroAttrsContainer.class);
    private Map<Integer, HeroAttrs> heroIdToHeroAttrs = new HashMap<>();           // 武将属性缓存
    private Map<Integer, Long> heroIdToBattleValue = new LinkedHashMap<>();        // 武将战斗力缓存
    private Map<Integer, OneHeroAttrs> heroIdToHeroDetailAttrs = new HashMap<>();  // 武将的细节属性
    private TalentAttrs talentAttrs;
    private Formation formation_Main;
    private Heros belongTarget;

    public void initAll(Talent talent, Heros belongTarget, Formation formation_Main) {
        clearAll();
        this.formation_Main = formation_Main;
        this.belongTarget = belongTarget;
        talentAttrs = new TalentAttrs(talent);  // 天赋增加的属性
        talentAttrs.calcuTalentAttrs();
        belongTarget.getIdToHero().forEach((id, hero) -> {
            _addNewHero(hero);
        });
    }


    public void initFormation(Talent talent, Heros belongTarget, Formation formation) {
        clearAll();
        this.formation_Main = formation;
        this.belongTarget = belongTarget;
        talentAttrs = new TalentAttrs(talent);  // 天赋增加的属性
        talentAttrs.calcuTalentAttrs();

        formation.getPosToFormationPos().forEach((pos, posObj) -> {
            if (posObj.getHeroId() > 0 && belongTarget.getIdToHero().containsKey(posObj.getHeroId())) {
                _addNewHero(belongTarget.getIdToHero().get(posObj.getHeroId()));
            }
        });
    }


    public void initSome(Talent talent, Heros belongTarget, List<Integer> heroIds) {
        clearAll();
        this.formation_Main = null;
        this.belongTarget = belongTarget;
        talentAttrs = new TalentAttrs(talent);  // 天赋增加的属性
        talentAttrs.calcuTalentAttrs();

        heroIds.forEach(heroId -> {
            if (belongTarget.getIdToHero().containsKey(heroId)) {
                _addNewHero(belongTarget.getIdToHero().get(heroId));
            } else {
                LOGGER.error("计算属性 初始化时: heroId={}, 不在belongTarget集合中! belongTarget={} ", heroId, belongTarget);
            }
        });
    }


    /**
     * 获取阵容武将的属性
     *
     * @param formation
     * @return
     */
    public Map<Integer, HeroAttrs> getHeroAttrs(Formation formation) {
        Map<Integer, HeroAttrs> attrsMap = new HashMap<>();
        formation.getPosToFormationPos().forEach((pos, posObj) -> {
            int heroId = posObj.getHeroId();
            if (heroId > 0 && belongTarget.getIdToHero().containsKey(heroId) && heroIdToHeroAttrs.containsKey(heroId)) {
                attrsMap.put(heroId, new HeroAttrs(heroIdToHeroAttrs.get(heroId)));
            }
        });
        return attrsMap;
    }

    /**
     * 获取单个武将的属性
     *
     * @param heroId
     * @return
     */
    public HeroAttrs getHeroAttrs(int heroId) {
        if (belongTarget.getIdToHero().containsKey(heroId) && heroIdToHeroAttrs.containsKey(heroId)) {
            return heroIdToHeroAttrs.get(heroId);
        }
        return null;
    }

    /**
     * 获取单个武将的战斗力
     *
     * @param heroId
     * @return
     */
    public long getHeroBattleValue(int heroId) {
        if (belongTarget.getIdToHero().containsKey(heroId) && heroIdToBattleValue.containsKey(heroId)) {
            return heroIdToBattleValue.get(heroId);
        }
        return 0;
    }

    // ======================================== 变化事件 start ===========================================

    /**
     * 天赋点发生变化
     */
    public void onTalentChange() {
        talentAttrs.calcuTalentAttrs();
        calcuAllHeroAllAttrs_Formation_Main();
    }

    /**
     * 新增了武将
     *
     * @param hero
     */
    public void onAddNewHero(Hero hero) {
        _addNewHero(hero);
        OneHeroAttrs oneHeroAttrs = getOneHeroAttrs(hero);
        _calcuOneHeroAllAttrs_Formation_Main(oneHeroAttrs);
    }

    /**
     * 武将基本信息发生变化
     *
     * @param hero
     */
    public void onOneHeroBaseChange(Hero hero) {
        OneHeroAttrs oneHeroAttrs = getOneHeroAttrs(hero);
        oneHeroAttrs.calcuBaseAttr();
        _calcuOneHeroAllAttrs_Formation_Main(oneHeroAttrs);
    }

    /**
     * 武将装备发生变化
     *
     * @param hero
     */
    public void onOneHeroEquipChange(Hero hero) {
        OneHeroAttrs oneHeroAttrs = getOneHeroAttrs(hero);
        oneHeroAttrs.calcuEquipAttr();
        _calcuOneHeroAllAttrs_Formation_Main(oneHeroAttrs);
    }

    /**
     * 武将技能发生变化
     *
     * @param hero
     */
    public void onOneHeroSkillChange(Hero hero) {
        OneHeroAttrs oneHeroAttrs = getOneHeroAttrs(hero);
        oneHeroAttrs.calcuSkillAttr();
        _calcuOneHeroAllAttrs_Formation_Main(oneHeroAttrs);
    }

    /**
     * 武将缘分发生变化
     *
     * @param hero
     */
    public void onOneHeroYokeChange(Hero hero) {
        OneHeroAttrs oneHeroAttrs = getOneHeroAttrs(hero);
        oneHeroAttrs.calcuYokeAttr();
        _calcuOneHeroAllAttrs_Formation_Main(oneHeroAttrs);
    }

    /**
     * 武将缘分发生变化
     *
     * @param heroes
     */
    public void onSomeHeroYokeChange(List<Hero> heroes) {
        for (Hero hero : heroes) {
            onOneHeroYokeChange(hero);
        }
    }


    /**
     * 武将战魂发生变化
     *
     * @param hero
     */
    public void onOneHeroWarSoulChange(Hero hero) {
        OneHeroAttrs oneHeroAttrs = getOneHeroAttrs(hero);
        oneHeroAttrs.calcuWarSoulAttr();
        _calcuOneHeroAllAttrs_Formation_Main(oneHeroAttrs);
    }


    // ======================================== 变化事件 end  ===========================================

    /**
     * 计算所有武将（可能是所有武将，也可能是阵容上的所有武将）的属性及战斗力， 应用于主阵容
     */
    public void calcuAllHeroAllAttrs_Formation_Main() {
        _calcuAllHeroAllAttrs(SystemModuleTypeEnum.MODULE_ALL, formation_Main);
        sortHeroIdToBattleValue();
    }


    /**
     * 计算所有武将（可能是所有武将，也可能是阵容上的所有武将）的属性及战斗力， 应用于指定的阵容
     *
     * @param moduleType
     * @param formation
     */
    public void calcuAllHeroAllAttrs(SystemModuleTypeEnum moduleType, Formation formation) {
        _calcuAllHeroAllAttrs(moduleType, formation);
        sortHeroIdToBattleValue();
    }

    public void calcuSomeHeroAllAttrs(List<Integer> heroIds, SystemModuleTypeEnum moduleType, Formation formation) {
        for (int heroId : heroIds) {
            if (!belongTarget.getIdToHero().containsKey(heroId)) {
                LOGGER.error("计算属性的heroId={}, 不在belongTarget集合中! belongTarget={} ", heroId, belongTarget);
                continue;
            }
            OneHeroAttrs oneHeroAttrs = getOneHeroAttrs(heroId);
            _calcuOneHeroAllAttrs(oneHeroAttrs, moduleType, formation);
        }
        sortHeroIdToBattleValue();
    }


    private void _calcuOneHeroAllAttrs_Formation_Main(OneHeroAttrs oneHeroAttrs) {
        _calcuOneHeroAllAttrs(oneHeroAttrs, SystemModuleTypeEnum.MODULE_ALL, formation_Main);
        sortHeroIdToBattleValue();
    }


    private void _calcuAllHeroAllAttrs(SystemModuleTypeEnum moduleType, Formation formation) {
        for (Entry<Integer, OneHeroAttrs> entry : heroIdToHeroDetailAttrs.entrySet()) {
            _calcuOneHeroAllAttrs(entry.getValue(), moduleType, formation);
        }
    }

    private void _calcuOneHeroAllAttrs(OneHeroAttrs oneHeroAttrs, SystemModuleTypeEnum moduleType, Formation formation) {
        Hero hero = oneHeroAttrs.getBelongHero();
        int heroId = hero.getId();
        HeroAttrs heroAttrs_Fix = new HeroAttrs();
        HeroAttrs heroAttrs_Precent = new HeroAttrs();
        oneHeroAttrs.mergeAllFixAttrs(heroAttrs_Fix, moduleType);
        oneHeroAttrs.mergeAllPrecentAttrs(heroAttrs_Precent, moduleType);
        talentAttrs.mergeAllFixAttrs(heroAttrs_Fix, moduleType, hero, formation);
        talentAttrs.mergeAllPrecentAttrs(heroAttrs_Precent, moduleType, hero, formation);
        HeroAttrsUtils.calcuHeroAttrPlus(heroAttrs_Fix, heroAttrs_Precent);
        heroIdToHeroAttrs.put(heroId, new HeroAttrs(heroAttrs_Fix).addAll(heroAttrs_Precent));
        heroIdToBattleValue.put(heroId, HeroBattleValue.calcuAttrBattleValue(hero, heroIdToHeroAttrs.get(heroId)));
    }


    private void sortHeroIdToBattleValue() {
        Map<Integer, Long> heroIdToBattleValueNew = RelationshipCommonUtils.sortMapByValueESC(heroIdToBattleValue);
        heroIdToBattleValue.clear();
        heroIdToBattleValue.putAll(heroIdToBattleValueNew);
        heroIdToBattleValueNew.clear();
    }

    public OneHeroAttrs getOneHeroAttrs(Hero belongHero) {
        if (!this.heroIdToHeroDetailAttrs.containsKey(belongHero.getId())) {
            this.heroIdToHeroDetailAttrs.put(belongHero.getId(), new OneHeroAttrs(belongHero, belongTarget));
        }
        return this.heroIdToHeroDetailAttrs.get(belongHero.getId());
    }

    public OneHeroAttrs getOneHeroAttrs(int heroId) {
        Hero hero = belongTarget.getIdToHero().get(heroId);
        if (!this.heroIdToHeroDetailAttrs.containsKey(heroId)) {
            this.heroIdToHeroDetailAttrs.put(heroId, new OneHeroAttrs(hero, belongTarget));
        }
        return this.heroIdToHeroDetailAttrs.get(heroId);
    }

    private void _addNewHero(Hero hero) {
        OneHeroAttrs oneHeroAttrs = getOneHeroAttrs(hero);
        oneHeroAttrs.calcuAll();
    }


    // ======================================== 其他方法 start ===========================================

    public long getFormationSumBattleValue_Formation_Main() {
        long sum = 0;
        for (Entry<HeroPositionEnum, FormationPos> entry : formation_Main.getPosToFormationPos().entrySet()) {
            FormationPos posObj = entry.getValue();
            if (posObj.getHeroId() > 0 && heroIdToBattleValue.containsKey(posObj.getHeroId())) {
                sum += heroIdToBattleValue.get(posObj.getHeroId());
            }
        }
        return sum;
    }

    public long getFormationSumBattleValue(Formation formation) {
        long sum = 0;
        for (Entry<HeroPositionEnum, FormationPos> entry : formation.getPosToFormationPos().entrySet()) {
            FormationPos posObj = entry.getValue();
            if (posObj.getHeroId() > 0 && heroIdToBattleValue.containsKey(posObj.getHeroId())) {
                sum += heroIdToBattleValue.get(posObj.getHeroId());
            }
        }
        return sum;
    }

    // ======================================== 其他方法 end   ===========================================

    /**
     * 清空属性数据
     */
    private void clearAll() {
        heroIdToHeroAttrs.clear();
        heroIdToBattleValue.clear();
        heroIdToHeroDetailAttrs.clear();
        talentAttrs = null;
        formation_Main = null;
        belongTarget = null;
    }
}
