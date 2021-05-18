package ws.relationship.topLevelPojos.newPve;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Created by leetony on 16-11-3.
 */
public class Chapter implements Serializable {
    private static final long serialVersionUID = 4926985771852556617L;
    private int chapterId;
    /**
     * 章节总星数
     */
    private int chapterSumStar;
    /**
     * 该章节已经打过的关卡
     */
    private LinkedHashMap<Integer, NewStage> idToStage = new LinkedHashMap<>();
    /**
     * 星级宝箱对应领取时间
     */
    private LinkedHashMap<Integer, Long> starToGetTime = new LinkedHashMap<>();

    public Chapter() {
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getChapterSumStar() {
        return chapterSumStar;
    }

    public void setChapterSumStar(int chapterSumStar) {
        this.chapterSumStar = chapterSumStar;
    }

    public LinkedHashMap<Integer, NewStage> getIdToStage() {
        return idToStage;
    }

    public void setIdToStage(LinkedHashMap<Integer, NewStage> idToStage) {
        this.idToStage = idToStage;
    }

    public LinkedHashMap<Integer, Long> getStarToGetTime() {
        return starToGetTime;
    }

    public void setStarToGetTime(LinkedHashMap<Integer, Long> starToGetTime) {
        this.starToGetTime = starToGetTime;
    }
}
