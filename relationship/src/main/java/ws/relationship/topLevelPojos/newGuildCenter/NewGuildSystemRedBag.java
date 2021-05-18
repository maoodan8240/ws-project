package ws.relationship.topLevelPojos.newGuildCenter;

import ws.protos.EnumsProtos.GuildRedBagTypeEnum;
import ws.relationship.utils.CloneUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by lee on 5/31/17.
 */
public class NewGuildSystemRedBag extends NewGuildRedBag   {
    private static final long serialVersionUID = -8516939723658183545L;


    public NewGuildSystemRedBag() {
    }

    /**
     * for clone
     *
     * @param redBagShare
     * @param playerNameAndShare
     * @param redBagTypeEnum
     */
    public NewGuildSystemRedBag(List<Integer> redBagShare, Map<String, Integer> playerNameAndShare, GuildRedBagTypeEnum redBagTypeEnum) {
        super(redBagShare, playerNameAndShare, redBagTypeEnum);
    }


    @SuppressWarnings("unchecked")
    @Override
    public NewGuildSystemRedBag clone() {
        return new NewGuildSystemRedBag(CloneUtils.cloneIntegerList(getRedBagShare()), CloneUtils.cloneHashMap(getPlayerNameAndShare()), getRedBagTypeEnum());
    }

}
