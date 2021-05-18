package ws.analogClient.features.flow.functions.mails;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.MailProtos.Cm_Mail;
import ws.protos.MailProtos.Cm_Mail.Action;
import ws.protos.MailProtos.Sm_Mail;
import ws.protos.MessageHandlerProtos.Response;

public class Func_Mails_Read {

    public static void execute() {
        test3();
    }

    /**
     * 需求：提前发送邮件给玩家  然后填写MailId
     * 测试点说明： 阅读邮件
     */
    public static void test1() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1000001:1";
        String resource_2 = "1000001:2";

        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        Cm_Mail.Builder b1 = Cm_Mail.newBuilder();
        b1.setAction(Action.READ);
        b1.setMailId("58e5c9985a542924ad0d28e0");
        Response response1 = ClientUtils.send(b1.build(), Sm_Mail.Action.RESP_READ);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 需求：提前发送邮件给玩家  然后填写MailId
     * 测试点说明：第一次正常读取，二次失败
     * 读取的邮件是已读邮件 mailId=58e5c91b5a542924ad0d28df
     */
    public static void test2() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1000001:1";
        String resource_2 = "1000001:2";

        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 2; i++) {
            Cm_Mail.Builder b1 = Cm_Mail.newBuilder();
            b1.setAction(Action.READ);
            b1.setMailId("58e5c91b5a542924ad0d28df");
            Response response1 = ClientUtils.send(b1.build(), Sm_Mail.Action.RESP_READ);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }

    /**
     * 测试点说明： 阅读邮件
     * 不存在的邮件ID mailId=sfsdfasfasdf
     */
    public static void test3() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1000001:1";
        String resource_2 = "1000001:2";

        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        Cm_Mail.Builder b1 = Cm_Mail.newBuilder();
        b1.setAction(Action.READ);
        b1.setMailId("sfsdfasfasdf");
        Response response1 = ClientUtils.send(b1.build(), Sm_Mail.Action.RESP_READ);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }
}

