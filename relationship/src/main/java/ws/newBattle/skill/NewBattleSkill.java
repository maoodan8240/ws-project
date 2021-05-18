package ws.newBattle.skill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.newBattle.BattleContext;
import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.buff.BuffCreater;
import ws.newBattle.buff.NewBattleBuffWrap;
import ws.newBattle.event.Event;
import ws.newBattle.event.EventListener;
import ws.newBattle.event.HeroDie;
import ws.newBattle.event.buff.AddSkillEffectsBefore;
import ws.newBattle.event.buff.BeSkillAttackAfter;
import ws.newBattle.event.buff.BeSkillAttackBefore;
import ws.newBattle.event.buff.SkillAttackAfter;
import ws.newBattle.event.buff.SkillAttackBefore;
import ws.newBattle.event.skill.SkillInnerEnd;
import ws.newBattle.event.skill.ToAddSkillEffectsAfterSkillAttack;
import ws.newBattle.event.skill.ToAddSkillEffectsBeforeSkillAttack;
import ws.newBattle.utils.NewBattleAllEnums.BuffContainerTypeEnum;
import ws.newBattle.utils.NewBattleCalcu;
import ws.newBattle.utils.NewBattleCalcu.BattleCalcuAttackResult;
import ws.newBattle.utils.SkillUtils;
import ws.protos.EnumsProtos.BuffStartPointEnum;
import ws.protos.EnumsProtos.EffectTypeEnum;
import ws.protos.EnumsProtos.FightTargetConditonEnum;
import ws.protos.EnumsProtos.FightTargetEnum;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.protos.EnumsProtos.SkillEffectTypeEnum;
import ws.protos.EnumsProtos.SpecialBuffEnum;
import ws.relationship.base.MagicNumbers;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_New_Buff_Row;
import ws.relationship.table.tableRows.Table_New_Skill_Row;
import ws.relationship.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class NewBattleSkill implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewBattleSkill.class);
    protected BattleContext context;
    protected NewBattleHeroWrap heroWrap;
    protected int skillId;
    protected Table_New_Skill_Row skill_row;
    private int level = 1;
    protected int useTimes; // 使用的次数
    private List<NewBattleHeroWrap> lastSkillTargets = new ArrayList<>(); // 上次使用技能的目标


    public NewBattleSkill init(BattleContext context, NewBattleHeroWrap hero, int skillId, int level) {
        this.context = context;
        this.heroWrap = hero;
        this.skillId = skillId;
        this.skill_row = RootTc.get(Table_New_Skill_Row.class, skillId);
        this.level = level;
        this.useTimes = 0;
        this.lastSkillTargets.clear();
        return this;
    }


    public void tick() {
        try {
            this.context.getBattleProto().setCurActionBuilder();
            this.context.getBattleProto().setHeroAttackAction(this.heroWrap.getId(), skillId);
            List<NewBattleHeroWrap> skillTargets = SkillUtils.getSkillTargets(heroWrap, this);
            if (skillTargets == null || skillTargets.size() <= 0) {
                // TODO: 16-12-21  log 找不到技能目标
            }
            context.getBeAttacks().clear();
            context.getBeAttacks().addAll(skillTargets);
            lastSkillTargets.clear();
            lastSkillTargets.addAll(skillTargets);
            this.onNotify(new ToAddSkillEffectsBeforeSkillAttack()); // 添加攻击前buff
            heroWrap.onNotify(new SkillAttackBefore(heroWrap, this, skillTargets)); // 加完攻击前buff再通知-->准备攻击了
            List<NewBattleHeroWrap> dieHeros = new ArrayList<>();
            int tidx = 0;
            for (NewBattleHeroWrap h : skillTargets) {
                if (h.isDie()) {
                    continue;
                }
                tidx++;
                if (skill_row.getSkillEffectType() == SkillEffectTypeEnum.DAMAGE_EFFECT) {
                    this.context.getLog().add("       新的战斗:sideEnum=%s pos=%s id=%s 攻击--->beAttackId=%s!", this.heroWrap.getSideEnum(), this.heroWrap.getPos(), this.heroWrap.getId(), h.getId());
                    h.onNotify(new BeSkillAttackBefore(heroWrap, this, skillTargets));
                    BattleCalcuAttackResult result = NewBattleCalcu.calcuDamage(heroWrap, h, this);
                    h.handleAttackResult(heroWrap, this, result);
                    h.onNotify(new BeSkillAttackAfter(heroWrap, this, skillTargets, result.getHp()));
                    if (h.isDie()) {
                        dieHeros.add(h);
                        this.context.getBattle().notifyAllHeroes(new HeroDie(heroWrap, this, h));
                    }
                    this.context.getBattleProto().addHeroAttackEffect(h, result);// hpAngerStatus 包括了血量和怒气的最终值
                } else if (skill_row.getSkillEffectType() == SkillEffectTypeEnum.CURE_EFFECT) {
                    this.context.getLog().add("       新的战斗:sideEnum=%s pos=%s id=%s 治疗--->beCureId=%s!", this.heroWrap.getSideEnum(), this.heroWrap.getPos(), this.heroWrap.getId(), h.getId());
                    h.onNotify(new BeSkillAttackBefore(heroWrap, this, skillTargets));
                    long hp = NewBattleCalcu.calcuCure(heroWrap, h, this);
                    h.handleCureResult(heroWrap, this, hp);
                    this.context.getBattleProto().addHeroAttackEffect(h, hp); // hpAngerStatus 包括了血量和怒气的最终值
                }
            }
            heroWrap.onNotify(new AddSkillEffectsBefore(heroWrap, this, skillTargets)); // 通知准备添加攻击后buff --> 减少buff回合
            this.onNotify(new ToAddSkillEffectsAfterSkillAttack()); // 添加攻击后buff
            heroWrap.onNotify(new SkillAttackAfter(heroWrap, this, skillTargets)); // 加完攻击后buff再通知本次-->攻击结束了
            this.onNotify(new SkillInnerEnd());
        } catch (Exception e) {
            LOGGER.error("使用技能异常 hero={} skillId={} ", this.heroWrap, this.skillId, e);
        } finally {
            this.context.getBattleProto().addBuffChange();
            this.context.getBattleProto().onCurActionEnd();
        }
    }

    public Table_New_Skill_Row getSkill_row() {
        return skill_row;
    }

    public int getLevel() {
        return level;
    }

    public int getUseTimes() {
        return useTimes;
    }


    public void effectBuffs(BuffStartPointEnum startPointEnum) {
        try {
            int idx = 0;
            for (int buffId : skill_row.getbUFFId()) {
                if (startPointEnum.getNumber() != skill_row.getbUFFStartPoint().get(idx)) {
                    idx++;
                    continue;
                }
                int effectType = skill_row.getBuffEffectType().get(idx);
                int round = skill_row.getbUFFRound().get(idx);
                EffectTypeEnum effectTypeEnum = EffectTypeEnum.valueOf(effectType);
                Table_New_Buff_Row buff_row = RootTc.get(Table_New_Buff_Row.class, buffId);
                List<NewBattleHeroWrap> targets;
                long effectRate = skill_row.getbUFFRate().get(idx) + (level - 1) * skill_row.getbUFFRateStep().get(idx);
                long effectFixValue = skill_row.getbUFFValue().get(idx) + (level - 1) * skill_row.getbUFFValueStep().get(idx);
                int conValue = skill_row.getbUFFTriggerCondition().get(idx);
                if (conValue > 0) {
                    FightTargetConditonEnum conditonEnum = FightTargetConditonEnum.valueOf(conValue);
                    // 过滤技能的目标
                    targets = SkillUtils.getConditonTargets(lastSkillTargets, conditonEnum, skill_row.getbUFFTriggerNumber().get(idx));
                } else {
                    int range = skill_row.getBuffRange().get(idx);
                    int rangeConditon = skill_row.getBuffRangeCondition().get(idx);
                    int rangeConditonValue = skill_row.getBuffRangeValue().get(idx);
                    if (range <= 0) { // 使用技能的范围
                        targets = lastSkillTargets;
                    } else {
                        FightTargetEnum rangeEnum = FightTargetEnum.valueOf(range);
                        FightTargetConditonEnum rangeConditonEnum = FightTargetConditonEnum.valueOf(rangeConditon);
                        targets = SkillUtils.getBuffTargets(effectTypeEnum, heroWrap, rangeEnum, rangeConditonEnum, rangeConditonValue);
                    }
                }
                BuffContainerTypeEnum containerType = BuffContainerTypeEnum.TMP;
                if (startPointEnum == BuffStartPointEnum.ALIVE) {
                    containerType = BuffContainerTypeEnum.ALIVE;
                } else if (startPointEnum == BuffStartPointEnum.FOREVER) {
                    containerType = BuffContainerTypeEnum.FOREVER;
                }
                NewBattleBuffWrap buffWrap = BuffCreater.chooseBuff(this.context, buffId, effectTypeEnum, buff_row.getbUFFProperty());
                for (NewBattleHeroWrap h : targets) {
                    NewBattleBuffWrap buffWrapClone = buffWrap.clone(); // 此处必须clone
                    long pro = skill_row.getbUFFPro().get(idx) + (level - 1) * skill_row.getbUFFProStep().get(idx);
                    pro = amendBuffProbability(pro, buffId, h);
                    boolean rs = RandomUtils.isDropPartsFractionOfBase(pro, MagicNumbers.RANDOM_BASE_VALUE);
                    if (!rs) {
                        continue;
                    }
                    buffWrapClone.init(this.context, buffId, h, this.heroWrap, this, round, effectRate, effectFixValue);
                    h.addBuff(buffWrapClone, containerType);
                    this.context.getBattleProto().addBuffIdToHero(h.getId(), buffId);
                    this.context.putBuffChangeHero(h);
                }
                idx++;
            }
        } catch (Exception e) {
            LOGGER.error("添加buff异常 hero={} skillId={} ", this.heroWrap, this.skillId, e);
        }
    }


    private void onToClearSkillTmpData() {
        this.context.getBattle().getCurWave().getSideMap().values().forEach(side ->
                side.getPosToHero().values().forEach(hero ->
                        hero.getHeroAttrsAttackTmp().clear()
                )
        );
    }


    public long amendBuffProbability(long origin, int buffId, NewBattleHeroWrap heroWrap) {
        SpecialBuffEnum specialBuffEnum = SpecialBuffEnum.valueOf(buffId);
        if (specialBuffEnum == null) {
            return origin;
        }
        switch (specialBuffEnum) {
            case DIZZY:
            case PARALYSE:
            case FREEZE:
            case SILENCE:
            case SEAL:
                return origin + this.heroWrap.curHeroAttr(HeroAttrTypeEnum.Ctrl) - heroWrap.curHeroAttr(HeroAttrTypeEnum.FreeCtrl);
            default:
                return origin;
        }
    }

    //---------------------------事件处理 - start ---------------------------------

    @Override
    public void onNotify(Event event) {
        if (event instanceof ToAddSkillEffectsBeforeSkillAttack) {
            effectBuffs(BuffStartPointEnum.BEFORE_ATTACK);
        } else if (event instanceof ToAddSkillEffectsAfterSkillAttack) {
            effectBuffs(BuffStartPointEnum.AFTER_ATTACK);
        } else if (event instanceof SkillInnerEnd) {
            onToClearSkillTmpData();
        }
    }

    //---------------------------事件处理 - end ---------------------------------

    public int getSkillId() {
        return skillId;
    }
}

