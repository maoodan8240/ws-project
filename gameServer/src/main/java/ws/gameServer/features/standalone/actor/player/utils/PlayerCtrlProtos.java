package ws.gameServer.features.standalone.actor.player.utils;

import ws.gameServer.features.standalone.extp.utils.CommonUtils;
import ws.protos.PlayerProtos.Sm_Player;
import ws.relationship.topLevelPojos.player.Player;

public class PlayerCtrlProtos {

    public static Sm_Player create_Sm_Player(Player player) {
        Sm_Player.Builder b = Sm_Player.newBuilder();
        b.setAction(Sm_Player.Action.RESP_SYNC);
        b.setId(player.getPlayerId());
        b.setSimpleId(player.getBase().getSimpleId());
        b.setName(CommonUtils.converNullToEmpty(player.getBase().getName()));
        b.setSign(CommonUtils.converNullToEmpty(player.getBase().getSign()));
        b.setIconId(player.getBase().getIconId());
        b.setLevel(player.getBase().getLevel());
        b.setOverflowExp(player.getBase().getOverflowExp());
        b.setVipLevel(player.getPayment().getVipLevel());
        b.setVipTotalExp(player.getPayment().getVipTotalExp());
        b.setReNameTimes(player.getOther().getReNameTs());
        return b.build();
    }
}
