package ws.gameServer.features.standalone.actor.playerIO.utils;

import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.PlayerExtension;
import ws.gameServer.features.standalone.extp.utils.PlayerExtensionsClassHolder;

@SuppressWarnings("rawtypes")
public class LoadAllPlayerExtensions {

    public static void loadAll(PlayerCtrl playerCtrl) {
        try {
            for (Class<? extends PlayerExtension> extensionClass : PlayerExtensionsClassHolder.getPlayerUseExtensionClasses()) {
                addExtension(playerCtrl, extensionClass);
            }
        } catch (Exception e) {
            throw new LoadPlayerExtensionsException(e);
        }
    }

    private static void addExtension(PlayerCtrl playerCtrl, Class<? extends PlayerExtension> clz) throws Exception {
        PlayerExtension<?> extension = clz.getConstructor(PlayerCtrl.class).newInstance(playerCtrl);
        playerCtrl.addExtension(extension);
    }

}
