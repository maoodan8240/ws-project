package ws.newBattle.buff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.newBattle.BattleContext;
import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.event.Event;
import ws.newBattle.event.EventListener;
import ws.newBattle.skill.NewBattleSkill;
import ws.newBattle.utils.NewBattleAllEnums.BuffContainerTypeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by zhangweiwei on 16-12-21.
 */
public class NewBattleBuffManager implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewBattleBuffManager.class);
    private BattleContext context;
    private NewBattleHeroWrap belongHeroWrap;
    private List<NewBattleBuffWrap> buffs = new ArrayList<>();
    private Map<NewBattleHeroWrap, List<NewBattleBuffWrap>> heroToAliveBuffs = new HashMap<>(); // 武将--其存活buff
    private List<NewBattleBuffWrap> foreverBuffs = new ArrayList<>(); // 永久性Buff

    public void init(BattleContext context, NewBattleHeroWrap belongHeroWrap) {
        this.context = context;
        this.belongHeroWrap = belongHeroWrap;
    }

    public void clearBuffOnDie() {
        this.heroToAliveBuffs.clear();
        this.buffs.clear();
    }

    public boolean containsBuff(Class<? extends NewBattleBuff> clzz) {
        for (NewBattleBuffWrap buff : buffs) {
            if (buff.getClass().equals(clzz)) {
                return true;
            }
        }
        return false;
    }

    private List<NewBattleBuffWrap> fromSameSkillBuffs(NewBattleSkill skill) {
        List<NewBattleBuffWrap> same = new ArrayList<>();
        if (skill == null) {
            return same;
        }
        for (NewBattleBuffWrap buff : buffs) {
            if (skill.getSkillId() == buff.fromSkill.getSkillId()) { // 技能Id相同则认为相同的
                same.add(buff);
            }
        }
        return same;
    }


    public void rempoveBuff(NewBattleBuffWrap buffWrap) {
        this.buffs.remove(buffWrap);
        // this.aliveBuffs.remove(buffWrap); // TODO: 16-12-29
        this.foreverBuffs.remove(buffWrap);
    }


    public void addBuff(NewBattleBuffWrap buff, BuffContainerTypeEnum containerType) {
        // 相同格斗家的相同技能的buff直接覆盖
        List<NewBattleBuffWrap> same = fromSameSkillBuffs(buff.getFromSkill());
        for (NewBattleBuffWrap buffTmp : same) {
            rempoveBuff(buffTmp);
        }
        addBuff0(buff, containerType);
        buff.immediatelyTick();
    }

    private void addBuff0(NewBattleBuffWrap buff, BuffContainerTypeEnum containerType) {
        switch (containerType) {
            case TMP:
                this.buffs.add(buff);
                return;
            case ALIVE:
                List<NewBattleBuffWrap> buffLisTmp = this.heroToAliveBuffs.get(buff.fromHero);
                if (buffLisTmp == null) {
                    buffLisTmp = new ArrayList<>();
                    this.heroToAliveBuffs.put(buff.fromHero, buffLisTmp);
                }
                buffLisTmp.add(buff);
                return;
            case FOREVER:
                this.foreverBuffs.add(buff);
                return;
            default:
                this.buffs.add(buff);
                return;
        }
    }


    /**
     * 所有不重复的buffTpId
     *
     * @return
     */
    public List<Integer> getUnrepetitionBuffTpIds() {
        List<Integer> ids = new ArrayList<>();
        buffs.forEach(buff -> {
            if (!ids.contains(buff.getId()) && buff.getId() > 0) {
                ids.add(buff.getId());
            }
        });

        heroToAliveBuffs.forEach((h, buffWrapList) -> {
            buffWrapList.forEach(buff -> {
                if (!ids.contains(buff.getId()) && buff.getId() > 0) {
                    ids.add(buff.getId());
                }
            });
        });
        foreverBuffs.forEach(buff -> {
            if (!ids.contains(buff.getId()) && buff.getId() > 0) {
                ids.add(buff.getId());
            }
        });
        return ids;
    }


    //---------------------------事件处理 - start ---------------------------------


    @Override
    public void onNotify(Event event) {
        try {
            for (NewBattleBuffWrap buffTmp : new ArrayList<>(this.buffs)) {
                buffTmp.onNotify(event);
            }
            Map<NewBattleHeroWrap, List<NewBattleBuffWrap>> heroToAliveBuffsTmp = new HashMap<>();
            heroToAliveBuffsTmp.putAll(heroToAliveBuffs);
            for (Entry<NewBattleHeroWrap, List<NewBattleBuffWrap>> kv : heroToAliveBuffsTmp.entrySet()) {
                for (NewBattleBuffWrap buffTmp : kv.getValue()) {
                    buffTmp.onNotify(event);
                }
            }
            for (NewBattleBuffWrap buffTmp : new ArrayList<>(foreverBuffs)) {
                buffTmp.onNotify(event);
            }
        } catch (Exception e) {
            LOGGER.error("event={}", event, e);
        }
    }

    //---------------------------事件处理 - end ---------------------------------
}
