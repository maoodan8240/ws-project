package ws.analogClient.features.utils.receiveModule;

import ws.analogClient.features.utils.ClientUtils;
import ws.protos.MailProtos.Cm_Mail;
import ws.protos.MailProtos.Sm_Mail;
import ws.protos.MailProtos.Sm_Mail.Action;
import ws.protos.MessageHandlerProtos.Response;

/**
 * Created by lee on 17-4-6.
 */
public class Module_Sm_Mail {

    public static void on_Sm_Mail(Response response) {
        Thread t = new Thread(() -> {
            Sm_Mail smMail = response.getSmMail();
            if (smMail.getAction() == Action.RESP_HAS_NEW_MAIL) {
                try {
                    // 10 秒后再发送同步信息
                    Thread.sleep(10 * 1000l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Cm_Mail.Builder b1 = Cm_Mail.newBuilder();
                b1.setAction(Cm_Mail.Action.SYNC);
                Response response1 = ClientUtils.send(b1.build(), Action.RESP_SYNC);
                if (!response1.getResult()) {
                    throw new RuntimeException("服务器返回失败！！！");
                }
            }
        });
        t.start();

    }
}
