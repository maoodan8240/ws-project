package ws.gameServer.features.standalone.extp.mails.utils;

import ws.relationship.topLevelPojos.mails.Mail;
import ws.relationship.topLevelPojos.mails.Mails;
import ws.relationship.utils.DBUtils;

/**
 * Created by zhangweiwei on 17-4-5.
 */
public class MailsCtrlOfflineUtils {

    /**
     * 添加离线邮件
     *
     * @param playerId
     * @param outerRealmId
     * @param mail
     */
    public static void addMail(String playerId, int outerRealmId, Mail mail) {
        mail = MailsCtrlUtils.cloneMail(mail); // 此处clone
        Mails mails = DBUtils.getHashPojo(playerId, outerRealmId, Mails.class);
        MailsCtrlUtils.addMail(mails, mail);
        DBUtils.saveHashPojo(outerRealmId, mails);
    }
}
