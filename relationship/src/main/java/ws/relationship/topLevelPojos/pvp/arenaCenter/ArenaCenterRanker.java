package ws.relationship.topLevelPojos.pvp.arenaCenter;

import com.alibaba.fastjson.annotation.JSONField;
import ws.common.mongoDB.interfaces.TopLevelPojo;
import ws.relationship.base.WsCloneable;

public class ArenaCenterRanker implements TopLevelPojo, WsCloneable {
    private static final long serialVersionUID = 3384233556646429388L;
    @JSONField(name = "_id")
    private String playerId;   // id (如果是机器人，这个字段代表的是机器人的Id)
    private int rank;          // 排名:从下标1开始
    private int outerRealmId;  // 显示服
    private int robotId;       // 机器人Id
    private boolean robot;     // 是否是机器人


    public ArenaCenterRanker() {
    }

    /**
     * for player
     *
     * @param playerId
     * @param rank
     * @param outerRealmId
     */
    public ArenaCenterRanker(String playerId, int rank, int outerRealmId) {
        this.playerId = playerId;
        this.rank = rank;
        this.outerRealmId = outerRealmId;
        this.robot = false;
    }

    /**
     * for robot
     *
     * @param playerId
     * @param rank
     * @param outerRealmId
     * @param robotId
     */
    public ArenaCenterRanker(String playerId, int rank, int outerRealmId, int robotId) {
        this.playerId = playerId;
        this.rank = rank;
        this.outerRealmId = outerRealmId;
        this.robotId = robotId;
        this.robot = true;
    }

    /**
     * for clone
     *
     * @param playerId
     * @param rank
     * @param outerRealmId
     * @param robotId
     * @param robot
     */
    public ArenaCenterRanker(String playerId, int rank, int outerRealmId, int robotId, boolean robot) {
        this.playerId = playerId;
        this.rank = rank;
        this.outerRealmId = outerRealmId;
        this.robotId = robotId;
        this.robot = robot;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public boolean isRobot() {
        return robot;
    }

    public void setRobot(boolean robot) {
        this.robot = robot;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getOuterRealmId() {
        return outerRealmId;
    }

    public void setOuterRealmId(int outerRealmId) {
        this.outerRealmId = outerRealmId;
    }

    public int getRobotId() {
        return robotId;
    }

    public void setRobotId(int robotId) {
        this.robotId = robotId;
    }

    @Override
    public String getOid() {
        return this.playerId;
    }

    @Override
    public void setOid(String oid) {
        this.playerId = oid;
    }

    @Override
    public ArenaCenterRanker clone() {
        return new ArenaCenterRanker(this.playerId, this.rank, this.outerRealmId, this.robotId, this.robot);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("rank:").append(rank)
                .append(" playerId:").append(playerId)
                .append(" outerRealmId:").append(outerRealmId)
                .append(" ").append(robot);
        return sb.toString();
    }

}
