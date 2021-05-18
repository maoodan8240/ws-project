package ws.newBattle.test;

import ws.common.table.container.RefreshableTableContainerListener;
import ws.common.table.data.PlanningTableData;
import ws.common.table.table.interfaces.Row;
import ws.common.utils.dataSource.txt._PlanningTableData;
import ws.newBattle.BattleContext;
import ws.newBattle.NewBattleConfig;
import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.NewBattleSide;
import ws.newBattle.NewBattleWrap;
import ws.protos.EnumsProtos.BattlePos;
import ws.protos.EnumsProtos.BattleSideEnum;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.protos.EnumsProtos.SkillPositionEnum;
import ws.relationship.base.HeroAttr;
import ws.relationship.base.HeroAttrs;
import ws.relationship.table.RootTc;
import ws.relationship.topLevelPojos.heros.Hero;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangweiwei on 17-1-4.
 */
public class BattleTest {

    public static void main(String[] args) throws Exception {
        PlanningTableData planningTableData = new _PlanningTableData("/home/zhangweiwei/Work/y9-git/gameServer/src/main/resources/data/tab/");
        planningTableData.loadAllTablesData();
        RootTc.init(planningTableData, new RefreshableTableContainerListener() {
            @Override
            public void preRefresh() {

            }

            @Override
            public void postRefresh(List<Class<? extends Row>> list) {

            }
        });

        new BattleTest().build();
    }

    private void build() {
        BattleContext context = new BattleContext();
        NewBattleConfig config = new NewBattleConfig(10, 1);
        NewBattleWrap battle = new NewBattleWrap();
        battle.setBattleConfig(config);
        context.setBattle(battle);
        battle.setContext(context);
        battle.init(createBattleAttackSide(context), createBattleDefenseSide(context));
        battle.waveBattle();
    }


    private NewBattleSide createBattleAttackSide(BattleContext context) {
        NewBattleSide side = new NewBattleSide(BattleSideEnum.ATTACK);
        Map<BattlePos, NewBattleHeroWrap> posToHero = new HashMap<>();
        long[] values_1000001 = new long[]{
                /*攻防血*/1000, 100, 10000,/*暴击*/5000, 1000, 1500,/*格档*/5000, 1000, 1500,/*伤害免伤*/1000, 1000,
                /*吸血反伤*/1000, 1000,/*控制*/0, 0,/*怒气*/100, 300, 100, 300, 100, 200, 200,
                /*小技能概率*/3300,/*大招伤害*/1000, 1000,/*被打*/1000, 1000, 1000, 1000, 1000, 1000,
        };
        posToHero.put(BattlePos.A, createHeroWrap(context, side, BattlePos.A, 1000001, values_1000001));
        long[] values_1000002 = new long[]{
                /*攻防血*/1000, 100, 10000,/*暴击*/5000, 1000, 1500,/*格档*/5000, 1000, 1500,/*伤害免伤*/1000, 1000,
                /*吸血反伤*/1000, 1000,/*控制*/0, 0,/*怒气*/100, 300, 100, 300, 100, 200, 200,
                /*小技能概率*/3300,/*大招伤害*/1000, 1000,/*被打*/1000, 1000, 1000, 1000, 1000, 1000,
        };
        posToHero.put(BattlePos.B, createHeroWrap(context, side, BattlePos.B, 1000002, values_1000002));
        long[] values_1000003 = new long[]{
                /*攻防血*/1000, 100, 10000,/*暴击*/5000, 1000, 1500,/*格档*/5000, 1000, 1500,/*伤害免伤*/1000, 1000,
                /*吸血反伤*/1000, 1000,/*控制*/0, 0,/*怒气*/100, 300, 100, 300, 100, 200, 200,
                /*小技能概率*/3300,/*大招伤害*/1000, 1000,/*被打*/1000, 1000, 1000, 1000, 1000, 1000,
        };
        posToHero.put(BattlePos.C, createHeroWrap(context, side, BattlePos.C, 1000003, values_1000003));
        long[] values_1000004 = new long[]{
                /*攻防血*/1000, 100, 10000,/*暴击*/5000, 1000, 1500,/*格档*/5000, 1000, 1500,/*伤害免伤*/1000, 1000,
                /*吸血反伤*/1000, 1000,/*控制*/0, 0,/*怒气*/100, 300, 100, 300, 100, 200, 200,
                /*小技能概率*/3300,/*大招伤害*/1000, 1000,/*被打*/1000, 1000, 1000, 1000, 1000, 1000,
        };
        posToHero.put(BattlePos.D, createHeroWrap(context, side, BattlePos.D, 1000004, values_1000004));
        long[] values_1000005 = new long[]{
                /*攻防血*/1000, 100, 10000,/*暴击*/5000, 1000, 1500,/*格档*/5000, 1000, 1500,/*伤害免伤*/1000, 1000,
                /*吸血反伤*/1000, 1000,/*控制*/0, 0,/*怒气*/100, 300, 100, 300, 100, 200, 200,
                /*小技能概率*/3300,/*大招伤害*/1000, 1000,/*被打*/1000, 1000, 1000, 1000, 1000, 1000,
        };
        posToHero.put(BattlePos.E, createHeroWrap(context, side, BattlePos.E, 1000005, values_1000005));
        long[] values_1000006 = new long[]{
                /*攻防血*/1000, 100, 10000,/*暴击*/5000, 1000, 1500,/*格档*/5000, 1000, 1500,/*伤害免伤*/1000, 1000,
                /*吸血反伤*/1000, 1000,/*控制*/0, 0,/*怒气*/100, 300, 100, 300, 100, 200, 200,
                /*小技能概率*/3300,/*大招伤害*/1000, 1000,/*被打*/1000, 1000, 1000, 1000, 1000, 1000,
        };
        posToHero.put(BattlePos.F, createHeroWrap(context, side, BattlePos.F, 1000006, values_1000006));
        side.init(posToHero);
        return side;
    }

    private NewBattleSide createBattleDefenseSide(BattleContext context) {
        NewBattleSide side = new NewBattleSide(BattleSideEnum.DEFENSE);
        Map<BattlePos, NewBattleHeroWrap> posToHero = new HashMap<>();
        long[] values_1000011 = new long[]{
                /*攻防血*/1000, 100, 10000,/*暴击*/5000, 1000, 1500,/*格档*/5000, 1000, 1500,/*伤害免伤*/1000, 1000,
                /*吸血反伤*/1000, 1000,/*控制*/0, 0,/*怒气*/100, 300, 100, 300, 100, 200, 200,
                /*小技能概率*/3300,/*大招伤害*/1000, 1000,/*被打*/1000, 1000, 1000, 1000, 1000, 1000,
        };
        posToHero.put(BattlePos.A, createHeroWrap(context, side, BattlePos.A, 1000011, values_1000011));
        long[] values_1000012 = new long[]{
                /*攻防血*/1000, 100, 10000,/*暴击*/5000, 1000, 1500,/*格档*/5000, 1000, 1500,/*伤害免伤*/1000, 1000,
                /*吸血反伤*/1000, 1000,/*控制*/0, 0,/*怒气*/100, 300, 100, 300, 100, 200, 200,
                /*小技能概率*/3300,/*大招伤害*/1000, 1000,/*被打*/1000, 1000, 1000, 1000, 1000, 1000,
        };
        posToHero.put(BattlePos.B, createHeroWrap(context, side, BattlePos.B, 1000012, values_1000012));
        long[] values_1000013 = new long[]{
                /*攻防血*/1000, 100, 10000,/*暴击*/5000, 1000, 1500,/*格档*/5000, 1000, 1500,/*伤害免伤*/1000, 1000,
                /*吸血反伤*/1000, 1000,/*控制*/0, 0,/*怒气*/100, 300, 100, 300, 100, 200, 200,
                /*小技能概率*/3300,/*大招伤害*/1000, 1000,/*被打*/1000, 1000, 1000, 1000, 1000, 1000,
        };
        posToHero.put(BattlePos.C, createHeroWrap(context, side, BattlePos.C, 1000013, values_1000013));
        long[] values_1000014 = new long[]{
                /*攻防血*/1000, 100, 10000,/*暴击*/5000, 1000, 1500,/*格档*/5000, 1000, 1500,/*伤害免伤*/1000, 1000,
                /*吸血反伤*/1000, 1000,/*控制*/0, 0,/*怒气*/100, 300, 100, 300, 100, 200, 200,
                /*小技能概率*/3300,/*大招伤害*/1000, 1000,/*被打*/1000, 1000, 1000, 1000, 1000, 1000,
        };
        posToHero.put(BattlePos.D, createHeroWrap(context, side, BattlePos.D, 1000014, values_1000014));
        long[] values_1000015 = new long[]{
                /*攻防血*/1000, 100, 10000,/*暴击*/5000, 1000, 1500,/*格档*/5000, 1000, 1500,/*伤害免伤*/1000, 1000,
                /*吸血反伤*/1000, 1000,/*控制*/0, 0,/*怒气*/100, 300, 100, 300, 100, 200, 200,
                /*小技能概率*/3300,/*大招伤害*/1000, 1000,/*被打*/1000, 1000, 1000, 1000, 1000, 1000,
        };
        posToHero.put(BattlePos.E, createHeroWrap(context, side, BattlePos.E, 1000015, values_1000015));
        long[] values_1000016 = new long[]{
                /*攻防血*/1000, 100, 10000,/*暴击*/5000, 1000, 1500,/*格档*/5000, 1000, 1500,/*伤害免伤*/1000, 1000,
                /*吸血反伤*/1000, 1000,/*控制*/0, 0,/*怒气*/100, 300, 100, 300, 100, 200, 200,
                /*小技能概率*/3300,/*大招伤害*/1000, 1000,/*被打*/1000, 1000, 1000, 1000, 1000, 1000,
        };
        posToHero.put(BattlePos.F, createHeroWrap(context, side, BattlePos.F, 1000016, values_1000016));
        side.init(posToHero);
        return side;
    }

    private NewBattleHeroWrap createHeroWrap(BattleContext context, NewBattleSide side, BattlePos pos, int tpId, long[] values) {
        Hero hero = new Hero();
        hero.setTpId(tpId);
        hero.getSkills().put(SkillPositionEnum.Skill_POS_1, 20);
        hero.getSkills().put(SkillPositionEnum.Skill_POS_2, 20);
        hero.getSkills().put(SkillPositionEnum.Skill_POS_3, 20);
        hero.getSkills().put(SkillPositionEnum.Skill_POS_4, 20);
        hero.getSkills().put(SkillPositionEnum.Skill_POS_5, 20);
        hero.getSkills().put(SkillPositionEnum.Skill_POS_6, 20);
        NewBattleHeroWrap heroWrap = new NewBattleHeroWrap();
        heroWrap.init(context, pos, hero, side, createHeroAttrs(values));
        return heroWrap;
    }

    private HeroAttrs createHeroAttrs(long[] values) {
        HeroAttrs attrs = new HeroAttrs();
        attrs.add(new HeroAttr(HeroAttrTypeEnum.Attack, values[0]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.Defense, values[1]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.HP, values[2]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.Crit, values[3]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.CritOppose, values[4]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.CritDamage, values[5]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.Block, values[6]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.BlockBreak, values[7]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.BlockDamage, values[8]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.Damage, values[9]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.DamageOppose, values[10]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.Suck, values[11]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.Thorns, values[12]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.FreeCtrl, values[13]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.Ctrl, values[14]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.StartAnger, values[15]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.HitAnger, values[16]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.HitedAnger, values[17]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.KillAnger, values[18]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.FriendDieAnger, values[19]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.EnemyDieAnger, values[20]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.RoundAnger, values[21]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.SmallSkillRate, values[22]));

        attrs.add(new HeroAttr(HeroAttrTypeEnum.AngerSkillDamage, values[23]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.AngerSkillDamageOppose, values[24]));

        attrs.add(new HeroAttr(HeroAttrTypeEnum.BeAtBySkillDamageOpposeAdd, values[25]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.BeAtBySkillDamageOpposeReduce, values[26]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.BeAtByAttackDamageOpposeAdd, values[27]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.BeAtByAttackDamageOpposeReduce, values[28]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.BeAtByDefenseDamageOpposeAdd, values[29]));
        attrs.add(new HeroAttr(HeroAttrTypeEnum.BeAtByDefenseDamageOpposeReduce, values[30]));
        return attrs;
    }
}
