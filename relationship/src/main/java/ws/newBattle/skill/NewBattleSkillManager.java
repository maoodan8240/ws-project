package ws.newBattle.skill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.newBattle.BattleContext;
import ws.newBattle.NewBattleConstants;
import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.buff.OtherEffectsCarrierBuff;
import ws.newBattle.event.BattleBegin;
import ws.newBattle.event.Event;
import ws.newBattle.event.EventListener;
import ws.newBattle.event.Resurgence;
import ws.newBattle.event.hero.RoundBegin;
import ws.newBattle.utils.NewBattleAllEnums.BuffContainerTypeEnum;
import ws.protos.EnumsProtos.BuffStartPointEnum;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.protos.EnumsProtos.SkillPositionEnum;
import ws.relationship.base.MagicNumbers;
import ws.relationship.utils.RandomUtils;
import ws.relationship.utils.RelationshipCommonUtils;

/**
 * Created by zhangweiwei on 16-12-21.
 */
public class NewBattleSkillManager implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewBattleSkillManager.class);
    protected BattleContext context;
    private NewBattleHeroWrap heroWrap;
    private NewBattleSkill simple;              // 普通技能
    private NewBattleSkill small;               // 小技能
    private NewBattleSkill big;                 // 大招
    private NewBattleSkill secondPassive;       // 第二被动技能
    private boolean useSmallSkill;              // 是否使用小技能


    public void init(BattleContext context, NewBattleHeroWrap hero) {
        this.context = context;
        this.heroWrap = hero;
        int simpleId = hero.getCardRow().getSkillAtt();
        SkillAndLevel smallSkillAndLevel = getSkillAndLevel(SkillPositionEnum.Skill_POS_1);
        SkillAndLevel bigSkillAndLevel = getSkillAndLevel(SkillPositionEnum.Skill_POS_2);
        SkillAndLevel secondPassiveSkillAndLevel = getSkillAndLevel(SkillPositionEnum.Skill_POS_4);

        this.simple = NewBattleSkillCreater.createSkill(context, hero, simpleId, MagicNumbers.DefaultValue_Skill_Level);
        this.small = NewBattleSkillCreater.createSkill(context, hero, smallSkillAndLevel.getId(), smallSkillAndLevel.getLevel());
        this.big = NewBattleSkillCreater.createSkill(context, hero, bigSkillAndLevel.getId(), bigSkillAndLevel.getLevel());
        if (secondPassiveSkillAndLevel != null) {
            this.secondPassive = NewBattleSkillCreater.createSkill(context, hero, secondPassiveSkillAndLevel.getId(), secondPassiveSkillAndLevel.getLevel());
        }
    }


    public void simpleTick() {
        if (useSmallSkill) {
            this.context.getLog().add("       新的战斗:sideEnum=%s pos=%s id=%s 使用小技能!", this.heroWrap.getSideEnum(), this.heroWrap.getPos(), this.heroWrap.getId());
            small.tick();
        } else {
            this.context.getLog().add("       新的战斗:sideEnum=%s pos=%s id=%s 使用普通攻击!", this.heroWrap.getSideEnum(), this.heroWrap.getPos(), this.heroWrap.getId());
            simple.tick();
        }
    }

    public void angerTick() {
        if (heroWrap.canUseBigSkill() && heroWrap.getCurAnger() > NewBattleConstants.MAX_ANGER_NUM) {
            this.heroWrap.getHeroAttrsCur().replace(HeroAttrTypeEnum.Anger, 0l);  // 怒气攻击前清除所有怒气
            big.tick();
        }
    }


    private void onBattleBegin() {
        OtherEffectsCarrierBuff buff = OtherEffectsCarrierBuff.create(this.context, this.heroWrap);
        this.heroWrap.addBuff(buff, BuffContainerTypeEnum.FOREVER); // 此处不需要clone
    }

    /**
     * 每个回合开始前决定该武将能不能释放小技能
     *
     * @param event
     */
    private void onRoundBegin(RoundBegin event) {
        useSmallSkill = RandomUtils.isDropPartsFractionOfBase(heroWrap.curHeroAttr(HeroAttrTypeEnum.SmallSkillRate), MagicNumbers.RANDOM_BASE_VALUE);
    }

    public boolean isUseSmallSkill() {
        return useSmallSkill;
    }

    private SkillAndLevel getSkillAndLevel(SkillPositionEnum skillPos) {
        boolean awake = RelationshipCommonUtils.isHeroAwake(this.heroWrap.getHero());
        int skillId = this.heroWrap.getCardRow().getSkillId(skillPos, awake);
        if (!this.heroWrap.getHero().getSkills().containsKey(skillPos)) {
            return null;
        }
        return new SkillAndLevel(skillId, this.heroWrap.getHero().getSkills().get(skillPos));
    }


    @Override
    public void onNotify(Event event) {
        if (event instanceof BattleBegin) {
            // 处理secondPassive技能的buff
            if (secondPassive != null) {
                secondPassive.effectBuffs(BuffStartPointEnum.ALIVE);
                secondPassive.effectBuffs(BuffStartPointEnum.ENTER);
            }
            onBattleBegin();
        } else if (event instanceof RoundBegin) {
            onRoundBegin((RoundBegin) event);
        } else if (event instanceof Resurgence) {
            if (secondPassive != null) {
                secondPassive.effectBuffs(BuffStartPointEnum.ALIVE);
            }
        }
    }
}

