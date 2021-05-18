package ws.analogClient.features.flow.functions.mails;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.MailProtos.Cm_Mail;
import ws.protos.MailProtos.Sm_Mail;
import ws.protos.MessageHandlerProtos.Response;

public class Func_Mails_Sync {

    public static void execute() {
        test1();
    }

    /**
     * 测试点说明： 同步
     */
    public static void test1() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------

        // -------------------------  功能测试 ---------------------------
        Cm_Mail.Builder b1 = Cm_Mail.newBuilder();
        b1.setAction(Cm_Mail.Action.SYNC);
        Response response1 = ClientUtils.send(b1.build(), Sm_Mail.Action.RESP_SYNC);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

}

