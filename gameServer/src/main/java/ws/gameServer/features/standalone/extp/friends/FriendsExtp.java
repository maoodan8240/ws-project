package ws.gameServer.features.standalone.extp.friends;


import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.friends.ctrl.FriendsCtrl;
import ws.gameServer.features.standalone.extp.friends.msg.In_AgreeApply;
import ws.gameServer.features.standalone.extp.friends.msg.In_ApplyFriend;
import ws.gameServer.features.standalone.extp.friends.msg.In_DelFriend;
import ws.gameServer.features.standalone.extp.friends.msg.In_GiveFriendEnergy;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.FriendProtos.Cm_Friend;
import ws.protos.FriendProtos.Cm_Friend.Action;
import ws.protos.FriendProtos.Sm_Friend;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.topLevelPojos.friends.Friends;
import ws.relationship.utils.ProtoUtils;


public class FriendsExtp extends AbstractPlayerExtension<FriendsCtrl> {
    public static boolean useExtension = true;

    public FriendsExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(FriendsCtrl.class, Friends.class);
    }

    @Override
    public void _initReference() throws Exception {
        getControlerForQuery()._initReference();
    }

    @Override
    public void _postInit() throws Exception {
        getControlerForQuery()._initAll();
    }

    @Override
    public void onRecvMyNetworkMsg(Message clientMsg) throws Exception {
        if (clientMsg instanceof Cm_Friend) {
            Cm_Friend cm = (Cm_Friend) clientMsg;
            onCm_Friend(cm);
        }
    }

    @Override
    public void onRecvInnerMsg(InnerMsg innerMsg) throws Exception {
        if (innerMsg instanceof In_PlayerLoginedRequest) {
            onPlayerLogined((In_PlayerLoginedRequest) innerMsg);
        } else if (innerMsg instanceof In_DayChanged) {
            onDayChanged();
        } else if (innerMsg instanceof In_AgreeApply.Request) {
            onIn_AgreeApply((In_AgreeApply.Request) innerMsg);
        } else if (innerMsg instanceof In_ApplyFriend.Request) {
            onIn_ApplyFriend((In_ApplyFriend.Request) innerMsg);
        } else if (innerMsg instanceof In_DelFriend.Request) {
            onIn_DelFriend((In_DelFriend.Request) innerMsg);
        } else if (innerMsg instanceof In_GiveFriendEnergy.Request) {
            onIn_GiveFriendEnergy((In_GiveFriendEnergy.Request) innerMsg);
        }
    }

    private void onIn_GiveFriendEnergy(In_GiveFriendEnergy.Request request) {
        getControlerForQuery().onGiveFriendEnergy(request);
    }

    private void onIn_DelFriend(In_DelFriend.Request request) {
        getControlerForQuery().onDelFriend(request);
    }

    private void onIn_ApplyFriend(In_ApplyFriend.Request request) {
        getControlerForQuery().onApplyFriend(request);
    }

    private void onIn_AgreeApply(In_AgreeApply.Request request) {
        getControlerForQuery().onAgreeApply(request);
    }

    @Override
    public void onRecvPrivateMsg(PrivateMsg privateMsg) throws Exception {
    }

    private void onCm_Friend(Cm_Friend cm) throws Exception {
        Sm_Friend.Builder b = Sm_Friend.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_Friend.Action.SYNC_VALUE:
                    b.setAction(Sm_Friend.Action.RESP_SYNC);
                    sync();
                    break;
                case Cm_Friend.Action.ADD_VALUE:
                    b.setAction(Sm_Friend.Action.RESP_ADD);
                    add(cm);
                    break;
                case Action.AGREE_VALUE:
                    b.setAction(Sm_Friend.Action.RESP_AGREE);
                    agree(cm);
                    break;
                case Action.REFUSE_VALUE:
                    b.setAction(Sm_Friend.Action.RESP_REFUSE);
                    refuse(cm);
                    break;
                case Action.DELETE_VALUE:
                    b.setAction(Sm_Friend.Action.RESP_DELETE);
                    delete(cm);
                    break;
                case Action.ANOTHER_VALUE:
                    b.setAction(Sm_Friend.Action.RESP_ANOTHER);
                    another(cm);
                    break;
                case Action.GIVE_VALUE:
                    b.setAction(Sm_Friend.Action.RESP_GIVE);
                    give(cm);
                    break;
                case Action.GET_FRIEND_VALUE:
                    b.setAction(Sm_Friend.Action.RESP_GET_FRIEND);
                    getFriend(cm);
                    break;
                case Action.GET_APPLY_VALUE:
                    b.setAction(Sm_Friend.Action.RESP_GET_APPLY);
                    getApply(cm);
                    break;
                case Action.GET_ENERGY_VALUE:
                    b.setAction(Sm_Friend.Action.RESP_GET_ENERGY);
                    getEnergy(cm);
                    break;
                case Action.SEARCH_VALUE:
                    b.setAction(Sm_Friend.Action.RESP_SEARCH);
                    search(cm);
                    break;
                case Action.GET_DEL_FRIEND_VALUE:
                    b.setAction(Sm_Friend.Action.RESP_GET_DEL_FRIEND);
                    getDelFriend(cm);
                    break;
                case Action.ONE_KEY_GIVE_VALUE:
                    b.setAction(Sm_Friend.Action.RESP_ONE_KEY_GIVE);
                    oneKeyGive(cm);
                    break;
                case Action.ONE_KEY_GET_VALUE:
                    b.setAction(Sm_Friend.Action.RESP_ONE_KEY_GET);
                    oneKeyGet(cm);
                    break;
            }
        } catch (BusinessLogicMismatchConditionException e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_Friend, b.getAction(), e.getErrorCodeEnum());
            getControlerForQuery().send(br.build());
            throw e;
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_Friend, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
    }

    private void give(Cm_Friend cm) {
        getControlerForQuery().giveEnergy(cm.getPlayerId());
    }

    private void getFriend(Cm_Friend cm) {
        getControlerForQuery().queryFriends(cm.getRound().getMin(), cm.getRound().getMax());
    }

    private void getApply(Cm_Friend cm) {
        getControlerForQuery().queryApplyLis(cm.getRound().getMin(), cm.getRound().getMax());
    }

    private void getEnergy(Cm_Friend cm) {
        getControlerForQuery().getEnergy(cm.getPlayerId());
    }

    private void search(Cm_Friend cm) {
        getControlerForQuery().search(cm.getSearchCondition());
    }

    private void getDelFriend(Cm_Friend cm) {
        getControlerForQuery().queryFriendsForDel(cm.getRound().getMin(), cm.getRound().getMax());
    }

    private void oneKeyGive(Cm_Friend cm) {
        getControlerForQuery().oneKeyGiveEnergy();
    }

    private void oneKeyGet(Cm_Friend cm) {
        getControlerForQuery().oneKeyGetEnergy();
    }

    private void another(Cm_Friend cm) {
        getControlerForQuery().recommendFriends();
    }

    private void delete(Cm_Friend cm) {
        getControlerForQuery().del(cm.getPlayerIdLisList());
    }

    private void refuse(Cm_Friend cm) {
        getControlerForQuery().refuse(cm.getPlayerIdLisList());
    }

    private void agree(Cm_Friend cm) {
        getControlerForQuery().agree(cm.getPlayerIdLisList());
    }

    private void add(Cm_Friend cm) {
        getControlerForQuery().apply(cm.getPlayerIdLisList());
    }

    private void onPlayerLogined(In_PlayerLoginedRequest im_PlayerLogined) throws Exception {
        if (im_PlayerLogined.getPlayerId().equals(ownerCtrl.getTarget().getPlayerId())) {
            // 本玩家, 同步信息
            sync();
        }
    }

    private void onDayChanged() throws Exception {
        getControlerForQuery()._resetDataAtDayChanged();
        getControlerForQuery().sync();
    }

    private void sync() {
        getControlerForQuery().sync();
    }
}
