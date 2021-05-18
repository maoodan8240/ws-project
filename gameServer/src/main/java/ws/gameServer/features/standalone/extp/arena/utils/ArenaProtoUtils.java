package ws.gameServer.features.standalone.extp.arena.utils;


import ws.protos.PvpProtos.Sm_Pvp_Base;
import ws.protos.PvpProtos.Sm_Pvp_Enemy;
import ws.protos.PvpProtos.Sm_Pvp_Record;
import ws.relationship.topLevelPojos.pvp.arena.ArenaBase;
import ws.relationship.topLevelPojos.pvp.arena.ArenaRecord;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.utils.ProtoUtils;

import java.util.ArrayList;
import java.util.List;

public class ArenaProtoUtils {


    public static List<Sm_Pvp_Record> create_Sm_Pvp_Record_List(List<ArenaRecord> records) {
        List<Sm_Pvp_Record> smPvpRecordList = new ArrayList<>();
        for (ArenaRecord record : records) {
            smPvpRecordList.add(create_Sm_Pvp_Record(record));
        }
        return smPvpRecordList;
    }

    public static Sm_Pvp_Record create_Sm_Pvp_Record(ArenaRecord record) {
        Sm_Pvp_Record.Builder b = Sm_Pvp_Record.newBuilder();
        b.setRecordId(record.getRecordId());
        b.setRs(record.isRs());
        b.setTime(record.getTime());
        if (record.isRobot()) {
            b.setIsRobot(record.isRobot());
            b.setRobotId(record.getRobotId());
        } else {
            b.setBattleValue(record.getBattleValue());
            b.setName(record.getName());
            b.setLevel(record.getLevel());
            b.setFirstHandValue(record.getFirstHandValue());
            b.setIcon(record.getIcon());
        }
        return b.build();
    }

    public static Sm_Pvp_Base create_Sm_Pvp_Base(ArenaBase arenaBase) {
        Sm_Pvp_Base.Builder b = Sm_Pvp_Base.newBuilder();
        b.setChallengeTimes(arenaBase.getChallengeTimes());
        b.setCdEndTime(arenaBase.getCdEndTime());
        b.setBuyAttTimes(arenaBase.getBuyChallengeTimes());
        b.setRefreshTimes(arenaBase.getRefreshTimes());
        b.setIntegral(arenaBase.getIntegral());
        b.setMaxRank(arenaBase.getMaxRank());
        return b.build();
    }

    public static Sm_Pvp_Enemy create_Sm_Pvp_Enemy(SimplePlayer simplePlayer) {
        Sm_Pvp_Enemy.Builder b = Sm_Pvp_Enemy.newBuilder();
        b.setIsRobot(false);
        b.setEnemy(ProtoUtils.create_Sm_Common_SimplePlayer_Pvp(simplePlayer));
        return b.build();
    }

    public static Sm_Pvp_Enemy create_Sm_Pvp_Enemy(String robotPlayerId, int robotId, int robotRank) {
        Sm_Pvp_Enemy.Builder b = Sm_Pvp_Enemy.newBuilder();
        b.setIsRobot(true);
        b.setRobotPlayerId(robotPlayerId);
        b.setRobotId(robotId);
        b.setRobotRank(robotRank);
        return b.build();
    }
}
