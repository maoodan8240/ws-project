package ws.relationship.logServer.daos.playerLvUpLog;

import ws.common.mongoDB.interfaces.BaseDao;
import ws.relationship.logServer.pojos.PlayerLvUpLog;

import java.util.List;

/**
 * Created by zww on 8/10/16.
 */
public interface PlayerLvUpLogDao extends BaseDao<PlayerLvUpLog> {
    /**
     * 查询时间范围内玩家的等级情况
     *
     * @param createAtTime
     * @param endDate
     * @return
     */
    List<Integer> findLvByDate(String createAtTime, String endDate, String platformType, int orid);
}
