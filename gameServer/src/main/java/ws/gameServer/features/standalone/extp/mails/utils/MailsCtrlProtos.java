package ws.gameServer.features.standalone.extp.mails.utils;

import ws.common.utils.date.WsDateFormatEnum;
import ws.common.utils.date.WsDateUtils;
import ws.gameServer.features.standalone.extp.utils.CommonUtils;
import ws.protos.MailProtos.Sm_Mail_Info;
import ws.relationship.topLevelPojos.mailCenter.GmMail;
import ws.relationship.topLevelPojos.mails.Mail;
import ws.relationship.topLevelPojos.mails.Mails;
import ws.relationship.topLevelPojos.mails.SysMail;
import ws.relationship.utils.ProtoUtils;
import ws.relationship.utils.RelationshipCommonUtils;

import java.util.ArrayList;
import java.util.List;

public class MailsCtrlProtos {

    public static List<Sm_Mail_Info> create_Sm_Mail_InfoList(Mails mails) {
        List<Sm_Mail_Info> bs = new ArrayList<>();
        mails.getIdToSysMail().forEach((id, mail) -> {
            bs.add(create_Sm_Mail_Info(mail));
        });
        mails.getIdToGmMail().forEach((id, mail) -> {
            bs.add(create_Sm_Mail_Info(mail));
        });
        return bs;
    }

    public static List<Sm_Mail_Info> create_Sm_Mail_InfoList(List<Mail> mails) {
        List<Sm_Mail_Info> bs = new ArrayList<>();
        for (Mail mail : mails) {
            bs.add(create_Sm_Mail_Info(mail));
        }
        return bs;
    }


    public static Sm_Mail_Info create_Sm_Mail_Info(Mail mail) {
        Sm_Mail_Info.Builder b = Sm_Mail_Info.newBuilder();
        b.setMailId(mail.getMailId());
        b.setTitle(CommonUtils.converNullToEmpty(mail.getTitle()));
        b.setContent(CommonUtils.converNullToEmpty(mail.getContent()));
        b.setTeplateId(mail.getTpId());
        long sendTime = WsDateUtils.dateToFormatDate(mail.getSendTime(), WsDateFormatEnum.yyyy_MM_dd$HH_mm_ss).getTime();
        b.setSendTime(sendTime);
        b.setSenderName(CommonUtils.converNullToEmpty(mail.getSenderName()));
        b.setHasRead(mail.isHasRead());
        b.setDeleteAfterRead(mail.isDeleteAfterRead());
        b.setHasAttachments(mail.isHasAttachments());
        if (mail.isHasAttachments()) {
            b.setAttachments(ProtoUtils.create_Sm_Common_IdAndCountList_UseIac(mail.getAttachments()));
        }
        if (mail instanceof GmMail) {
            GmMail gmMail = (GmMail) mail;
            b.setContent(RelationshipCommonUtils.converNullToEmpty(gmMail.getContent()));
        } else if (mail instanceof SysMail) {
            SysMail sysMail = (SysMail) mail;
            b.addAllArgs(sysMail.getArgs());
        }
        return b.build();
    }

}
