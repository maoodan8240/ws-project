package ws.thirdPartyServer.features.actor.loginCheck.loginCheck.platform;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import ws.relationship.appServers.thirdPartyServer.In_LoginToPlatformRequest;

public interface LoginCheckPlatform {

    /**
     * 登录验证
     */
    void loginAuthorizeRequest(ActorContext actorContext, ActorRef sender, In_LoginToPlatformRequest request) throws Exception;

}
