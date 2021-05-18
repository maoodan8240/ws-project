package ws.gameServer.features.standalone.extp.friends.utils;

import ws.protos.CommonProtos.Sm_Common_SimplePlayer_Base;
import ws.protos.FriendProtos.Sm_Friend_Player;
import ws.relationship.topLevelPojos.friends.Friend;

public class FriendsCtrlProtos {


    public static Sm_Friend_Player create_Sm_Friend_Player(Sm_Common_SimplePlayer_Base base, Friend friend, int index) {
        Sm_Friend_Player.Builder b = Sm_Friend_Player.newBuilder();
        b.setSimePlayer(base);
        b.setGive(friend.isGive());
        b.setCanGet(FriendsCtrlUtils.canGetEnergy(friend));
        b.setIndex(index);
        return b.build();
    }

    /**
     * 不含体力信息
     *
     * @param base
     * @param index
     * @return
     */
    public static Sm_Friend_Player create_Sm_Friend_Player(Sm_Common_SimplePlayer_Base base, int index) {
        Sm_Friend_Player.Builder b = Sm_Friend_Player.newBuilder();
        b.setSimePlayer(base);
        b.setIndex(index);
        return b.build();
    }


    /**
     * 不带索引
     *
     * @param base
     * @param friend
     * @return
     */
    public static Sm_Friend_Player create_Sm_Friend_Player(Sm_Common_SimplePlayer_Base base, Friend friend) {
        Sm_Friend_Player.Builder b = Sm_Friend_Player.newBuilder();
        b.setSimePlayer(base);
        b.setGive(friend.isGive());
        b.setCanGet(FriendsCtrlUtils.canGetEnergy(friend));
        return b.build();
    }


}
