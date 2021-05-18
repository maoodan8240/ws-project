package ws.sdk.system;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.sdk.system.camel.JsonExchangeUtils;

public abstract class AbstractRouteProcessor implements Processor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRouteProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        try {
            _process(exchange);
        } catch (Exception e) {
            LOGGER.error("处理失败！", e);
            JsonExchangeUtils.Out.fail(exchange);
        }
    }

    public abstract void _process(Exchange exchange) throws Exception;
}
