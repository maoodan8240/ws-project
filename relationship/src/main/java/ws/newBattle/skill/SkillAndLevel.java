package ws.newBattle.skill;

/**
 * Created by zhangweiwei on 17-1-4.
 */
public class SkillAndLevel {
    private int id;
    private int level;

    public SkillAndLevel(int id, int level) {
        this.id = id;
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }
}
