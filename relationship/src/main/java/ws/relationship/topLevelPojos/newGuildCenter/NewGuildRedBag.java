package ws.relationship.topLevelPojos.newGuildCenter;

import ws.protos.EnumsProtos.GuildRedBagTypeEnum;
import ws.relationship.base.WsCloneable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lee on 5/24/17.
 */
public abstract class NewGuildRedBag implements Serializable, WsCloneable {
    private static final long serialVersionUID = 7912672782240668719L;
    private List<Integer> redBagShare = new ArrayList<>(); //红包份额
    private Map<String, Integer> playerNameAndShare = new HashMap<>(); //玩家名字对应领取到的红包数量
    private GuildRedBagTypeEnum redBagTypeEnum;

    public NewGuildRedBag() {
    }


    /**
     * for clone
     *
     * @param redBagShare
     * @param playerNameAndShare
     * @param redBagTypeEnum
     */
    public NewGuildRedBag(List<Integer> redBagShare, Map<String, Integer> playerNameAndShare, GuildRedBagTypeEnum redBagTypeEnum) {
        this.redBagShare = redBagShare;
        this.playerNameAndShare = playerNameAndShare;
        this.redBagTypeEnum = redBagTypeEnum;
    }

    public List<Integer> getRedBagShare() {
        return redBagShare;
    }

    public void setRedBagShare(List<Integer> redBagShare) {
        this.redBagShare = redBagShare;
    }

    public Map<String, Integer> getPlayerNameAndShare() {
        return playerNameAndShare;
    }

    public void setPlayerNameAndShare(Map<String, Integer> playerNameAndShare) {
        this.playerNameAndShare = playerNameAndShare;
    }

    public GuildRedBagTypeEnum getRedBagTypeEnum() {
        return redBagTypeEnum;
    }

    public void setRedBagTypeEnum(GuildRedBagTypeEnum redBagTypeEnum) {
        this.redBagTypeEnum = redBagTypeEnum;
    }

    public abstract NewGuildRedBag clone();

}
