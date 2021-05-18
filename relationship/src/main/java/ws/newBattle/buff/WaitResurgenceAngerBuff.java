package ws.newBattle.buff;

import ws.newBattle.BattleContext;
import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.skill.NewBattleSkill;
import ws.newBattle.utils.NewBattleAllEnums.AngerResurgenceType;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.relationship.base.MagicNumbers;
import ws.relationship.table.tableRows.Table_New_Buff_Row;

/**
 * 等待复活  复活后生命x%+怒气50%
 */
public class WaitResurgenceAngerBuff extends NewBattleBuffWrap {
    private AngerResurgenceType type;

    public WaitResurgenceAngerBuff(AngerResurgenceType type) {
        this.type = type;
    }


    public WaitResurgenceAngerBuff(BattleContext context, int uniId, int id, Table_New_Buff_Row buffRow, NewBattleHeroWrap belongHero, NewBattleHeroWrap fromHero, NewBattleSkill fromSkill, int whichRoundAdd, int lastReduceRound, int roundOri, int round, long effectRate, long effectFixValue, AngerResurgenceType type) {
        super(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue);
        this.type = type;
    }

    @Override
    public void immediatelyTick() {
        this.tick();
        check();
    }

    @Override
    protected void tick0() {
        this.belongHero.setWaitForResurgence(true);
        int angerValue = 10000;
        if (type == AngerResurgenceType.TO50) {
            angerValue = 5000;
        }
        // hp
        replaceHeroAttr(this.belongHero, HeroAttrTypeEnum.HP, this.effectRate);
        // anger
        replaceHeroAttr(this.belongHero, HeroAttrTypeEnum.Anger, angerValue);

        System.out.println("BUFF  复活：" + this.belongHero + "--->" + this + "--->replace[HP]:" + this.belongHero.curHeroAttr(HeroAttrTypeEnum.HP) + "--->replace[Anger]:" + this.belongHero.curHeroAttr(HeroAttrTypeEnum.Anger));
        this.round = 0;
    }

    /**
     * 替换武将的属性位原来的万分必
     *
     * @param belongHero
     * @param attrType
     * @param percent
     */
    public static void replaceHeroAttr(NewBattleHeroWrap belongHero, HeroAttrTypeEnum attrType, long percent) {
        long ori = belongHero.getHeroAttrsOri().get(attrType);
        long toAdd = ori * percent / MagicNumbers.RANDOM_BASE_VALUE;
        belongHero.getHeroAttrsCur().replace(attrType, toAdd);
    }


    @Override
    public String toString() {
        return "[等待复活:" + type + "--" + this.id + "--" + this.getUniId() + "--<<<<" + this.belongHero + this.fromHero + ">>>>]";
    }

    @Override
    public NewBattleBuffWrap clone() {
        return new WaitResurgenceAngerBuff(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue, type);

    }
}
