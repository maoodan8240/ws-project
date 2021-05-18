package ws.relationship.topLevelPojos.pickCards;

import ws.relationship.topLevelPojos.PlayerTopLevelPojo;

import java.util.HashMap;
import java.util.Map;

public class PickCards extends PlayerTopLevelPojo {
    private static final long serialVersionUID = 9129362770240854262L;

    private Map<Integer, PickCard> idToPickCard = new HashMap<>(); // id - 抽卡项

    public PickCards() {

    }

    public PickCards(String playerId) {
        super(playerId);
    }

    public Map<Integer, PickCard> getIdToPickCard() {
        return idToPickCard;
    }

    public void setIdToPickCard(Map<Integer, PickCard> idToPickCard) {
        this.idToPickCard = idToPickCard;
    }

}
