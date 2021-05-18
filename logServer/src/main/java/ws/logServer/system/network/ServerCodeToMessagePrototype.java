package ws.logServer.system.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.network.exception.CodeOfMessagePrototypeAlreadyExistsException;
import ws.common.network.server.implement._CodeToMessagePrototype;

public class ServerCodeToMessagePrototype extends _CodeToMessagePrototype {
    private static final Logger logger = LoggerFactory.getLogger(ServerCodeToMessagePrototype.class);

    public ServerCodeToMessagePrototype() {
        try {
            init();
        } catch (CodeOfMessagePrototypeAlreadyExistsException e) {
            logger.error("Error on Init {}", getClass().getName(), e);
        }
    }

    private void init() throws CodeOfMessagePrototypeAlreadyExistsException {
        logger.debug("Initing {}", getClass().getName());
    }
}
