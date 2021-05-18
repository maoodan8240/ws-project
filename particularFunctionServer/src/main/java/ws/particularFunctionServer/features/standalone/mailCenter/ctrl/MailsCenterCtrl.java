package ws.particularFunctionServer.features.standalone.mailCenter.ctrl;

import akka.actor.ActorContext;
import ws.common.utils.mc.controler.Controler;
import ws.relationship.base.msg.mail.In_SyncGmMail;
import ws.relationship.topLevelPojos.mailCenter.GmMail;
import ws.relationship.topLevelPojos.mailCenter.MailsCenter;

import java.util.List;


public interface MailsCenterCtrl extends Controler<MailsCenter> {
    /**
     * 设置actor上下文
     *
     * @param actorContext
     */
    void setActorContext(ActorContext actorContext);

    /**
     * 同步邮件
     *
     * @param request
     * @return
     */
    List<GmMail> syncGmMail(In_SyncGmMail.Request request);


    /**
     * 添加新的GmMail
     */
    void addNewGmMail(GmMail gmMail);
}
