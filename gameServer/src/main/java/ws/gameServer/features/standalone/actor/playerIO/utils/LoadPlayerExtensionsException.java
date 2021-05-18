package ws.gameServer.features.standalone.actor.playerIO.utils;

import ws.relationship.exception.WsBaseException;

public class LoadPlayerExtensionsException extends WsBaseException {
    private static final long serialVersionUID = 1L;

    public LoadPlayerExtensionsException(Throwable t) {
        super(t);
    }
}
