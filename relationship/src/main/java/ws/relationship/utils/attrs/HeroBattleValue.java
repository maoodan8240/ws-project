package ws.relationship.utils.attrs;

import ws.common.table.table.implement.cell._TupleCell;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.protos.EnumsProtos.SkillPositionEnum;
import ws.relationship.base.HeroAttrs;
import ws.relationship.base.MagicNumbers;
import ws.relationship.topLevelPojos.heros.Hero;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lee on 17-4-27.
 */
public class HeroBattleValue {

    private static final Map<HeroAttrTypeEnum, TupleCell<Integer>> ALL = new HashMap<HeroAttrTypeEnum, TupleCell<Integer>>() {
        private static final long serialVersionUID = 2725507300155502429L;

        {
            put(HeroAttrTypeEnum.Attack, new _TupleCell<>(new Integer[]{2, 1})); // 2点攻击提供1点
            put(HeroAttrTypeEnum.Defense, new _TupleCell<>(new Integer[]{1, 1}));
            put(HeroAttrTypeEnum.HP, new _TupleCell<>(new Integer[]{10, 1}));
            put(HeroAttrTypeEnum.Damage, new _TupleCell<>(new Integer[]{1, 2}));
            put(HeroAttrTypeEnum.DamageOppose, new _TupleCell<>(new Integer[]{1, 2}));
            put(HeroAttrTypeEnum.Crit, new _TupleCell<>(new Integer[]{1, 1}));
            put(HeroAttrTypeEnum.CritOppose, new _TupleCell<>(new Integer[]{1, 1}));
            put(HeroAttrTypeEnum.Block, new _TupleCell<>(new Integer[]{1, 1}));
            put(HeroAttrTypeEnum.BlockBreak, new _TupleCell<>(new Integer[]{1, 1}));
            put(HeroAttrTypeEnum.CritDamage, new _TupleCell<>(new Integer[]{2, 1}));
            put(HeroAttrTypeEnum.BlockDamage, new _TupleCell<>(new Integer[]{2, 1}));
            put(HeroAttrTypeEnum.Ctrl, new _TupleCell<>(new Integer[]{2, 1}));
            put(HeroAttrTypeEnum.FreeCtrl, new _TupleCell<>(new Integer[]{2, 1}));
            put(HeroAttrTypeEnum.AngerSkillDamage, new _TupleCell<>(new Integer[]{1, 2}));
            put(HeroAttrTypeEnum.AngerSkillDamageOppose, new _TupleCell<>(new Integer[]{1, 2}));
        }
    };


    private static long getbattleValuePoint(HeroAttrTypeEnum attrType, HeroAttrs heroAttrs) {
        if (!ALL.containsKey(attrType)) {
            return 0;
        }
        long value = heroAttrs.get(attrType);
        TupleCell<Integer> ratio = ALL.get(attrType);
        return (value * ratio.get(TupleCell.SECOND)) / ratio.get(TupleCell.FIRST);
    }


    /**
     * 计算武将的战斗力
     *
     * @param hero
     * @param heroAttrs
     * @return
     */
    public static long calcuAttrBattleValue(Hero hero, HeroAttrs heroAttrs) {
        return calcuAttrBattleValue(heroAttrs) + calcuSkillBattleValue(hero);
    }

    /**
     * <pre>
     *     * 计算属性提供的战斗力
     *     （攻击/2+防御+生命/10）*（1+伤害*2+免伤*2+暴击+抗暴+格挡+破击+暴击强度加成/2+格挡强度加成/2+控制/2+免控/2+必杀伤害加成*2+必杀抗性加成*2）
     * </pre>
     *
     * @param heroAttrs
     * @return
     */
    public static long calcuAttrBattleValue(HeroAttrs heroAttrs) {
        return ((getbattleValuePoint(HeroAttrTypeEnum.Attack, heroAttrs) +
                getbattleValuePoint(HeroAttrTypeEnum.Defense, heroAttrs) +
                getbattleValuePoint(HeroAttrTypeEnum.HP, heroAttrs)) *

                (MagicNumbers.RANDOM_BASE_VALUE +
                        getbattleValuePoint(HeroAttrTypeEnum.Damage, heroAttrs) +
                        getbattleValuePoint(HeroAttrTypeEnum.DamageOppose, heroAttrs) +
                        getbattleValuePoint(HeroAttrTypeEnum.Crit, heroAttrs) +
                        getbattleValuePoint(HeroAttrTypeEnum.CritOppose, heroAttrs) +
                        getbattleValuePoint(HeroAttrTypeEnum.Block, heroAttrs) +
                        getbattleValuePoint(HeroAttrTypeEnum.BlockBreak, heroAttrs) +
                        getbattleValuePoint(HeroAttrTypeEnum.CritDamage, heroAttrs) +
                        getbattleValuePoint(HeroAttrTypeEnum.BlockDamage, heroAttrs) +
                        getbattleValuePoint(HeroAttrTypeEnum.Ctrl, heroAttrs) +
                        getbattleValuePoint(HeroAttrTypeEnum.FreeCtrl, heroAttrs) +
                        getbattleValuePoint(HeroAttrTypeEnum.AngerSkillDamage, heroAttrs) +
                        getbattleValuePoint(HeroAttrTypeEnum.AngerSkillDamageOppose, heroAttrs)
                ))

                / MagicNumbers.RANDOM_BASE_VALUE;
    }


    /**
     * <pre>
     *     计算技能提供的战斗力
     *     技能1等级+技能2等级+技能3等级+技能4等级）*8+（技能5等级+技能6等级）*12
     * </pre>
     *
     * @param hero
     * @return
     */
    public static long calcuSkillBattleValue(Hero hero) {
        return (getSkillLv(hero, SkillPositionEnum.Skill_POS_1) +
                getSkillLv(hero, SkillPositionEnum.Skill_POS_2) +
                getSkillLv(hero, SkillPositionEnum.Skill_POS_3) +
                getSkillLv(hero, SkillPositionEnum.Skill_POS_4)) * 8
                +
                (getSkillLv(hero, SkillPositionEnum.Skill_POS_5) +
                        getSkillLv(hero, SkillPositionEnum.Skill_POS_6)
                ) * 12;
    }

    /**
     * 获取技能等级
     *
     * @param hero
     * @param skillPos
     * @return
     */
    private static int getSkillLv(Hero hero, SkillPositionEnum skillPos) {
        if (hero.getSkills().containsKey(skillPos)) {
            return hero.getSkills().get(skillPos);
        }
        return 0;
    }
}
