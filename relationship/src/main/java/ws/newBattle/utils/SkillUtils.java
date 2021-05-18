package ws.newBattle.utils;

import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.NewBattleSide;
import ws.newBattle.skill.NewBattleSkill;
import ws.protos.EnumsProtos.EffectTypeEnum;
import ws.protos.EnumsProtos.FightTargetConditonEnum;
import ws.protos.EnumsProtos.FightTargetEnum;
import ws.protos.EnumsProtos.HeroAttrPosTypeEnum;
import ws.protos.EnumsProtos.SexEnum;
import ws.protos.EnumsProtos.SkillEffectTypeEnum;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_New_Skill_Row;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangweiwei on 16-12-5.
 */
public class SkillUtils {


    /**
     * 获取技能目标
     *
     * @param self
     * @param skill
     * @return
     */
    public static List<NewBattleHeroWrap> getSkillTargets(NewBattleHeroWrap self, NewBattleSkill skill) {
        List<NewBattleHeroWrap> poses = new ArrayList<>();
        Table_New_Skill_Row skill_row = RootTc.get(Table_New_Skill_Row.class, skill.getSkill_row().getId());
        if (skill_row.getSkillRange() == null) {
            return poses;
        }
        EffectTypeEnum effectType = EffectTypeEnum.BUFF_EFFECT;
        if (skill_row.getSkillEffectType() == SkillEffectTypeEnum.DAMAGE_EFFECT) {
            effectType = EffectTypeEnum.DEBUFF_EFFECT;
        }
        poses.addAll(getBigTargets(effectType, skill_row.getSkillRange(), self));
        if (poses.size() == 0) {
            return poses;
        }
        if (skill_row.getSkillRangeCondition() == null) {
            return poses;
        }
        return getConditonTargets(poses, skill_row.getSkillRangeCondition(), skill_row.getSkillRangeValue());
    }

    /**
     * 获取Buff的作用目标
     *
     * @param effectType
     * @param self
     * @param fightTargetEnum
     * @param conditonEnum
     * @param conditionValue
     * @return
     */
    public static List<NewBattleHeroWrap> getBuffTargets(EffectTypeEnum effectType, NewBattleHeroWrap self, FightTargetEnum fightTargetEnum, FightTargetConditonEnum conditonEnum, int conditionValue) {
        List<NewBattleHeroWrap> poses = new ArrayList<>();
        poses.addAll(getBigTargets(effectType, fightTargetEnum, self));
        if (poses.size() == 0) {
            return poses;
        }
        if (conditonEnum == null) {
            return poses;
        }
        return getConditonTargets(poses, conditonEnum, conditionValue);
    }


    /**
     * 根据大范围，获取目标
     *
     * @param effectType
     * @param targetEnum
     * @param self
     * @return
     */
    public static List<NewBattleHeroWrap> getBigTargets(EffectTypeEnum effectType, FightTargetEnum targetEnum, NewBattleHeroWrap self) {
        List<NewBattleHeroWrap> poses = new ArrayList<>();
        NewBattleSide effectBattleSide = self.getOppositeBattleSide();
        if (effectType == EffectTypeEnum.BUFF_EFFECT) {
            effectBattleSide = self.getBelongBattleSide();
        }
        switch (targetEnum) {
            case SELF:
                poses.add(self);
                return poses;
            case ALL:
                return NewBattleHeroRange.getAllAliveHeros(effectBattleSide);
            case FRONT_SINGLE:
                poses.add(NewBattleHeroRange.getFrontRandomHero(effectBattleSide));
                return poses;
            case BACK_SINGLE:
                poses.add(NewBattleHeroRange.getBackRandomHero(effectBattleSide));
                return poses;
            case ONE_CLOUMN:
                return NewBattleHeroRange.getRandomCloumnHeros(effectBattleSide);
            case FRONT_ROW:
                return NewBattleHeroRange.getFrontRowHeros(effectBattleSide);
            case BACK_ROW:
                return NewBattleHeroRange.getBackRowHeros(effectBattleSide);
            default:
                return poses;
        }
    }

    /**
     * 根据范围条件，获取目标
     *
     * @param heros
     * @param conditonEnum
     * @param conditoValue
     * @return
     */
    public static List<NewBattleHeroWrap> getConditonTargets(List<NewBattleHeroWrap> heros, FightTargetConditonEnum conditonEnum, int conditoValue) {
        List<NewBattleHeroWrap> poses = new ArrayList<>();
        switch (conditonEnum) {
            case ROUND:
                return NewBattleHeroRangeCondition.getTargetRoundHeros(heros.get(0));
            case MALE_F:
                return NewBattleHeroRangeCondition.getSexHeros(heros, SexEnum.MALE);
            case FEMALE_F:
                return NewBattleHeroRangeCondition.getSexHeros(heros, SexEnum.FEMALE);
            case RANDOM_N:
                return NewBattleHeroRangeCondition.random_N_Heros(heros, conditoValue);
            case AFTER:
                poses.add(NewBattleHeroRangeCondition.getTargetAfterHero(heros.get(0)));
                return poses;
            case ROW_IN:
                return NewBattleHeroRangeCondition.getTargetRowHeros(heros.get(0));
            case CLOUMN_IN:
                return NewBattleHeroRangeCondition.getTargetCloumnHeros(heros.get(0));
            case ATTACK_ATTR:
                return NewBattleHeroRangeCondition.getAttrPosTypeHeros(heros, HeroAttrPosTypeEnum.ATTACK_ATTR_POS);
            case DEFEND_ATRR:
                return NewBattleHeroRangeCondition.getAttrPosTypeHeros(heros, HeroAttrPosTypeEnum.DEFEND_ATRR_POS);
            case SKILL_ATTR:
                return NewBattleHeroRangeCondition.getAttrPosTypeHeros(heros, HeroAttrPosTypeEnum.SKILL_ATTR_POS);
            case MIN_HP:
                poses.add(NewBattleHeroRangeCondition.minHpHero(heros));
                return poses;
            case MAX_HP:
                poses.add(NewBattleHeroRangeCondition.maxHpHero(heros));
                return poses;
            case MIN_ANGER:
                poses.add(NewBattleHeroRangeCondition.minAngerHero(heros));
                return poses;
            case MAX_ANGER:
                poses.add(NewBattleHeroRangeCondition.maxAngerHero(heros));
                return poses;
            case MORE_THAN_HP:
                return NewBattleHeroRangeCondition.higherHpHero(heros, conditoValue);
            case LESS_THAN_HP:
                return NewBattleHeroRangeCondition.lowerHpHero(heros, conditoValue);
            default:
                return poses;
        }
    }
}
