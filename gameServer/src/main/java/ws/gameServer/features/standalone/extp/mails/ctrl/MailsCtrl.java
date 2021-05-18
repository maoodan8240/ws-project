package ws.gameServer.features.standalone.extp.mails.ctrl;


import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.topLevelPojos.mails.Mail;
import ws.relationship.topLevelPojos.mails.Mails;

public interface MailsCtrl extends PlayerExteControler<Mails> {

    /**
     * 读取邮件
     *
     * @param mailId
     */
    void read(String mailId);

    /**
     * 领取邮件附件
     *
     * @param mailId
     */
    void getAttachments(String mailId);


    /**
     * 发送游戏内系统邮件
     *
     * @param mailTemplateId
     * @param idMaptoCount   如果该字段有值，则不使用表里的附件，使用该字段
     * @param args
     */
    void addSysMail(int mailTemplateId, IdMaptoCount idMaptoCount, Object... args);

    /**
     * 添加Mail
     *
     * @param mail
     */
    void addMail(Mail mail);


    /**
     * 获取新的邮件
     */
    void toGetNewGmMail();


    /**
     * 获取所有邮件的附件
     */
    void getAllAttachments();
}
