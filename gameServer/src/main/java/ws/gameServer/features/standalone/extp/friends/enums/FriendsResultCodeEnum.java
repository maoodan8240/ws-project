package ws.gameServer.features.standalone.extp.friends.enums;

import ws.common.utils.message.interfaces.ResultCode;

public enum FriendsResultCodeEnum implements ResultCode {
    
    REACH_MAX_FRIENDS_NUM(10101, "已经达到最大好友数量"),

    //
    ;


    private int value;
    private String message;

    FriendsResultCodeEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int getCode() {
        return value;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
