package ws.analogClient.features.flow.functions.mails;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.MailProtos.Cm_Mail;
import ws.protos.MailProtos.Cm_Mail.Action;
import ws.protos.MailProtos.Sm_Mail;
import ws.protos.MailProtos.Sm_Mail_Info;
import ws.protos.MessageHandlerProtos.Response;

public class Func_Mails_Get_Attachments {

    public static void execute() {
        test2();
    }

    /**
     * 测试点说明：
     * 第一次：正常领取附件
     * 第二次：邮件已经被领取过了 mailId=58e5d7ec5a542936d1fdb871
     */
    public static void test1() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1000001:1";
        String resource_2 = "1000001:2";

        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        Cm_Mail.Builder b2 = Cm_Mail.newBuilder();
        b2.setAction(Cm_Mail.Action.SYNC);
        Response response2 = ClientUtils.send(b2.build(), Sm_Mail.Action.RESP_SYNC);
        if (!response2.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
        String mailId = "";
        // 获取有附件的邮件Id
        for (Sm_Mail_Info info : response2.getSmMail().getMailsInfoList()) {
            if (info.hasAttachments()) {
                mailId = info.getMailId();
            }
        }
        for (int i = 0; i < 2; i++) {
            Cm_Mail.Builder b1 = Cm_Mail.newBuilder();
            b1.setAction(Action.GET_ATTACHMENTS);
            b1.setMailId(mailId);
            Response response1 = ClientUtils.send(b1.build(), Sm_Mail.Action.RESP_GET_ATTACHMENTS);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }

    /**
     * 测试点说明：
     * 邮件没有附件无法领取 mailId=58e5d88e5a54293ac88bbe41
     */
    public static void test2() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1000001:1";
        String resource_2 = "1000001:2";

        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        Cm_Mail.Builder b2 = Cm_Mail.newBuilder();
        b2.setAction(Cm_Mail.Action.SYNC);
        Response response2 = ClientUtils.send(b2.build(), Sm_Mail.Action.RESP_SYNC);
        if (!response2.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
        String mailId = "";
        // 获取有附件的邮件Id
        for (Sm_Mail_Info info : response2.getSmMail().getMailsInfoList()) {
            if (!info.hasAttachments()) {
                mailId = info.getMailId();
            }
        }
        Cm_Mail.Builder b1 = Cm_Mail.newBuilder();
        b1.setAction(Action.GET_ATTACHMENTS);
        b1.setMailId(mailId);
        Response response1 = ClientUtils.send(b1.build(), Sm_Mail.Action.RESP_GET_ATTACHMENTS);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }
}

