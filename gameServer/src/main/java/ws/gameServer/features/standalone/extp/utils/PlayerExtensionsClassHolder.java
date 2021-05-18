package ws.gameServer.features.standalone.extp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.classProcess.ClassFinder;
import ws.gameServer.features.standalone.actor.player.mc.extension.PlayerExtension;
import ws.gameServer.features.standalone.extp.ExtpPackageHolder;
import ws.relationship.exception.BusinessLogicMismatchConditionException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class PlayerExtensionsClassHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerExtensionsClassHolder.class);
    
    private static List<Class<? extends PlayerExtension>> playerAllExtensionClasses = null;
    private static List<Class<? extends PlayerExtension>> playerUseExtensionClasses = new ArrayList<>();

    static {
        playerAllExtensionClasses = ClassFinder.getAllAssignedClass(PlayerExtension.class, ExtpPackageHolder.class);
        init();
    }

    public static void init() {
        playerUseExtensionClasses.clear();
        for (Class<? extends PlayerExtension> extensionClass : playerAllExtensionClasses) {
            if (useExtension(extensionClass)) {
                playerUseExtensionClasses.add(extensionClass);
            } else {
                LOGGER.debug("PlayerExtension={} 未启用！开启请修改[useExtension] 属性!", extensionClass);
            }
        }
    }

    public static List<Class<? extends PlayerExtension>> getPlayerUseExtensionClasses() {
        return new ArrayList<>(playerUseExtensionClasses);
    }

    private static boolean useExtension(Class<? extends PlayerExtension> extensionClass) {
        try {
            Field field = extensionClass.getField("useExtension");
            return (Boolean) field.get(null);
        } catch (Exception e) {
            String msg = String.format("clazz=%s 未配置 [useExtension] 属性!", extensionClass);
            throw new BusinessLogicMismatchConditionException(msg);
        }
    }
}
