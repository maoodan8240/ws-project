package ws.gameServer.features.standalone.extp.signin.ctrl;


import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.relationship.topLevelPojos.signin.Signin;

public interface SigninCtrl extends PlayerExteControler<Signin> {

    /**
     * 签到
     */
    void signin();

    /**
     * 领取累计签到奖励
     */
    void comulativeReward();

    /**
     * vip补签到
     */
    void addVipSignin();
}
