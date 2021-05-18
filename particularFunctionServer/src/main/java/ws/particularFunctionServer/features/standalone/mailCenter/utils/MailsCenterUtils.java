package ws.particularFunctionServer.features.standalone.mailCenter.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.date.WsDateFormatEnum;
import ws.common.utils.date.WsDateUtils;
import ws.relationship.base.msg.mail.In_SyncGmMail;
import ws.relationship.topLevelPojos.mailCenter.GmMail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MailsCenterUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailsCenterUtils.class);

    /**
     * 过滤不可用的邮件
     *
     * @param gmMails
     * @param request
     * @return
     */
    public static List<GmMail> filterGmMails(List<GmMail> gmMails, In_SyncGmMail.Request request) {
        List<GmMail> mailsReturn = new ArrayList<>();
        for (int idx = 0; idx < gmMails.size(); idx++) {
            GmMail gmMail = gmMails.get(idx);
            if (!isNewGmMail(gmMail, request)) {
                LOGGER.debug("邮件={}-{} 不是新邮件！", idx, gmMail.getSendTime());
                continue;
            }
            if (isExpiredGmMail(gmMail)) {
                LOGGER.debug("邮件={}-{} 已经过期了！", idx, gmMail.getSendTime());
                continue;
            }
            if (!isInLimitOuterRealmId(gmMail, request)) {
                LOGGER.debug("邮件={}-{} 不在[服]的限制范围内！", idx, gmMail.getSendTime());
                continue;
            }
            if (!isInLimitPlatform(gmMail, request)) {
                LOGGER.debug("邮件={}-{} 不在[平台]的限制范围内！", idx, gmMail.getSendTime());
                continue;
            }
            if (!isInLimitCreateAt(gmMail, request)) {
                LOGGER.debug("邮件={}-{} 不在[创建日期]的限制范围内！", idx, gmMail.getSendTime());
                continue;
            }
            if (!isInLimitLevel(gmMail, request)) {
                LOGGER.debug("邮件={}-{} 不在[等级]的限制范围内！", idx, gmMail.getSendTime());
                continue;
            }
            if (!isInLimitVipLevel(gmMail, request)) {
                LOGGER.debug("邮件={}-{} 不在[VIP等级]的限制范围内！", idx, gmMail.getSendTime());
                continue;
            }
            LOGGER.debug("邮件={}-{} 获取到新邮件！", idx, gmMail.getSendTime());
            mailsReturn.add(gmMail.clone()); // 此处clone
        }
        return mailsReturn;
    }

    /**
     * 对玩家来说，是否是新邮件
     *
     * @param gmMail
     * @param request
     * @return
     */
    private static boolean isNewGmMail(GmMail gmMail, In_SyncGmMail.Request request) {
        if (!StringUtils.isBlank(request.getLastGmMailSendTime())) {
            Date d1 = WsDateUtils.dateToFormatDate(gmMail.getSendTime(), WsDateFormatEnum.yyyy_MM_dd$HH_mm_ss);
            Date d2 = WsDateUtils.dateToFormatDate(request.getLastGmMailSendTime(), WsDateFormatEnum.yyyy_MM_dd$HH_mm_ss);
            if (d2.getTime() >= d1.getTime()) { // 之前的邮件，已经获取过
                return false;
            }
        }
        return true;
    }

    /**
     * 邮件是否已经过期失效
     *
     * @param gmMail
     * @return true 失效 false 未失效
     */
    private static boolean isExpiredGmMail(GmMail gmMail) {
        if (StringUtils.isBlank(gmMail.getExpireTime())) {  // 未填写过期日期，则永不过期
            return false;
        }
        Date expireDate = WsDateUtils.dateToFormatDate(gmMail.getExpireTime(), WsDateFormatEnum.yyyy_MM_dd$HH_mm_ss);
        if (System.currentTimeMillis() >= expireDate.getTime()) {
            // 邮件已经失效
            return true;
        }
        return false;
    }


    /**
     * 是否在 [等级] 限制范围内
     *
     * @param gmMail
     * @param request
     * @return ture 在等级范围内，可以获取邮件 false 不能获取邮件
     */
    private static boolean isInLimitLevel(GmMail gmMail, In_SyncGmMail.Request request) {
        if (gmMail.getLimitLevelMin() > 0 && gmMail.getLimitLevelMin() > request.getLevel()) {
            // 邮件小的限制等级 比 玩家等级还大
            return false;
        }
        if (gmMail.getLimitLevelMax() > 0 && gmMail.getLimitLevelMax() < request.getLevel()) {
            // 邮件大的限制等级 比 玩家等级还小
            return false;
        }
        return true;
    }

    /**
     * 是否在 [服] 限制范围内
     *
     * @param gmMail
     * @param request
     * @return ture 在服范围内，可以获取邮件 false 不能获取邮件
     */
    private static boolean isInLimitOuterRealmId(GmMail gmMail, In_SyncGmMail.Request request) {
        if (!isInLimitOuterRealmIdOut(gmMail, request)) {
            return false;
        }
        return isInLimitOuterRealmIdIn(gmMail, request);
    }

    /**
     * 是否在 [排除服] 限制范围内
     *
     * @param gmMail
     * @param request
     * @return ture 在不在[排除服]范围内，可以获取邮件 false 不能获取邮件
     */
    private static boolean isInLimitOuterRealmIdOut(GmMail gmMail, In_SyncGmMail.Request request) {
        if ((gmMail.getOuterRealmIdsOut() != null && gmMail.getOuterRealmIdsOut().size() > 0) && (gmMail.getOuterRealmIdsOut().contains(request.getOuterRealmId()))) {
            // 邮件限制的服 不包含玩家所属的服
            return false;
        }
        return true;
    }

    /**
     * 是否在 [服] 限制范围内
     *
     * @param gmMail
     * @param request
     * @return ture 在服范围内，可以获取邮件 false 不能获取邮件
     */
    private static boolean isInLimitOuterRealmIdIn(GmMail gmMail, In_SyncGmMail.Request request) {
        if ((gmMail.getOuterRealmIdsIn() != null && gmMail.getOuterRealmIdsIn().size() > 0) && (!gmMail.getOuterRealmIdsIn().contains(request.getOuterRealmId()))) {
            // 邮件限制的服 不包含玩家所属的服
            return false;
        }
        return true;
    }

    /**
     * 是否在 [平台] 限制范围内
     *
     * @param gmMail
     * @param request
     * @return ture 在平台范围内，可以获取邮件 false 不能获取邮件
     */
    private static boolean isInLimitPlatform(GmMail gmMail, In_SyncGmMail.Request request) {
        if ((gmMail.getLimitPlatforms() != null && gmMail.getLimitPlatforms().size() > 0) && (!gmMail.getLimitPlatforms().contains(request.getPlatformType()))) {
            // 邮件限制的平台 不包含玩家所属的平台
            return false;
        }
        return true;
    }

    /**
     * 是否在 [VIP等级] 限制范围内
     *
     * @param gmMail
     * @param request
     * @return ture 在VIP等级范围内，可以获取邮件 false 不能获取邮件
     */
    private static boolean isInLimitVipLevel(GmMail gmMail, In_SyncGmMail.Request request) {
        if (gmMail.getLimitVipLevelMin() > 0 && gmMail.getLimitVipLevelMin() > request.getVipLevel()) {
            // 邮件小的限制VIP等级 比 玩家VIP等级还大
            return false;
        }
        if (gmMail.getLimitVipLevelMax() > 0 && gmMail.getLimitVipLevelMax() < request.getVipLevel()) {
            // 邮件大的限制VIP等级 比 玩家VIP等级还小
            return false;
        }
        return true;
    }

    /**
     * 是否在 [注册时间] 限制范围内
     *
     * @param gmMail
     * @param request
     * @return ture 在注册时间范围内，可以获取邮件 false 不能获取邮件
     */
    private static boolean isInLimitCreateAt(GmMail gmMail, In_SyncGmMail.Request request) {
        Date createAtTime = WsDateUtils.dateToFormatDate(request.getCreateAt(), WsDateFormatEnum.yyyy_MM_dd$HH_mm_ss);
        if (!StringUtils.isBlank(gmMail.getLimitCreateAtMin())) {
            if (WsDateUtils.dateToFormatDate(gmMail.getLimitCreateAtMin(), WsDateFormatEnum.yyyy_MM_dd$HH_mm_ss).getTime() > createAtTime.getTime()) {
                // 邮件小的限制时间点 比 玩家注册时间还大
                return false;
            }
        }
        if (!StringUtils.isBlank(gmMail.getLimitCreateAtMax())) {
            if (WsDateUtils.dateToFormatDate(gmMail.getLimitCreateAtMax(), WsDateFormatEnum.yyyyMMddHHmmss).getTime() < createAtTime.getTime()) {
                // 邮件大的限制时间点 比 玩家注册时间还小
                return false;
            }
        }
        return true;
    }

}
