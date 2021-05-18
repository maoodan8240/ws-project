package ws.gameServer.features.standalone.extp.mails.utils;

import org.bson.types.ObjectId;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_Mails_Row;
import ws.relationship.topLevelPojos.mailCenter.GmMail;
import ws.relationship.topLevelPojos.mails.Mail;
import ws.relationship.topLevelPojos.mails.Mails;
import ws.relationship.topLevelPojos.mails.SysMail;
import ws.relationship.utils.RelationshipCommonUtils;

import java.util.Objects;

public class MailsCtrlUtils {

    public static Mail cloneMail(Mail mail) {
        if (mail instanceof GmMail) {
            return ((GmMail) mail).clone();
        } else if (mail instanceof SysMail) {
            return ((SysMail) mail).clone();
        }
        throw new RuntimeException("不支持的Mail类型:" + mail.getClass().toString());
    }

    public static void addMail(Mails mails, Mail mail) {
        Objects.requireNonNull(mail, "新增邮件mail不能为空！");
        mail = cloneMail(mail);
        mail.setMailId(ObjectId.get().toString());
        if (mail instanceof GmMail) {
            mails.getIdToGmMail().put(mail.getMailId(), (GmMail) mail);
        } else if (mail instanceof SysMail) {
            mails.getIdToSysMail().put(mail.getMailId(), (SysMail) mail);
        }
    }


    public static GmMail createGmMails(String title, String content, String senderName) {
        LogicCheckUtils.validateParam(String.class, title);
        LogicCheckUtils.validateParam(String.class, content);
        LogicCheckUtils.validateParam(String.class, senderName);
        GmMail gmMail = new GmMail();
        gmMail.setMailId(ObjectId.get().toString());
        gmMail.setSenderName(senderName);
        gmMail.setTitle(title);
        gmMail.setContent(content);
        gmMail.setHasRead(false);
        RelationshipCommonUtils.setSendTimeAndExpiredTime(gmMail);
        return gmMail;
    }


    /**
     * 构建系统内邮件
     *
     * @param tpId
     * @param idMaptoCount
     * @param args
     * @return
     */
    public static SysMail createSysMail(int tpId, IdMaptoCount idMaptoCount, Object... args) {
        Table_Mails_Row mailsRow = RootTc.get(Table_Mails_Row.class, tpId);
        LogicCheckUtils.requireNonNullForTable(mailsRow, Table_Mails_Row.class, tpId);
        SysMail m = new SysMail();
        m.setMailId(ObjectId.get().toString());
        m.setTpId(tpId);
        m.setHasRead(false);
        m.setDeleteAfterRead(mailsRow.getIsReadedDelete());
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                m.getArgs().add(arg.toString());
            }
        }
        if (idMaptoCount != null && idMaptoCount.getAll().size() > 0) {
            m.setHasAttachments(true);
            m.setAttachments(idMaptoCount.getAll(1));
        } else {
            m.setHasAttachments(mailsRow.getAttachments().size() > 0);
            m.setAttachments(mailsRow.getAttachments());
        }
        RelationshipCommonUtils.setSendTimeAndExpiredTime(m);
        return m;
    }

}
