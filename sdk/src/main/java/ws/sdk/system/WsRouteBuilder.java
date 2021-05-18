package ws.sdk.system;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WsRouteBuilder extends RouteBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(WsRouteBuilder.class);

    @Override
    public void configure() throws Exception {
        try {
            _configure();
        } catch (Exception e) {
            LOGGER.error("RouteBuilder configure error! ", e);
        }
    }

    public abstract void _configure() throws Exception;
}
