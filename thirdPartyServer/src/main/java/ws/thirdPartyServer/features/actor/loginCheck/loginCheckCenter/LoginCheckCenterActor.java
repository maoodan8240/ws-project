package ws.thirdPartyServer.features.actor.loginCheck.loginCheckCenter;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.protos.EnumsProtos.PlatformTypeEnum;
import ws.relationship.appServers.thirdPartyServer.In_LoginToPlatformRequest;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.thirdPartyServer.features.actor.loginCheck.loginCheck.LoginCheckActor;

import java.util.HashMap;
import java.util.Map;

public class LoginCheckCenterActor extends WsActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginCheckCenterActor.class);
    private Map<PlatformTypeEnum, ActorRef> platformToActorRef = new HashMap<>();

    public LoginCheckCenterActor() {
        init();
    }

    @Override
    public void onRecv(Object msg) throws Exception {
        if (msg instanceof In_LoginToPlatformRequest) {
            onLoginToPlatformRequest((In_LoginToPlatformRequest) msg);
        }
    }

    private void onLoginToPlatformRequest(In_LoginToPlatformRequest request) {
        ActorRef actorRef = platformToActorRef.get(request.getPlatformType());
        if (actorRef == null) {
            LOGGER.warn("处理渠道的Actor不存在！！！！");
            return;
        }
        actorRef.tell(request, sender());
    }

    private void init() {
        platformToActorRef.clear();
        for (PlatformTypeEnum platformTypeEnum : PlatformTypeEnum.values()) {
            ActorRef ref = getContext().actorOf(Props.create(LoginCheckActor.class, platformTypeEnum), ActorSystemPath.WS_ThirdPartyServer_LoginCheck + platformTypeEnum.toString());
            getContext().watch(ref);
            platformToActorRef.put(platformTypeEnum, ref);
        }
    }
}
