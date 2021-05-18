package ws.gameServer.features.standalone.extp.mails.ctrl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.date.WsDateFormatEnum;
import ws.common.utils.date.WsDateUtils;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.mails.utils.MailsCtrlProtos;
import ws.gameServer.features.standalone.extp.mails.utils.MailsCtrlUtils;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.MailProtos.Sm_Mail;
import ws.protos.MailProtos.Sm_Mail.Action;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.ServerRoleEnum;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.mail.In_SyncGmMail;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.topLevelPojos.mailCenter.GmMail;
import ws.relationship.topLevelPojos.mails.Mail;
import ws.relationship.topLevelPojos.mails.Mails;
import ws.relationship.topLevelPojos.mails.SysMail;
import ws.relationship.utils.ActorMsgSynchronizedExecutor;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class _MailsCtrl extends AbstractPlayerExteControler<Mails> implements MailsCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_MailsCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
        _deleteHasReadMails();
    }

    @Override
    public void sync() {
        _deleteExpiredMails();
        _getNewGmMail();
        SenderFunc.sendInner(this, Sm_Mail.class, Sm_Mail.Builder.class, Sm_Mail.Action.RESP_SYNC, (b, br) -> {
            b.addAllMailsInfo(MailsCtrlProtos.create_Sm_Mail_InfoList(target));
        });
        save();
    }


    @Override
    public void read(String mailId) {
        LogicCheckUtils.validateParam(String.class, mailId);
        _logicCheck_IsMailExist(mailId);
        _logicCheck_IsMailExpire(mailId);
        Mail mail = _getMail(mailId);
        if (mail.isHasRead()) {
            String msg = String.format("读取的邮件是已读邮件 mailId=%s", mailId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!mail.isHasAttachments()) { // 没有附件的邮件可以直接设置已读取
            mail.setHasRead(true);
        }
        SenderFunc.sendInner(this, Sm_Mail.class, Sm_Mail.Builder.class, Sm_Mail.Action.RESP_READ, (b, br) -> {
            b.addMailsInfo(MailsCtrlProtos.create_Sm_Mail_Info(mail));
        });
        save();
    }


    @Override
    public void getAttachments(String mailId) {
        LogicCheckUtils.validateParam(String.class, mailId);
        _logicCheck_IsMailExist(mailId);
        _logicCheck_IsMailExpire(mailId);
        Mail mail = _getMail(mailId);
        if (!mail.isHasAttachments()) {
            String msg = String.format("邮件没有附件无法领取 mailId=%s", mailId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (mail.isHasReceive()) {
            String msg = String.format("邮件已经被领取过了 mailId=%s", mailId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdMaptoCount idMaptoCount = new IdMaptoCount(mail.getAttachments(), 1);
        LogicCheckUtils.canAdd(itemIoCtrl, idMaptoCount);
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(Sm_Mail.Action.RESP_GET_ATTACHMENTS).addItem(idMaptoCount);
        mail.setHasReceive(true);
        mail.setHasRead(true);
        LOGGER.debug("领取了邮件:{} 获得的附件：{}", mailId, idMaptoCount);
        SenderFunc.sendInner(this, Sm_Mail.class, Sm_Mail.Builder.class, Sm_Mail.Action.RESP_GET_ATTACHMENTS, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(refresh, br);
            b.addMailsInfo(MailsCtrlProtos.create_Sm_Mail_Info(mail));
        });
        save();
    }


    @Override
    public void getAllAttachments() {
        List<Mail> mailList = getAllUnGetMails();
        if (mailList.size() <= 0) {
            String msg = String.format("没有附件可以领取！");
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        for (Mail mail : mailList) {
            IdMaptoCount one = new IdMaptoCount(mail.getAttachments(), 1);
            idMaptoCount.addAll(one);
            mail.setHasReceive(true);
            mail.setHasRead(true);
            LOGGER.debug("领取了邮件:{} 获得的附件：{}", mail.getMailId(), one);
        }
        LogicCheckUtils.canAdd(itemIoCtrl, idMaptoCount);
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(Sm_Mail.Action.RESP_GET_All_ATTACHMENTS).addItem(idMaptoCount);
        SenderFunc.sendInner(this, Sm_Mail.class, Sm_Mail.Builder.class, Action.RESP_GET_All_ATTACHMENTS, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(refresh, br);
            b.addAllMailsInfo(MailsCtrlProtos.create_Sm_Mail_InfoList(mailList));
        });
        save();
    }


    public void tellHasNewMail() {
        SenderFunc.sendInner(this, Sm_Mail.class, Sm_Mail.Builder.class, Sm_Mail.Action.RESP_HAS_NEW_MAIL, (b, br) -> {
        });
    }


    @Override
    public void addMail(Mail mail) {
        MailsCtrlUtils.addMail(target, mail);
        LOGGER.debug("新增了邮件:{} ", mail.getMailId());
        tellHasNewMail();
        save();
    }


    @Override
    public void addSysMail(int mailTemplateId, IdMaptoCount idMaptoCount, Object... args) {
        SysMail sysMail = MailsCtrlUtils.createSysMail(mailTemplateId, idMaptoCount, args);
        addMail(sysMail);
    }


    @Override
    public void toGetNewGmMail() {
        int size = _getNewGmMail();
        if (size > 0) {
            tellHasNewMail();
            save();
        }
    }


    /**
     * ========================================================以下为内部方法======================================================
     */

    private int _getNewGmMail() {
        LOGGER.debug("前去邮件中心获取Gm邮件！");
        In_SyncGmMail.Response response = _sendMsgToMailsCenter(new In_SyncGmMail.Request(
                getPlayerCtrl().getTarget().getAccount().getPlatformType(),
                getPlayerCtrl().getOuterRealmId(),
                target.getLastGmMailSendTime(),
                getPlayerCtrl().getTarget().getAccount().getCreateAt(),
                getPlayerCtrl().getCurLevel(), getPlayerCtrl().getTarget().getPayment().getVipLevel()
        ));
        List<GmMail> gmMailList = response.getGmMailList();
        LOGGER.debug("前去邮件中心获取Gm邮件的结果为(个数):{}！", gmMailList.size());
        for (GmMail gmMail : gmMailList) {
            MailsCtrlUtils.addMail(target, gmMail);
        }
        int size = gmMailList.size();
        if (size > 0) {
            target.setLastGmMailSendTime(gmMailList.get(size - 1).getSendTime());
        }
        return size;
    }

    private <I, O> O _sendMsgToMailsCenter(I request) {
        InnerMsg response = ActorMsgSynchronizedExecutor.sendMsgToSingleServerIgnoreEnv(ServerRoleEnum.WS_ParticularFunctionServer, getPlayerCtrl().getContext(), ActorSystemPath.WS_ThirdPartyServer_Selection_MailsCenterActor, request);
        LogicCheckUtils.checkResponse(response);
        return (O) response;
    }

    /**
     * 删除过期的邮件
     */
    private void _deleteExpiredMails() {
        _deleteExpiredMails(target.getIdToGmMail().entrySet());
        _deleteExpiredMails(target.getIdToSysMail().entrySet());
    }

    /**
     * 删除已经读取的邮件
     */
    private void _deleteHasReadMails() {
        _deleteHasReadMails(target.getIdToGmMail().entrySet());
        _deleteHasReadMails(target.getIdToSysMail().entrySet());
    }


    private void _deleteExpiredMails(Set<?> set) {
        Iterator<?> it = set.iterator();
        while (it.hasNext()) {
            Entry entry = (Entry) it.next();
            Mail mail = (Mail) entry.getValue();
            if (StringUtils.isBlank(mail.getExpireTime())) {
                it.remove();
                LOGGER.debug("删除邮件:{}，因为邮件的ExpireTime为空！", mail.getMailId());
                continue;
            }
            _deletExpiredMail(it, mail);
        }
    }

    private void _deleteHasReadMails(Set<?> set) {
        Iterator<?> it = set.iterator();
        while (it.hasNext()) {
            Entry entry = (Entry) it.next();
            Mail mail = (Mail) entry.getValue();
            if (mail.isHasRead()) {
                it.remove();
                LOGGER.debug("删除邮件:{}，因为邮件已经成功读取or获取领取了附件！", mail.getMailId());
            }
        }
    }

    private void _deletExpiredMail(Iterator<?> it, Mail mail) {
        if (!mail.isHasRead()) {// 未读取的邮件(包括有附件未领取的)
            Date date = WsDateUtils.dateToFormatDate(mail.getExpireTime(), WsDateFormatEnum.yyyy_MM_dd$HH_mm_ss);
            if (System.currentTimeMillis() >= date.getTime()) {
                it.remove();
                LOGGER.debug("删除邮件:{}，因为邮件的已经到了失效日期！", mail.getMailId());
            }
        }
    }


    /**
     * 是否已经过期
     *
     * @param mailId
     * @return
     */
    private boolean _isMailExpire(String mailId) {
        Mail mail = _getMail(mailId);
        long nowTime = new Date().getTime();
        long expireTime = WsDateUtils.dateToFormatDate(mail.getExpireTime(), WsDateFormatEnum.yyyy_MM_dd$HH_mm_ss).getTime();
        return nowTime > expireTime;
    }


    /**
     * 邮件是否存在
     *
     * @param mailId
     * @return
     */
    private boolean _isMailExist(String mailId) {
        if (target.getIdToSysMail().containsKey(mailId)) {
            return true;
        }
        return target.getIdToGmMail().containsKey(mailId);
    }


    private Mail _getMail(String mailId) {
        Mail mail = null;
        if (target.getIdToGmMail().containsKey(mailId)) {
            mail = target.getIdToGmMail().get(mailId);
        }
        if (target.getIdToSysMail().containsKey(mailId)) {
            mail = target.getIdToSysMail().get(mailId);
        }
        return mail;
    }

    /**
     * 获取所有未领取附件的邮件
     *
     * @return
     */
    private List<Mail> getAllUnGetMails() {
        List<Mail> mailList = new ArrayList<>();
        mailList.addAll(getAllUnGetMails(target.getIdToGmMail()));
        mailList.addAll(getAllUnGetMails(target.getIdToSysMail()));
        return mailList;
    }

    /**
     * 获取一个类型中所有未领取附件的邮件
     *
     * @param mailsMap
     * @param <T>
     * @return
     */
    private <T extends Mail> List<Mail> getAllUnGetMails(Map<String, T> mailsMap) {
        List<Mail> mailList = new ArrayList<>();
        for (T mail : mailsMap.values()) {
            if (mail.isHasAttachments() && !mail.isHasReceive()) { // 有附件并且未领取
                mailList.add(mail);
            }
        }
        return mailList;
    }


    /**
     * ======================================================== logic check ======================================================
     */

    private void _logicCheck_IsMailExist(String mailId) {
        if (!_isMailExist(mailId)) {
            String msg = String.format("不存在的邮件ID mailId=%s", mailId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
    }

    private void _logicCheck_IsMailExpire(String mailId) {
        if (_isMailExpire(mailId)) {
            String msg = String.format("邮件已经过期 mailId=%s", mailId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
    }

}
