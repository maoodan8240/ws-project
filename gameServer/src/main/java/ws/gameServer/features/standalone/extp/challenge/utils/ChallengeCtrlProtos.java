package ws.gameServer.features.standalone.extp.challenge.utils;

import ws.protos.ChallengeProtos.Sm_Challenge_StageInfo;
import ws.protos.ChallengeProtos.Sm_Challenge_StageTypeInfo;
import ws.protos.EnumsProtos.ChallengeTypeEnum;
import ws.relationship.topLevelPojos.challenge.Challenge;
import ws.relationship.topLevelPojos.challenge.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class ChallengeCtrlProtos {
    public static List<Sm_Challenge_StageTypeInfo> create_Sm_Challenge_StageTypeInfo_List(Challenge challenge) {
        List<Sm_Challenge_StageTypeInfo> sm_stageTypeInfoList = new ArrayList<>();
        Map<ChallengeTypeEnum, Stage> stageTypeToAttackTimes = challenge.getStageTypeToStage();
        for (Entry<ChallengeTypeEnum, Stage> entry : stageTypeToAttackTimes.entrySet()) {
            sm_stageTypeInfoList.add(create_Sm_Challenge_StageTypeInfo(entry.getKey(), entry.getValue()));
        }
        return sm_stageTypeInfoList;
    }


    public static Sm_Challenge_StageTypeInfo create_Sm_Challenge_StageTypeInfo(ChallengeTypeEnum stageType, Stage stage) {
        Sm_Challenge_StageTypeInfo.Builder b = Sm_Challenge_StageTypeInfo.newBuilder();
        b.setAttackTimes(stage.getAttackTimes());
        b.setStageType(stageType.getNumber());
        b.addAllStageInfo(create_Sm_Challenge_StageInfo_List(stage.getOpenStageIdAndWin()));
        b.setLastAttackTime(stage.getCdTime());
        return b.build();
    }

    public static List<Sm_Challenge_StageInfo> create_Sm_Challenge_StageInfo_List(Map<Integer, Boolean> openStageIdAndWin) {
        List<Sm_Challenge_StageInfo> smChallengeStageInfoList = new ArrayList<>();
        for (Entry<Integer, Boolean> entry : openStageIdAndWin.entrySet()) {
            smChallengeStageInfoList.add(create_Sm_Challenge_StageInfo(entry));
        }
        return smChallengeStageInfoList;

    }

    public static Sm_Challenge_StageInfo create_Sm_Challenge_StageInfo(Entry<Integer, Boolean> entry) {
        Sm_Challenge_StageInfo.Builder b = Sm_Challenge_StageInfo.newBuilder();
        b.setStageId(entry.getKey());
        b.setIsFinish(entry.getValue());
        return b.build();
    }

}
