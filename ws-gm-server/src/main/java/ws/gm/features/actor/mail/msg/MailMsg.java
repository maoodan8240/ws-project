package ws.gm.features.actor.mail.msg;

import ws.gm.system.httpServer.msg.HttpRequestMsg;

/**
 * author: lilin
 * 2017/4/11
 */
public class MailMsg {
    public static class SendMailToPlayers extends HttpRequestMsg {
        private String ids;
        private String title;
        private String content;
        private String expireTime;
        private boolean deleteAfterRead;
        private String attachments;
        private String senderName;

        public String getIds() {
            return ids;
        }

        public void setIds(String ids) {
            this.ids = ids;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(String expireTime) {
            this.expireTime = expireTime;
        }

        public boolean isDeleteAfterRead() {
            return deleteAfterRead;
        }

        public void setDeleteAfterRead(boolean deleteAfterRead) {
            this.deleteAfterRead = deleteAfterRead;
        }

        public String getAttachments() {
            return attachments;
        }

        public void setAttachments(String attachments) {
            this.attachments = attachments;
        }

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }
    }

    public static class SendMailToAll extends HttpRequestMsg {
        private String title;
        private String content;
        private String expireTime;
        private boolean deleteAfterRead;
        private String attachments;
        private String senderName;
        private String outerRealmIdsOut;          // ?????? - ???????????????????????????,(??????????????????outerRealmIdsIn??????) (??????????????????,????????????outerRealmIdsIn??????)
        private String outerRealmIdsIn;           // ?????? - ?????????????????????, ??????????????????
        private String limitPlatforms;   // ?????? - ??????
        private String limitCreateAtMin;                                     // ?????? - ??????????????????
        private String limitCreateAtMax;                                     // ?????? - ??????????????????
        private int limitLevelMin;                                           // ?????? - ????????????
        private int limitLevelMax;                                           // ?????? - ????????????
        private int limitVipLevelMin;                                        // ?????? - VIP????????????
        private int limitVipLevelMax;                                        // ?????? - VIP????????????

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(String expireTime) {
            this.expireTime = expireTime;
        }

        public boolean isDeleteAfterRead() {
            return deleteAfterRead;
        }

        public void setDeleteAfterRead(boolean deleteAfterRead) {
            this.deleteAfterRead = deleteAfterRead;
        }

        public String getAttachments() {
            return attachments;
        }

        public void setAttachments(String attachments) {
            this.attachments = attachments;
        }

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        public String getOuterRealmIdsOut() {
            return outerRealmIdsOut;
        }

        public void setOuterRealmIdsOut(String outerRealmIdsOut) {
            this.outerRealmIdsOut = outerRealmIdsOut;
        }

        public String getOuterRealmIdsIn() {
            return outerRealmIdsIn;
        }

        public void setOuterRealmIdsIn(String outerRealmIdsIn) {
            this.outerRealmIdsIn = outerRealmIdsIn;
        }

        public String getLimitPlatforms() {
            return limitPlatforms;
        }

        public void setLimitPlatforms(String limitPlatforms) {
            this.limitPlatforms = limitPlatforms;
        }

        public String getLimitCreateAtMin() {
            return limitCreateAtMin;
        }

        public void setLimitCreateAtMin(String limitCreateAtMin) {
            this.limitCreateAtMin = limitCreateAtMin;
        }

        public String getLimitCreateAtMax() {
            return limitCreateAtMax;
        }

        public void setLimitCreateAtMax(String limitCreateAtMax) {
            this.limitCreateAtMax = limitCreateAtMax;
        }

        public int getLimitLevelMin() {
            return limitLevelMin;
        }

        public void setLimitLevelMin(int limitLevelMin) {
            this.limitLevelMin = limitLevelMin;
        }

        public int getLimitLevelMax() {
            return limitLevelMax;
        }

        public void setLimitLevelMax(int limitLevelMax) {
            this.limitLevelMax = limitLevelMax;
        }

        public int getLimitVipLevelMin() {
            return limitVipLevelMin;
        }

        public void setLimitVipLevelMin(int limitVipLevelMin) {
            this.limitVipLevelMin = limitVipLevelMin;
        }

        public int getLimitVipLevelMax() {
            return limitVipLevelMax;
        }

        public void setLimitVipLevelMax(int limitVipLevelMax) {
            this.limitVipLevelMax = limitVipLevelMax;
        }
    }
}
