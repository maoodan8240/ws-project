package ws.relationship.daos.arenaCenterRanker;

import ws.common.mongoDB.interfaces.BaseDao;
import ws.relationship.topLevelPojos.pvp.arenaCenter.ArenaCenterRanker;

import java.util.List;

/**
 * Created by lee on 17-3-7.
 */
public interface ArenaCenterRankerDao extends BaseDao<ArenaCenterRanker> {


    /**
     * 根据排名查询
     *
     * @param rank
     * @return
     */
    ArenaCenterRanker queryByRank(int rank);


    /**
     * 查询[排名数值] <= minRank
     *
     * @param minRank
     * @return
     */
    List<ArenaCenterRanker> queryRankTopN_NotRobot(int minRank);


    /**
     * 批量覆盖更新
     *
     * @param rankerList
     */
    void mutliReplace(List<ArenaCenterRanker> rankerList);
}
