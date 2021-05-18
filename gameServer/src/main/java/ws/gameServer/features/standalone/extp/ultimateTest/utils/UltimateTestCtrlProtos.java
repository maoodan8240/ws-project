package ws.gameServer.features.standalone.extp.ultimateTest.utils;

import ws.protos.EnumsProtos.HardTypeEnum;
import ws.protos.EnumsProtos.HeroPositionEnum;
import ws.protos.EnumsProtos.UltimateTestBuffIndexTypeEnum;
import ws.protos.UltimateTestProtos.Sm_UltimateTest_Buff_Info;
import ws.protos.UltimateTestProtos.Sm_UltimateTest_Enemies;
import ws.protos.UltimateTestProtos.Sm_UltimateTest_FightResult;
import ws.protos.UltimateTestProtos.Sm_UltimateTest_Hero_Info;
import ws.protos.UltimateTestProtos.Sm_UltimateTest_Info;
import ws.protos.UltimateTestProtos.Sm_UltimateTest_Monster;
import ws.relationship.topLevelPojos.ultimateTest.UltimateTest;
import ws.relationship.topLevelPojos.ultimateTest.UltimateTestHero;
import ws.relationship.topLevelPojos.ultimateTest.UltimatetestBuff;
import ws.relationship.topLevelPojos.ultimateTest.UltimatetestEnemy;
import ws.relationship.topLevelPojos.ultimateTest.UltimatetestMonster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class UltimateTestCtrlProtos {
    public static Sm_UltimateTest_FightResult create_Sm_UltimateTest_FightResult(int score, int buffstar) {
        Sm_UltimateTest_FightResult.Builder b = Sm_UltimateTest_FightResult.newBuilder();
        b.setScore(score);
        b.setBuffstar(buffstar);
        return b.build();
    }

    public static Sm_UltimateTest_Enemies create_Sm_UltimateTest_Enemy(UltimatetestEnemy enemy) {
        Sm_UltimateTest_Enemies.Builder b = Sm_UltimateTest_Enemies.newBuilder();
        b.setHardLevel(enemy.getHardTypeEnum());
        b.setPlayerName(enemy.getPlayerName());
        b.addAllMonster(create_Sm_UltimateTest_Monster_List(enemy.getMonsterList()));
        return b.build();
    }


    public static List<Sm_UltimateTest_Monster> create_Sm_UltimateTest_Monster_List(List<UltimatetestMonster> monsterList) {
        List<Sm_UltimateTest_Monster> smUltimateTestMonsterList = new ArrayList<>();
        int index = 1;
        for (UltimatetestMonster monster : monsterList) {
            smUltimateTestMonsterList.add(create_Sm_UltimateTest_Monster(monster, HeroPositionEnum.valueOf(index)));
            index++;
        }
        return smUltimateTestMonsterList;
    }


    public static Sm_UltimateTest_Monster create_Sm_UltimateTest_Monster(UltimatetestMonster monster, HeroPositionEnum positionEnum) {
        Sm_UltimateTest_Monster.Builder b = Sm_UltimateTest_Monster.newBuilder();
        b.setMonsterGrade(monster.getMonsterGrade());
        b.setMonsterId(monster.getMonsterId());
        b.setMonsterLv(monster.getLevel());
        b.setMonsterStar(monster.getMonsterStar());
        b.setPosition(positionEnum);
        return b.build();
    }

    public static List<Sm_UltimateTest_Enemies> create_Sm_UltimateTest_Enemy_List(Map<HardTypeEnum, UltimatetestEnemy> hardLevelToSimplePlayer) {
        List<Sm_UltimateTest_Enemies> sm_ultimateTest_enemyList = new ArrayList<>();
        for (Entry<HardTypeEnum, UltimatetestEnemy> entry : hardLevelToSimplePlayer.entrySet()) {
            sm_ultimateTest_enemyList.add(create_Sm_UltimateTest_Enemy(entry.getValue()));
        }
        return sm_ultimateTest_enemyList;
    }

    public static Sm_UltimateTest_Info create_Sm_UltimateTest_Info(UltimateTest target) {
        Sm_UltimateTest_Info.Builder b = Sm_UltimateTest_Info.newBuilder();
        b.setStageLevel(target.getStageLevel());
        b.setScore(target.getTestScore());
        b.setHistoryHighLevel(target.getHistoryHighLevel());
        b.setBuffStar(target.getBuffStar());
        b.addAllHeroInfo(create_Sm_UltimateTest_Hero_Info_List(target.getIdToHeros()));
        b.addAllBuffIds(target.getHeroBuffIds());
        if (target.getHardLevel() == null) {
            b.setLastFightHardLevel(HardTypeEnum.NULL);
        } else {
            b.setLastFightHardLevel(target.getHardLevel());
        }
        return b.build();
    }

    public static List<Sm_UltimateTest_Hero_Info> create_Sm_UltimateTest_Hero_Info_List(Map<Integer, UltimateTestHero> heros) {
        List<Sm_UltimateTest_Hero_Info> smUltimateTestHeroInfoList = new ArrayList<>();
        for (Entry<Integer, UltimateTestHero> entry : heros.entrySet()) {
            smUltimateTestHeroInfoList.add(create_Sm_UltimateTest_Hero_Info(entry.getValue()));
        }
        return smUltimateTestHeroInfoList;
    }


    public static Sm_UltimateTest_Hero_Info create_Sm_UltimateTest_Hero_Info(UltimateTestHero hero) {
        Sm_UltimateTest_Hero_Info.Builder b = Sm_UltimateTest_Hero_Info.newBuilder();
        b.setAnger(hero.getAnger());
        b.setHeroId(hero.getHeroId());
        b.setHp(hero.getHp());
        return b.build();
    }

    public static List<Sm_UltimateTest_Buff_Info> create_Sm_UltimateTest_Buff_Info_List(Map<UltimateTestBuffIndexTypeEnum, UltimatetestBuff> buffIndexAndBuff) {
        List<Sm_UltimateTest_Buff_Info> buffInfoList = new ArrayList<>();
        for (Entry<UltimateTestBuffIndexTypeEnum, UltimatetestBuff> entry : buffIndexAndBuff.entrySet()) {
            buffInfoList.add(create_Sm_UltimateTest_Buff_Info(entry));
        }
        return buffInfoList;
    }

    public static Sm_UltimateTest_Buff_Info create_Sm_UltimateTest_Buff_Info(Entry<UltimateTestBuffIndexTypeEnum, UltimatetestBuff> entry) {
        Sm_UltimateTest_Buff_Info.Builder b = Sm_UltimateTest_Buff_Info.newBuilder();
        b.setSkillId(entry.getValue().getBuffId());
        b.setBuffIndex(entry.getKey());
        b.setConsum(entry.getValue().getConsum());
        return b.build();
    }

}
