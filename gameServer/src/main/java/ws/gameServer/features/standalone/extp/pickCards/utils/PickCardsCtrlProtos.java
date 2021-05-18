package ws.gameServer.features.standalone.extp.pickCards.utils;

import ws.protos.PickCardsProtos.Sm_OnePickCard;
import ws.relationship.base.MagicNumbers;
import ws.relationship.topLevelPojos.pickCards.PickCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PickCardsCtrlProtos {


    public static List<Sm_OnePickCard> create_Sm_OnePickCard_Lis(Map<Integer, PickCard> idToPickCard) {
        List<Sm_OnePickCard> bs = new ArrayList<>();
        for (PickCard pickCard : idToPickCard.values()) {
            bs.add(create_Sm_OnePickCard(pickCard).build());
        }
        return bs;
    }

    public static Sm_OnePickCard.Builder create_Sm_OnePickCard(PickCard pickCard) {
        Sm_OnePickCard.Builder b = Sm_OnePickCard.newBuilder();
        b.setId(pickCard.getId());
        b.setUseFreeTimes(pickCard.getUseFreeTimes());
        b.setNextFreeTime(pickCard.getNextFreeTime());

        b.setDaliy1Times(pickCard.getDaliy1Times());
        b.setDaliy10Times(pickCard.getDaliy10Times());
        b.setDaliy100Times(pickCard.getDaliy100Times());
        
        int left = pickCard.getSum1Times() % MagicNumbers.PICKCARD_TEN_PICK_CARD;
        b.setMustOutAfterTimes(MagicNumbers.PICKCARD_TEN_PICK_CARD - left - 1);
        return b;
    }
}
