package ws.newBattle.utils;

import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.skill.NewBattleSkill;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.relationship.base.MagicNumbers;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_New_Skill_Row;
import ws.relationship.utils.RandomUtils;

/**
 * Created by zhangweiwei on 16-8-23.
 */
public class NewBattleCalcu {
    public static final int RATIO_BASE = MagicNumbers.RANDOM_BASE_VALUE;
    private static final int BLOCK_RADIO = 7000; // 格档系数
    private static final int CRIT_RADIO = 15000; // 暴击系数
    private static final int LOWEST_VALUE_MIN = 10;
    private static final int LOWEST_VALUE_MAX = 20;

    /**
     * 计算伤害
     *
     * @param attacker
     * @param defender
     * @return
     */
    public static BattleCalcuAttackResult calcuDamage(NewBattleHeroWrap attacker, NewBattleHeroWrap defender, NewBattleSkill skill) {
        BattleCalcuAttackResult resutl = new BattleCalcuAttackResult();
        Table_New_Skill_Row skill_row = RootTc.get(Table_New_Skill_Row.class, skill.getSkill_row().getId());
        long skillRatio = skill_row.getSkillRate() + (skill.getLevel() - 1) * skill_row.getRateStep();
        int skillFixValue = skill_row.getSkillValue() + (skill.getLevel() - 1) * skill_row.getValueStep();

        boolean critical = isCriticalHit(attacker, defender); // 是否暴击
        boolean beBlocked = isBeBlocked(attacker, defender);  // 是否格档
        long criticalRatio = RATIO_BASE;
        long blockedRatio = RATIO_BASE;
        if (critical) {
            resutl.crt = true;
            criticalRatio = criticalRatio(attacker); // 暴击系数
        }
        if (beBlocked) {
            resutl.block = true;
            blockedRatio = blockRatio(defender); // 格档系数
        }
        long part1 = (attacker.curHeroAttr(HeroAttrTypeEnum.Attack) - defender.curHeroAttr(HeroAttrTypeEnum.Defense)) * skillRatio; // 需要除以 RATIO_BASE
        long part2 = skillFixValue;
        long part3 = (RATIO_BASE + attacker.curHeroAttr(HeroAttrTypeEnum.Damage) - defender.curHeroAttr(HeroAttrTypeEnum.DamageOppose));
        long part4 = criticalRatio; // 例如11000,最小为10000
        long part5 = blockedRatio; // 例如7000

        System.out.println("攻击防御  :" + part1);
        System.out.println("技能固定值:" + part2);
        System.out.println("伤害减免  :" + part3);
        System.out.println("暴击     :" + part4);
        System.out.println("格档     :" + part5);
        System.out.println("技能     :" + skillRatio);
        long sumDamage;
        if (part1 <= 0 || part3 <= 0 || blockedRatio <= 0) {
            sumDamage = 1; // 强制为1点伤害
        } else {
            double rs1 = ((double) part1) / RATIO_BASE + part2; // [（我方攻击-敌方防御）*技能伤害系数+固定伤害]
            double rs2 = ((double) part3 * (double) part4 * (double) part5) / ((double) RATIO_BASE * (double) RATIO_BASE * (double) RATIO_BASE);
            sumDamage = (long) (rs1 * rs2);
        }

        if (sumDamage < LOWEST_VALUE_MIN) {
            sumDamage = randomLowestValue(); // 强制为最低伤害
        }
        System.out.println("最终伤害为:" + sumDamage);
        resutl.hp = sumDamage;
        return resutl;
    }

    /**
     * 计算治疗值
     *
     * @param curer   治疗者
     * @param beCurer 被治疗者
     * @return
     */
    public static long calcuCure(NewBattleHeroWrap curer, NewBattleHeroWrap beCurer, NewBattleSkill skill) {
        Table_New_Skill_Row skill_row = RootTc.get(Table_New_Skill_Row.class, skill.getSkill_row().getId());
        long skillRatio = skill_row.getSkillRate() + (skill.getLevel() - 1) * skill_row.getRateStep();
        int skillFixValue = skill_row.getSkillValue() + (skill.getLevel() - 1) * skill_row.getValueStep();
        long part1 = curer.curHeroAttr(HeroAttrTypeEnum.Attack) * skillRatio; // 需要除以 RATIO_BASE
        long part2 = skillFixValue;
        long beCureRatio = beCurer.curHeroAttr(HeroAttrTypeEnum.Cure);
        long value = (((part1 / RATIO_BASE) + part2) * beCureRatio) / RATIO_BASE;
        return value < LOWEST_VALUE_MIN ? randomLowestValue() : value; // 强制为最低治疗量
    }

    /**
     * 是否暴击
     *
     * @param attacker
     * @param defender
     * @return
     */
    public static boolean isCriticalHit(NewBattleHeroWrap attacker, NewBattleHeroWrap defender) {
        long value = attacker.curHeroAttr(HeroAttrTypeEnum.Crit) - defender.curHeroAttr(HeroAttrTypeEnum.CritOppose);
        return RandomUtils.isDropPartsFractionOfBase(value, RATIO_BASE);
    }

    /**
     * 是否被格档
     *
     * @param attacker
     * @param defender
     * @return
     */
    public static boolean isBeBlocked(NewBattleHeroWrap attacker, NewBattleHeroWrap defender) {
        long value = defender.curHeroAttr(HeroAttrTypeEnum.Block) - attacker.curHeroAttr(HeroAttrTypeEnum.BlockBreak);
        return RandomUtils.isDropPartsFractionOfBase(value, RATIO_BASE);
    }


    /**
     * 暴击系数(万分比表示)
     *
     * @param attacker
     * @return
     */
    public static long criticalRatio(NewBattleHeroWrap attacker) {
        long critDamage = attacker.curHeroAttr(HeroAttrTypeEnum.CritDamage);
        return RATIO_BASE + critDamage + CRIT_RADIO;
    }

    /**
     * 格档系数(万分比表示)
     *
     * @param defender
     * @return
     */
    public static long blockRatio(NewBattleHeroWrap defender) {
        long blockDamage = defender.curHeroAttr(HeroAttrTypeEnum.BlockDamage);
        return BLOCK_RADIO - blockDamage;
    }

    /**
     * 随机一个最低伤害或者治疗值
     *
     * @return
     */
    private static long randomLowestValue() {
        return RandomUtils.dropBetweenTowNum(LOWEST_VALUE_MIN, LOWEST_VALUE_MAX);
    }


    public static class BattleCalcuAttackResult {
        protected long hp; //血量变化    如果血量减少，负数
        protected boolean crt; //是否暴击
        protected boolean block; //是否格挡

        public long getHp() {
            return hp;
        }

        public boolean isCrt() {
            return crt;
        }

        public boolean isBlock() {
            return block;
        }
    }
}


