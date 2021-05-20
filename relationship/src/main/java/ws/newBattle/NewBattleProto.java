package ws.newBattle;

import ws.newBattle.utils.NewBattleCalcu.BattleCalcuAttackResult;
import ws.protos.BattleProtos.Sm_Battle_Action;
import ws.protos.BattleProtos.Sm_Battle_BackData;
import ws.protos.BattleProtos.Sm_Battle_ScriptRound;
import ws.protos.BattleProtos.Sm_Battle_ScriptWave;
import ws.protos.EnumsProtos.BattleResultTypeEnum;

/**
 * Created by lee on 17-5-15.
 */
public class NewBattleProto {
    private Sm_Battle_BackData.Builder backData = NewBattleProtosUtils.create_Sm_Battle_BackData();
    private BattleContext context;

    private Sm_Battle_ScriptWave.Builder curWaveBuilder;
    private Sm_Battle_ScriptRound.Builder curRoundBuilder;
    private Sm_Battle_Action.Builder curActionBuilder;

    public NewBattleProto(BattleContext context) {
        this.context = context;
    }

    public void setCurWaveBuilder() {
        this.curWaveBuilder = NewBattleProtosUtils.create_Sm_Battle_ScriptWave(context.getBattle().getCurWave());
    }

    public void setCurRoundBuilder() {
        this.curRoundBuilder = NewBattleProtosUtils.create_Sm_Battle_ScriptRound(context.getBattle().getCurWave());
    }

    public void onCurWaveEnd() {
        this.backData.addWaveLis(curWaveBuilder);
    }

    public void onCurRoundEnd() {
        this.curWaveBuilder.addRoundLis(curRoundBuilder);
    }

    public void setCurActionBuilder() {
        this.curActionBuilder = Sm_Battle_Action.newBuilder();
    }

    public void setHeroAttackAction(int attackHeroId, int attackSkillId) {
        this.curActionBuilder.setHeroId(attackHeroId);
        this.curActionBuilder.setSkillId(attackSkillId);
    }


    public void setBattleResult(BattleResultTypeEnum result) {
        backData.setResult(NewBattleProtosUtils.create_Sm_Battle_Resut(result, this.context.getBattle().curWave.getSideMap()));
    }

    public void addHeroAttackEffect(NewBattleHeroWrap beAttackHeroWrap, BattleCalcuAttackResult result) {
        curActionBuilder.addEffectLis(NewBattleProtosUtils.create_Sm_Battle_ActionEffect_AttackOrCure(-1 * result.getHp(), 0, result.isCrt(), result.isBlock(), beAttackHeroWrap));
    }

    public void addHeroAttackEffect(NewBattleHeroWrap beCureHeroWrap, long hp) {
        curActionBuilder.addEffectLis(NewBattleProtosUtils.create_Sm_Battle_ActionEffect_AttackOrCure(hp, 0, false, false, beCureHeroWrap));
    }

    public void addBuffEffect_Hp(int buffId, int fromHeroId, NewBattleHeroWrap beBuffHeroWrap, long hp) {
        Sm_Battle_Action.Builder b = Sm_Battle_Action.newBuilder();
        b.setHeroId(fromHeroId);
        b.setBuffId(buffId);
        b.addEffectLis(NewBattleProtosUtils.create_Sm_Battle_ActionEffect_Buff_Hp(hp, beBuffHeroWrap));
        curRoundBuilder.addActionLis(b);
    }

    public void addBuffEffect_Anger(int buffId, int fromHeroId, NewBattleHeroWrap beBuffHeroWrap, long anger) {
        Sm_Battle_Action.Builder b = Sm_Battle_Action.newBuilder();
        b.setHeroId(fromHeroId);
        b.setBuffId(buffId);
        b.addEffectLis(NewBattleProtosUtils.create_Sm_Battle_ActionEffect_Buff_Anger(anger, beBuffHeroWrap));
        curRoundBuilder.addActionLis(b);
    }

    public void addBuffChange() {
        curActionBuilder.addAllEffectLis(NewBattleProtosUtils.create_Sm_Battle_ActionEffect_Lis(context.getIdToBuffChangeHero()));
        context.clearBuffChangeHero();
    }

    public void addBuffIdToHero(int heroId, int buffId) { // 被动技能加的时候curActionBuilder还没生成
        // todo
        if (curActionBuilder == null) {
            return;
        }
        curActionBuilder.addAddBuffLis(NewBattleProtosUtils.create_Sm_Battle_ActionAddBuff(heroId, buffId));
    }


    public void onCurActionEnd() {
        if (curActionBuilder.getEffectLisCount() > 0 || curActionBuilder.getAddBuffLisCount() > 0) {
            curRoundBuilder.addActionLis(curActionBuilder);
        }
    }

    public Sm_Battle_BackData getBackData() {
        return backData.build();
    }
}
