package ws.gameServer.features.standalone.extp.pickCards.utils;

import ws.relationship.topLevelPojos.pickCards.PickCard;
import ws.relationship.topLevelPojos.pickCards.PickCards;

public class PickCardsCtrlUtils {


    /**
     * 计算每日抽卡次数
     *
     * @param pickCards
     * @param pickId
     * @return
     */
    public static int calcuDaliyPickTimes(PickCards pickCards, int pickId) {
        int sum = 0;
        if (containsPickCard(pickCards, pickId)) {
            sum += getPickCard(pickCards, pickId).getDaliy1Times();
            sum += getPickCard(pickCards, pickId).getDaliy10Times();
            sum += getPickCard(pickCards, pickId).getDaliy100Times();
        }
        return sum;
    }


    public static PickCard getPickCard(PickCards pickCards, int pickId) {
        return pickCards.getIdToPickCard().get(pickId);
    }

    public static boolean containsPickCard(PickCards pickCards, int pickId) {
        return pickCards.getIdToPickCard().containsKey(pickId);
    }
}
