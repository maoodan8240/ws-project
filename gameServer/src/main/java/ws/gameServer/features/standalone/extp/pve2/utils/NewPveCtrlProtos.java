package ws.gameServer.features.standalone.extp.pve2.utils;

import ws.protos.NewPveProtos.Sm_NewPve_Chapter;
import ws.protos.NewPveProtos.Sm_NewPve_Stage;
import ws.relationship.topLevelPojos.newPve.Chapter;
import ws.relationship.topLevelPojos.newPve.NewStage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewPveCtrlProtos {

    /**
     * sm关卡
     *
     * @param stage
     * @return
     */
    public static Sm_NewPve_Stage create_Sm_NewPve_Stage(NewStage stage) {
        Sm_NewPve_Stage.Builder b = Sm_NewPve_Stage.newBuilder();
        b.setId(stage.getStageId());
        b.setAttackTimes(stage.getDailyAttackTimes());
        b.setMaxStarLevel(stage.getMaxStar());
        b.setResetTimes(stage.getResetTimes());
        b.setIsOpenBox(stage.isOpenBox());
        return b.build();
    }


    /**
     * sm所有章节list
     *
     * @param chapters
     * @return
     */
    public static List<Sm_NewPve_Chapter> create_Sm_NewPve_Maps_List(List<Chapter> chapters) {
        List<Sm_NewPve_Chapter> bs = new ArrayList<>();
        for (Chapter chapter : chapters) {
            if (chapter.getChapterSumStar() != 0) {
                Sm_NewPve_Chapter b$map = _create_Sm_NewPve_Chapter_allStage(chapter);
                bs.add(b$map);
            }
        }
        return bs;
    }

    private static Sm_NewPve_Chapter _create_Sm_NewPve_Chapter_allStage(Chapter chapter) {
        Sm_NewPve_Chapter.Builder b = Sm_NewPve_Chapter.newBuilder();
        List<NewStage> stageList = _getAllStage(chapter);
        for (NewStage stage : stageList) {
            if (stage.getMaxStar() != 0) {
                b.addStages(create_Sm_NewPve_Stage(stage));
            }
        }
        b.setSumStarLevel(chapter.getChapterSumStar());
        b.setId(chapter.getChapterId());
        b.addAllIdx(new ArrayList<>(chapter.getStarToGetTime().keySet()));
        return b.build();
    }


    private static List<NewStage> _getAllStage(Chapter chapter) {
        List<NewStage> stageList = new ArrayList<>();
        Map<Integer, NewStage> idToStages = chapter.getIdToStage();
        for (Integer stageId : idToStages.keySet()) {
            stageList.add(idToStages.get(stageId));
        }
        return stageList;
    }


    /**
     * sm章节不带关卡
     *
     * @param chapter
     * @return
     */
    public static Sm_NewPve_Chapter create_Sm_NewPve_Chapter_nonStage(Chapter chapter) {
        Sm_NewPve_Chapter.Builder b = Sm_NewPve_Chapter.newBuilder();
        b.setId(chapter.getChapterId());
        List<Integer> starLevelList = new ArrayList<>(chapter.getStarToGetTime().keySet());
        for (Integer starLevelNode : starLevelList) {
            b.addIdx(starLevelNode);
        }
        b.setSumStarLevel(chapter.getChapterSumStar());
        return b.build();
    }
}
