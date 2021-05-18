package ws.thirdPartyServer.features.actor.loginCheck.loginCheck.platform;

import org.apache.commons.lang3.EnumUtils;
import ws.protos.EnumsProtos.PlatformTypeEnum;
import ws.thirdPartyServer.features.utils.AssignedClassHolder;

import java.util.HashMap;
import java.util.Map;

public class LoginCheckPlatformHandlerManager {
    private static final Map<PlatformTypeEnum, LoginCheckPlatform> platformToLoginCheckPlatform = new HashMap<>();

    static {
        try {
            platformToLoginCheckPlatform.clear();
            for (Class<? extends LoginCheckPlatform> clz : AssignedClassHolder.getLoginCheckPlatformClasses()) {
                String[] nameArr = clz.getName().split("_");
                String platformStr = nameArr[nameArr.length - 1];
                PlatformTypeEnum PlatformTypeEnum = EnumUtils.getEnum(PlatformTypeEnum.class, platformStr);
                platformToLoginCheckPlatform.put(PlatformTypeEnum, clz.newInstance());
            }
        } catch (Exception e) {

        }
    }

    public static Map<PlatformTypeEnum, LoginCheckPlatform> getPlatformtologincheckplatform() {
        return platformToLoginCheckPlatform;
    }

}
