package ws.loginServer.features.actor.login;

import akka.actor.ActorContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.protos.EnumsProtos.PlatformTypeEnum;
import ws.relationship.appServers.thirdPartyServer.In_LoginToPlatformRequest;
import ws.relationship.appServers.thirdPartyServer.In_LoginToPlatformResponse;
import ws.relationship.base.ServerRoleEnum;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.utils.ActorMsgSynchronizedExecutor;

public class ThirdPartySystemUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThirdPartySystemUtils.class);

    public static ThirdPartyAccountInfo query(ActorContext actorContext, In_LoginToPlatformRequest request) {
        String uid = request.getUid();
        try {
            In_LoginToPlatformResponse response = ActorMsgSynchronizedExecutor.sendMsgToSingleServerIgnoreEnv(ServerRoleEnum.WS_ThirdPartyServer, actorContext, ActorSystemPath.WS_ThirdPartyServer_Selection_LoginCheckCenter, request);
            if (response == null) {
                return null;
            }
            JSONObject contentJsonObject = null;
            String content = response.getContent();
            content = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1);
            contentJsonObject = JSON.parseObject(content);
            if (!contentJsonObject.getBoolean("_success")) {
                return null;
            }
            if (!StringUtils.isEmpty(contentJsonObject.getString("accountId"))) {
                uid = contentJsonObject.getString("accountId");
            }
            ThirdPartyAccountInfo accountInfo = new ThirdPartyAccountInfo();
            accountInfo.setPlatformType(request.getPlatformType());
            accountInfo.setPlatformUid(uid);
            return accountInfo;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("去第三方校验玩家登录信息异常！", e);
        }
        return null;
    }

    public static class ThirdPartyAccountInfo {
        private PlatformTypeEnum PlatformTypeEnum;
        private String platformUid;

        public PlatformTypeEnum getPlatformType() {
            return PlatformTypeEnum;
        }

        public void setPlatformType(PlatformTypeEnum PlatformTypeEnum) {
            this.PlatformTypeEnum = PlatformTypeEnum;
        }

        public String getPlatformUid() {
            return platformUid;
        }

        public void setPlatformUid(String platformUid) {
            this.platformUid = platformUid;
        }
    }
}
