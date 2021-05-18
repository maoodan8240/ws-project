package ws.newBattle;

import ws.protos.BattleProtos.Sm_Battle_ActionAddBuff;
import ws.protos.BattleProtos.Sm_Battle_ActionEffect;
import ws.protos.BattleProtos.Sm_Battle_BackData;
import ws.protos.BattleProtos.Sm_Battle_HeroBuffStatus;
import ws.protos.BattleProtos.Sm_Battle_HeroHpAngerStatus;
import ws.protos.BattleProtos.Sm_Battle_HeroStatus;
import ws.protos.BattleProtos.Sm_Battle_Hero_AttackDetail;
import ws.protos.BattleProtos.Sm_Battle_Resut;
import ws.protos.BattleProtos.Sm_Battle_ScriptRound;
import ws.protos.BattleProtos.Sm_Battle_ScriptWave;
import ws.protos.EnumsProtos.BattleActionEffectTypeEnum;
import ws.protos.EnumsProtos.BattleResultTypeEnum;
import ws.protos.EnumsProtos.BattleSideEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangweiwei on 17-5-15.
 */
public class NewBattleProtosUtils {

    /**
     * 战斗返回数据
     *
     * @return
     */
    public static Sm_Battle_BackData.Builder create_Sm_Battle_BackData() {
        Sm_Battle_BackData.Builder b = Sm_Battle_BackData.newBuilder();
        return b;
    }


    /**
     * 战斗-波 数据
     *
     * @param wave
     * @return
     */
    public static Sm_Battle_ScriptWave.Builder create_Sm_Battle_ScriptWave(NewBattleWave wave) {
        Sm_Battle_ScriptWave.Builder b = Sm_Battle_ScriptWave.newBuilder();
        b.setWave(wave.getCurWaveNum());
        return b;
    }

    /**
     * 创建回合builder,并且初始化各个武将状态
     *
     * @param wave
     * @return
     */
    public static Sm_Battle_ScriptRound.Builder create_Sm_Battle_ScriptRound(NewBattleWave wave) {
        Sm_Battle_ScriptRound.Builder b = Sm_Battle_ScriptRound.newBuilder();
        b.setRound(wave.getCurRoundNum());
        wave.getSideMap().forEach((sideEnum, sideObj) -> {
            sideObj.getPosToHero().forEach((pos, heroWrap) -> {
                b.addHeroStatusLis(create_Sm_Battle_HeroStatus(heroWrap));
            });
        });
        return b;
    }


    /**
     * 武将的状态
     *
     * @param heroWrap
     * @return
     */
    public static Sm_Battle_HeroStatus create_Sm_Battle_HeroStatus(NewBattleHeroWrap heroWrap) {
        Sm_Battle_HeroStatus.Builder b = Sm_Battle_HeroStatus.newBuilder();
        b.setHeroId(heroWrap.getId());
        b.setHeroTpId(heroWrap.getHero().getTpId());
        b.setSide(heroWrap.getBelongBattleSide().getCurSide());
        b.setPos(heroWrap.getPos());
        b.setHp(heroWrap.getCurHp());
        b.setAnger(heroWrap.getCurAnger());
        b.setUseSmallSkill(heroWrap.getSkillManager().isUseSmallSkill());
        b.addAllBuffId(heroWrap.getBuffManager().getUnrepetitionBuffTpIds());
        return b.build();
    }


    /**
     * 攻击细节
     *
     * @param hp
     * @param anger
     * @param crt
     * @param block
     * @param kill
     * @return
     */
    public static Sm_Battle_Hero_AttackDetail create_Sm_Battle_Hero_AttackDetail(long hp, long anger, boolean crt, boolean block, boolean kill) {
        Sm_Battle_Hero_AttackDetail.Builder b = Sm_Battle_Hero_AttackDetail.newBuilder();
        b.setHp(hp);
        b.setAnger(anger);
        b.setCrt(crt);
        b.setBlock(block);
        b.setKill(kill);
        return b.build();
    }


    /**
     * 武将血量、怒气状态
     *
     * @param heroWrap
     * @return
     */
    public static Sm_Battle_HeroHpAngerStatus create_Sm_Battle_HeroHpAngerStatus(NewBattleHeroWrap heroWrap) {
        Sm_Battle_HeroHpAngerStatus.Builder b = Sm_Battle_HeroHpAngerStatus.newBuilder();
        b.setHeroId(heroWrap.getId());
        b.setHp(heroWrap.getCurHp());
        b.setAnger(heroWrap.getCurAnger());
        return b.build();

    }


    /**
     * for 攻击OR治疗
     *
     * @param hp
     * @param anger
     * @param crt
     * @param block
     * @param beHeroWrap
     * @return
     */
    public static Sm_Battle_ActionEffect create_Sm_Battle_ActionEffect_AttackOrCure(long hp, long anger,
                                                                                    boolean crt, boolean block, NewBattleHeroWrap beHeroWrap) {
        Sm_Battle_ActionEffect.Builder b = Sm_Battle_ActionEffect.newBuilder();
        b.setType(BattleActionEffectTypeEnum.ACTION_EFT_ATTACK);
        b.setAttackDetail(create_Sm_Battle_Hero_AttackDetail(hp, anger, crt, block, beHeroWrap.isDie()));
        b.setHpAngerStatus(create_Sm_Battle_HeroHpAngerStatus(beHeroWrap));
        return b.build();
    }

    /**
     * for BUFF ，比如 灼烧
     *
     * @param hp
     * @param beBuffHeroWrap
     * @return
     */
    public static Sm_Battle_ActionEffect create_Sm_Battle_ActionEffect_Buff_Hp(long hp, NewBattleHeroWrap beBuffHeroWrap) {
        Sm_Battle_ActionEffect.Builder b = Sm_Battle_ActionEffect.newBuilder();
        b.setType(BattleActionEffectTypeEnum.ACTION_EFT_BUFF);
        b.setAttackDetail(create_Sm_Battle_Hero_AttackDetail(hp, 0, false, false, false));
        b.setHpAngerStatus(create_Sm_Battle_HeroHpAngerStatus(beBuffHeroWrap));
        return b.build();
    }


    /**
     * for BUFF ，比如 灼烧
     *
     * @param anger
     * @param beBuffHeroWrap
     * @return
     */
    public static Sm_Battle_ActionEffect create_Sm_Battle_ActionEffect_Buff_Anger(long anger, NewBattleHeroWrap beBuffHeroWrap) {
        Sm_Battle_ActionEffect.Builder b = Sm_Battle_ActionEffect.newBuilder();
        b.setType(BattleActionEffectTypeEnum.ACTION_EFT_BUFF);
        b.setAttackDetail(create_Sm_Battle_Hero_AttackDetail(0, anger, false, false, false));
        b.setHpAngerStatus(create_Sm_Battle_HeroHpAngerStatus(beBuffHeroWrap));
        return b.build();
    }

    /**
     * 所有buff发生增减的Effect
     *
     * @param idToBuffChangeHero
     * @return
     */
    public static List<Sm_Battle_ActionEffect> create_Sm_Battle_ActionEffect_Lis(Map<Integer, NewBattleHeroWrap> idToBuffChangeHero) {
        List<Sm_Battle_ActionEffect> bs = new ArrayList<>();
        Sm_Battle_ActionEffect.Builder b = Sm_Battle_ActionEffect.newBuilder();
        idToBuffChangeHero.forEach((id, heroWrap) -> {
            b.clear();
            b.setType(BattleActionEffectTypeEnum.ACTION_EFT_BUFF_CHANGE);
            b.setBuffStatus(create_Sm_Battle_HeroBuffStatus(heroWrap));
            bs.add(b.build());
        });
        return bs;
    }


    /**
     * 给某一个武将添加离buff
     *
     * @param addToHeroId
     * @param buffId
     * @return
     */
    public static Sm_Battle_ActionAddBuff create_Sm_Battle_ActionAddBuff(int addToHeroId, int buffId) {
        Sm_Battle_ActionAddBuff.Builder b = Sm_Battle_ActionAddBuff.newBuilder();
        b.setHeroId(addToHeroId);
        b.setBuffId(buffId);
        return b.build();
    }

    /**
     * 武将buff状态
     *
     * @param heroWrap
     * @return
     */
    public static Sm_Battle_HeroBuffStatus create_Sm_Battle_HeroBuffStatus(NewBattleHeroWrap heroWrap) {
        Sm_Battle_HeroBuffStatus.Builder b = Sm_Battle_HeroBuffStatus.newBuilder();
        b.setHeroId(heroWrap.getId());
        b.setWaitResurgence(heroWrap.isWaitForResurgence());
        b.addAllBuffId(heroWrap.getBuffManager().getUnrepetitionBuffTpIds());
        return b.build();
    }


    /**
     * 战斗结果
     *
     * @param result
     * @param sideMap
     * @return
     */
    public static Sm_Battle_Resut create_Sm_Battle_Resut(BattleResultTypeEnum result, Map<BattleSideEnum, NewBattleSide> sideMap) {
        Sm_Battle_Resut.Builder b = Sm_Battle_Resut.newBuilder();
        b.setRsType(result);
        sideMap.forEach((sideEnum, sideObj) -> {
            sideObj.getPosToHero().forEach((pos, heroWrap) -> {
                b.addHeroStatusLis(create_Sm_Battle_HeroHpAngerStatus(heroWrap));
            });
        });
        return b.build();
    }

}
