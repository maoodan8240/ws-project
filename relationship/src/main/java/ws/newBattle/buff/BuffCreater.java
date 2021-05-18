package ws.newBattle.buff;

import ws.newBattle.BattleContext;
import ws.newBattle.utils.NewBattleAllEnums.AngerResurgenceType;
import ws.newBattle.utils.NewBattleAllEnums.HpJudgeType;
import ws.protos.EnumsProtos.EffectTypeEnum;
import ws.protos.EnumsProtos.HeroAttrPosTypeEnum;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.protos.EnumsProtos.SpecialBuffEnum;

/**
 * Created by zhangweiwei on 16-12-2.
 */
public class BuffCreater {

    public static NewBattleBuffWrap chooseBuff(BattleContext context, int buffId, EffectTypeEnum effectTypeEnum, HeroAttrTypeEnum attrType) {
        NewBattleBuffWrap buffWrap = chooseBuff0(buffId, effectTypeEnum, attrType);
        buffWrap.setUniId(context.getNextBuffUniId());
        buffWrap.id = buffId;
        return buffWrap;
    }

    private static NewBattleBuffWrap chooseBuff0(int buffId, EffectTypeEnum effectTypeEnum, HeroAttrTypeEnum attrType) {
        SpecialBuffEnum specialBuff = SpecialBuffEnum.valueOf(buffId);
        if (specialBuff == null) {
            return new HeroAttrChangeBuff(effectTypeEnum, attrType);
        }
        switch (specialBuff) {
            case DIZZY:
            case PARALYSE:
            case FREEZE:
                return new CannotActionBuff();

            case SILENCE:
            case SEAL:
                return new CannotUseBigSkillBuff();

            case BURN:
                return new BurnBuff();

            case CURE:
                return new CureBuff();

            case RESURGENCE50:
                return new WaitResurgenceAngerBuff(AngerResurgenceType.TO50);
            case RESURGENCE100:
                return new WaitResurgenceAngerBuff(AngerResurgenceType.TO100);

            case REDUCE_CUR_PER_HP:
                return new ChangeCurrentAttrPercentBuff(EffectTypeEnum.DEBUFF_EFFECT, HeroAttrTypeEnum.HP);
            case ADD_CUR_PER_ATTACK:
                return new ChangeCurrentAttrPercentBuff(EffectTypeEnum.BUFF_EFFECT, HeroAttrTypeEnum.Attack);
            case REDUCE_CUR_PER_ATTACK:
                return new ChangeCurrentAttrPercentBuff(EffectTypeEnum.DEBUFF_EFFECT, HeroAttrTypeEnum.Attack);
            case ADD_CUR_PER_DEFENSE:
                return new ChangeCurrentAttrPercentBuff(EffectTypeEnum.BUFF_EFFECT, HeroAttrTypeEnum.Defense);
            case REDUCE_CUR_PER_DEFENSE:
                return new ChangeCurrentAttrPercentBuff(EffectTypeEnum.DEBUFF_EFFECT, HeroAttrTypeEnum.Defense);

            case DRAW_ATTACK:
                return new DrawAttrPercentBuff(HeroAttrTypeEnum.Attack);
            case DRAW_DEFENSE:
                return new DrawAttrPercentBuff(HeroAttrTypeEnum.Defense);
            case DRAW_DAMAGE:
                return new DrawAttrPercentBuff(HeroAttrTypeEnum.Damage);
            case DRAW_DAMAGEOPPOSE:
                return new DrawAttrPercentBuff(HeroAttrTypeEnum.DamageOppose);
            case DRAW_ANGER:
                return new DrawHpOrAngerBuff(HeroAttrTypeEnum.Anger);

            case ADD_ATTACK_ATTR_DAMAGE:
                return new HeroAttrsPosTypeBuff(HeroAttrPosTypeEnum.ATTACK_ATTR_POS);
            case ADD_DEFENSE_ATTR_DAMAGE:
                return new HeroAttrsPosTypeBuff(HeroAttrPosTypeEnum.DEFEND_ATRR_POS);
            case ADD_SKILL_ATTR_DAMAGE:
                return new HeroAttrsPosTypeBuff(HeroAttrPosTypeEnum.SKILL_ATTR_POS);

            case BEHEADED:
                return new TargetHPJudgeBuff(HpJudgeType.BEHEAD);
            case FATAL:
                return new TargetHPJudgeBuff(HpJudgeType.FATAL);
            case UNYIELDING:
                return new TargetHPJudgeBuff(HpJudgeType.UNYIELDING);

            default:
                return new HeroAttrChangeBuff(effectTypeEnum, attrType);
        }
    }

}
