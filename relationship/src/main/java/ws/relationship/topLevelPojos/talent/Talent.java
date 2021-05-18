package ws.relationship.topLevelPojos.talent;

import ws.relationship.topLevelPojos.PlayerTopLevelPojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 17-2-4.
 */
public class Talent extends PlayerTopLevelPojo {
    private static final long serialVersionUID = 7266273543297161454L;

    /**
     * 天赋Id对应天赋等级
     */
    private List<Integer> talentLevelIds = new ArrayList<>();


    public Talent() {
    }


    public Talent(String playerId) {
        super(playerId);
    }

    public List<Integer> getTalentLevelIds() {
        return talentLevelIds;
    }

    public void setTalentLevelIds(List<Integer> talentLevelIds) {
        this.talentLevelIds = talentLevelIds;
    }
}
