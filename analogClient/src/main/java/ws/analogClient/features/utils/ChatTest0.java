package ws.analogClient.features.utils;

import ws.analogClient.system.config.AppConfig;
import ws.analogClient.system.di.GlobalInjectorUtils;
import ws.protos.ChatProtos.Cm_Chat;
import ws.protos.ChatProtos.Cm_ChatServerLogin;
import ws.protos.ChatProtos.Cm_ChatServerLogin.Action;
import ws.protos.ChatProtos.Sm_Chat;
import ws.protos.ChatProtos.Sm_ChatServerLogin;
import ws.protos.EnumsProtos.ChatTypeEnum;

/**
 * Created by zhangweiwei on 17-5-5.
 */
public class ChatTest0 {

    public static void login() {
        Cm_ChatServerLogin.Builder b = Cm_ChatServerLogin.newBuilder();
        b.setSecurityCode("1i/ZNrlrhA+k1riUwjY0L/O4vZsK/7oFHCD3/I/ukXI3g7QTKrlcHE6aFniiakWqYLPGm9s6IK1oi\ncqprlUT/8CfgcD/GlfFoYzaWFjnY3chFJ+kmHKrlaV3YpFFqKaQGSmRXHbS97zkPNEWxIR44cYAj\nyqfDMZGNfXxlmKd/gV8=");
        b.setAction(Action.AUTH);
        ChatClientUtils.send(b.build(), Sm_ChatServerLogin.Action.RESP_AUTH);
    }

    public static void sendrelationMsg() {
        try {
            Thread.sleep(10 * 1000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 591019c9784ba354f2eba0a3 1000023  zwwab4
        Cm_Chat.Builder b = Cm_Chat.newBuilder();
        b.setAction(Cm_Chat.Action.SEND);
        b.setType(ChatTypeEnum.CHAT_PRIVATE);
        b.setChatTargetId("591019e1784ba354f2eba0a6"); //
        b.setUseHorn(false);
        b.setContent("测试聊天啦啦啦..... gen relationship >>> ");
        ChatClientUtils.send(b.build(), Sm_Chat.Action.RESP_SEND);
        try {
            Thread.sleep(10000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Cm_Chat.Builder b1 = Cm_Chat.newBuilder();
        b1.setAction(Cm_Chat.Action.SYNC);
        ChatClientUtils.send(b1.build(), Sm_Chat.Action.RESP_SYNC);
    }

    public static void sendMsg() {
        try {
            Thread.sleep(10 * 1000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 591019c9784ba354f2eba0a3 1000023  zwwab4
        for (int i = 0; i < 3; i++) {
            Cm_Chat.Builder b = Cm_Chat.newBuilder();
            b.setAction(Cm_Chat.Action.SEND);
            b.setType(ChatTypeEnum.CHAT_PRIVATE);
            b.setChatTargetId("591019e1784ba354f2eba0a6"); //
            b.setRelationId(1494244052892l);
            b.setUseHorn(false);
            b.setContent("测试聊天啦啦啦..... 000000000000000 >>> " + i);
            ChatClientUtils.send(b.build(), Sm_Chat.Action.RESP_SEND);
            try {
                Thread.sleep(10000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Cm_Chat.Builder b = Cm_Chat.newBuilder();
        b.setAction(Cm_Chat.Action.SYNC);
        ChatClientUtils.send(b.build(), Sm_Chat.Action.RESP_SYNC);
    }

    public static void sendMsg2() {
        for (int i = 0; i < 3; i++) {
            Cm_Chat.Builder b = Cm_Chat.newBuilder();
            b.setAction(Cm_Chat.Action.SEND);
            b.setType(ChatTypeEnum.CHAT_WORLD);
            b.setUseHorn(true);
            b.setContent("世界聊天测试..... aaaaaaaaaaaaaa >>> " + i);
            ChatClientUtils.send(b.build(), Sm_Chat.Action.RESP_SEND);
        }

    }

    public static void sync() {
        Cm_Chat.Builder b = Cm_Chat.newBuilder();
        b.setAction(Cm_Chat.Action.SYNC);
        ChatClientUtils.send(b.build(), Sm_Chat.Action.RESP_SYNC);
    }

    public static void main(String[] args) throws Exception {
        AppConfig.init();
        GlobalInjectorUtils.init();
        login();
        // Thread.sleep(2 * 1000);
        // login();
        // sendMsg2();
    }

}
