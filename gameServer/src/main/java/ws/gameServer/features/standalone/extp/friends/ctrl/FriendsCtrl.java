package ws.gameServer.features.standalone.extp.friends.ctrl;


import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.gameServer.features.standalone.extp.friends.msg.In_AgreeApply;
import ws.gameServer.features.standalone.extp.friends.msg.In_ApplyFriend;
import ws.gameServer.features.standalone.extp.friends.msg.In_DelFriend;
import ws.gameServer.features.standalone.extp.friends.msg.In_GiveFriendEnergy;
import ws.relationship.topLevelPojos.friends.Friends;

import java.util.List;

public interface FriendsCtrl extends PlayerExteControler<Friends> {

    /**
     * 获取好友
     *
     * @param start 下标从0开始
     * @param end   下标从0开始
     */
    void queryFriends(int start, int end);

    /**
     * 获取好友，删除好友时用
     *
     * @param start 下标从0开始
     * @param end   下标从0开始
     */
    void queryFriendsForDel(int start, int end);

    /**
     * 申请添加好友
     *
     * @param playerIdLis
     */
    void apply(List<String> playerIdLis);

    /**
     * 同意好友
     *
     * @param playerIdLis
     */
    void agree(List<String> playerIdLis);

    /**
     * 拒绝申请
     *
     * @param playerIdLis
     */
    void refuse(List<String> playerIdLis);

    /**
     * 删除好友
     *
     * @param playerIdLis
     */
    void del(List<String> playerIdLis);

    /**
     * 推荐好友
     */
    void recommendFriends();


    /**
     * 赠送体力
     *
     * @param targetPlayerId
     */
    void giveEnergy(String targetPlayerId);

    /**
     * 一键赠送体力
     */
    void oneKeyGiveEnergy();

    /**
     * 领取体力
     *
     * @param targetPlayerId
     */
    void getEnergy(String targetPlayerId);

    /**
     * 一键领取体力
     */
    void oneKeyGetEnergy();


    /**
     * 搜索玩家
     *
     * @param condition
     */
    void search(String condition);

    /**
     * 获取申请列表
     *
     * @param start 下标从0开始
     * @param end   下标从0开始
     */
    void queryApplyLis(int start, int end);


    /**
     * 对面的玩家同意了申请
     *
     * @param request
     */
    void onAgreeApply(In_AgreeApply.Request request);

    /**
     * 接收到了别的玩家申请
     *
     * @param request
     */
    void onApplyFriend(In_ApplyFriend.Request request);

    /**
     * 对面的玩家删除了好友关系
     *
     * @param request
     */
    void onDelFriend(In_DelFriend.Request request);

    /**
     * 对面的好友赠了体力
     *
     * @param request
     */
    void onGiveFriendEnergy(In_GiveFriendEnergy.Request request);
}
