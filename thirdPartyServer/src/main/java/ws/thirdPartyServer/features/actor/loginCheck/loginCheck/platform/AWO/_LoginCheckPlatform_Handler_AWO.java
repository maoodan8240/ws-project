package ws.thirdPartyServer.features.actor.loginCheck.loginCheck.platform.AWO;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.relationship.appServers.thirdPartyServer.In_LoginToPlatformRequest;
import ws.relationship.appServers.thirdPartyServer.In_LoginToPlatformResponse;
import ws.thirdPartyServer.features.actor.loginCheck.loginCheck.platform.LoginCheckPlatform;
import ws.thirdPartyServer.features.http.QhHttpClient;
import ws.thirdPartyServer.system.config.AppConfig;

public class _LoginCheckPlatform_Handler_AWO implements LoginCheckPlatform {
    private static final Logger LOGGER = LoggerFactory.getLogger(_LoginCheckPlatform_Handler_AWO.class);
    private static final String LOGIN_CHECK_URI = "http://%s:20010/sdk/auth?sessionId=%s";

    public _LoginCheckPlatform_Handler_AWO() {
    }

    @Override
    public void loginAuthorizeRequest(ActorContext actorContext, ActorRef sender, In_LoginToPlatformRequest request) {

        String uri = String.format(LOGIN_CHECK_URI, AppConfig.getIp(), request.getSessionOrToken());
        String re = QhHttpClient.httpSynSend_Get(uri);
        sender.tell(new In_LoginToPlatformResponse(request, false, re), actorContext.self());
        LOGGER.info("验证的uri={} 验证的结果rs={}", uri, re);
    }


}
