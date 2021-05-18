package ws.newBattle.buff;

import ws.newBattle.BattleContext;
import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.skill.NewBattleSkill;
import ws.newBattle.utils.NewBattleAllEnums.NewAttrContainerType;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.relationship.base.HeroAttr;
import ws.relationship.table.tableRows.Table_New_Buff_Row;

/**
 * <pre>
 * 吸取生命
 * 吸取怒气
 * 不支持回合
 * </pre>
 */
public class DrawHpOrAngerBuff extends NewBattleBuffWrap {
    private HeroAttrTypeEnum attrType;

    public DrawHpOrAngerBuff(HeroAttrTypeEnum attrType) {
        this.attrType = attrType;
    }

    public DrawHpOrAngerBuff(BattleContext context, int uniId, int id, Table_New_Buff_Row buffRow, NewBattleHeroWrap belongHero, NewBattleHeroWrap fromHero, NewBattleSkill fromSkill, int whichRoundAdd, int lastReduceRound, int roundOri, int round, long effectRate, long effectFixValue, HeroAttrTypeEnum attrType) {
        super(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue);
        this.attrType = attrType;
    }

    @Override
    public void immediatelyTick() {
        this.tick();
        check();
    }

    @Override
    protected void tick0() {
        HeroAttr heroAttr = new HeroAttr(attrType, this.effectFixValue);
        this.fromHero.addAttr(heroAttr, NewAttrContainerType.CUR);
        this.belongHero.reduceAttr(heroAttr, NewAttrContainerType.CUR);
        this.round = 0;
        System.out.println("Buff Tick：from->" + this.fromHero + "--->" + this + "--->add[" + heroAttr.getAttrType() + "]:" + heroAttr.getAttrValue());
        System.out.println("Buff Tick：belong->" + this.belongHero + "--->" + this + "--->reduce[" + heroAttr.getAttrType() + "]:" + heroAttr.getAttrValue());
        addToBattleProto(heroAttr);
    }


    @Override
    public String toString() {
        return "[吸取XX:" + attrType + "--" + this.id + "--" + this.getUniId() + "--<<<<" + this.belongHero + this.fromHero + ">>>>]";
    }

    @Override
    public NewBattleBuffWrap clone() {
        return new DrawHpOrAngerBuff(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue, attrType);
    }

    private void addToBattleProto(HeroAttr heroAttr) {
        if (attrType == HeroAttrTypeEnum.HP) {
            this.context.getBattleProto().addBuffEffect_Hp(this.getId(), this.fromHero.getId(), this.fromHero, heroAttr.getAttrValue());
            this.context.getBattleProto().addBuffEffect_Hp(this.getId(), this.fromHero.getId(), this.belongHero, -heroAttr.getAttrValue());
        } else if (attrType == HeroAttrTypeEnum.Anger) {
            this.context.getBattleProto().addBuffEffect_Anger(this.getId(), this.fromHero.getId(), this.fromHero, heroAttr.getAttrValue());
            this.context.getBattleProto().addBuffEffect_Anger(this.getId(), this.fromHero.getId(), this.belongHero, -heroAttr.getAttrValue());
        }
    }
}
