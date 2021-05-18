package ws.relationship.topLevelPojos.player;

import ws.relationship.topLevelPojos.PlayerTopLevelPojo;

public class Player extends PlayerTopLevelPojo {
    private static final long serialVersionUID = 2630217257496628198L;

    private PlayerBase base = new PlayerBase(); // 基本信息
    private PlayerAccount account = new PlayerAccount();// 账户信息
    private PlayerPayment payment = new PlayerPayment();// 支付信息
    private PlayerOther other = new PlayerOther();// 其他信息


    public PlayerBase getBase() {
        return base;
    }

    public void setBase(PlayerBase base) {
        this.base = base;
    }

    public PlayerAccount getAccount() {
        return account;
    }

    public void setAccount(PlayerAccount account) {
        this.account = account;
    }

    public PlayerPayment getPayment() {
        return payment;
    }

    public void setPayment(PlayerPayment payment) {
        this.payment = payment;
    }

    public PlayerOther getOther() {
        return other;
    }

    public void setOther(PlayerOther other) {
        this.other = other;
    }
}
