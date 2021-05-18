package ws.relationship.topLevelPojos.newGuildCenter;

import org.bson.types.ObjectId;
import ws.protos.EnumsProtos.GuildRedBagTypeEnum;
import ws.relationship.utils.CloneUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by lee on 5/31/17.
 */
public class NewGuildPlayerRedBag extends NewGuildRedBag {
    private static final long serialVersionUID = 3908663295686818523L;
    private String ownerName;
    private long sendTime;
    private String redBagId;

    public NewGuildPlayerRedBag(List<Integer> redBagShare, Map<String, Integer> playerNameAndShare, GuildRedBagTypeEnum redBagTypeEnum, String ownerName, long sendTime) {
        super(redBagShare, playerNameAndShare, redBagTypeEnum);
        this.ownerName = ownerName;
        this.sendTime = sendTime;
        this.redBagId = ObjectId.get().toString();
    }

    public NewGuildPlayerRedBag(List<Integer> redBagShare, Map<String, Integer> playerNameAndShare, GuildRedBagTypeEnum redBagTypeEnum, String ownerName, long sendTime, String redBagId) {
        super(redBagShare, playerNameAndShare, redBagTypeEnum);
        this.ownerName = ownerName;
        this.sendTime = sendTime;
        this.redBagId = redBagId;
    }

    public NewGuildPlayerRedBag() {
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getRedBagId() {
        return redBagId;
    }

    public void setRedBagId(String redBagId) {
        this.redBagId = redBagId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewGuildPlayerRedBag clone() {
        return new NewGuildPlayerRedBag(CloneUtils.cloneIntegerList(getRedBagShare()), CloneUtils.cloneHashMap(getPlayerNameAndShare()), getRedBagTypeEnum(), ownerName, sendTime, redBagId);

    }
}
