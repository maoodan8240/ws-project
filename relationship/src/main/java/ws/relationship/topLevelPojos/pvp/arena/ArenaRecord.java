package ws.relationship.topLevelPojos.pvp.arena;

import java.io.Serializable;


public class ArenaRecord implements Serializable {
    private static final long serialVersionUID = -4722187372745294777L;
    private String recordId;    // 战报Id
    private boolean rs;         // 战斗结果(相对于我的结果)
    private boolean robot;      // 是否是机器人
    private int robotId;        // 机器人Id
    private String name;        // 名称
    private int level;          // 等级
    private int icon;           // 头像
    private long battleValue;   // 战斗力
    private int firstHandValue; // 先手值
    private long time;          // 战斗发生时间

    public ArenaRecord() {
    }

    /**
     * 被攻击的人为机器人
     *
     * @param recordId
     * @param rs
     * @param robotId
     */
    public ArenaRecord(String recordId, boolean rs, int robotId) {
        this.recordId = recordId;
        this.rs = rs;
        this.robot = true;
        this.robotId = robotId;
        this.time = System.currentTimeMillis();
    }


    /**
     * 被攻击的人为真实玩家
     *
     * @param recordId
     * @param rs
     * @param name
     * @param level
     * @param icon
     * @param battleValue
     * @param firstHandValue
     */
    public ArenaRecord(String recordId, boolean rs, String name, int level, int icon, long battleValue, int firstHandValue) {
        this.robot = false;
        this.recordId = recordId;
        this.rs = rs;
        this.name = name;
        this.level = level;
        this.icon = icon;
        this.battleValue = battleValue;
        this.firstHandValue = firstHandValue;
        this.time = System.currentTimeMillis();
    }


    /**
     * for clone
     *
     * @param recordId
     * @param rs
     * @param robot
     * @param robotId
     * @param name
     * @param level
     * @param icon
     * @param battleValue
     * @param firstHandValue
     * @param time
     */
    public ArenaRecord(String recordId, boolean rs, boolean robot, int robotId, String name, int level, int icon, long battleValue, int firstHandValue, long time) {
        this.recordId = recordId;
        this.rs = rs;
        this.robot = robot;
        this.robotId = robotId;
        this.name = name;
        this.level = level;
        this.icon = icon;
        this.battleValue = battleValue;
        this.firstHandValue = firstHandValue;
        this.time = time;
    }

    public boolean isRs() {
        return rs;
    }

    public void setRs(boolean rs) {
        this.rs = rs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getBattleValue() {
        return battleValue;
    }

    public void setBattleValue(long battleValue) {
        this.battleValue = battleValue;
    }

    public int getFirstHandValue() {
        return firstHandValue;
    }

    public void setFirstHandValue(int firstHandValue) {
        this.firstHandValue = firstHandValue;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isRobot() {
        return robot;
    }

    public void setRobot(boolean robot) {
        this.robot = robot;
    }

    public int getRobotId() {
        return robotId;
    }

    public void setRobotId(int robotId) {
        this.robotId = robotId;
    }

    @Override
    public ArenaRecord clone() {
        return new ArenaRecord(recordId, rs, robot, robotId, name, level,
                icon, battleValue, firstHandValue, time);
    }
}
