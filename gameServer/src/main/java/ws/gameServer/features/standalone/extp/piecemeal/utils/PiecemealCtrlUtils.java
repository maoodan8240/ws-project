package ws.gameServer.features.standalone.extp.piecemeal.utils;

import ws.relationship.base.RedisRankAndScore;

import java.util.ArrayList;
import java.util.List;

public class PiecemealCtrlUtils {


    /**
     * 获取排行成员的Id集合
     *
     * @param rankAndScoreList
     * @return
     */
    public static List<String> memberIds(List<RedisRankAndScore> rankAndScoreList) {
        List<String> memberIds = new ArrayList<>();
        for (RedisRankAndScore rankAndScore : rankAndScoreList) {
            memberIds.add(rankAndScore.getMember());
        }
        return memberIds;
    }
}
