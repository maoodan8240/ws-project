package ws.gameServer.features.actor.register.utils.create.ext;


import ws.gameServer.features.actor.register.utils.create.ext.base.ExtCommonData;
import ws.gameServer.features.actor.register.utils.create.ext.base.ExtensionIniter;
import ws.relationship.topLevelPojos.mails.Mails;

//邮件
public class _555_MailsExtIniter implements ExtensionIniter {
    @Override
    public void init(ExtCommonData commonData) throws Exception {
        Mails mails = new Mails(commonData.getPlayer().getPlayerId());
        commonData.addPojo(mails);
    }
}
