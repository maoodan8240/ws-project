package ws.sdk.features.route;

import com.alibaba.fastjson.JSONObject;
import org.apache.camel.Exchange;
import org.bson.types.ObjectId;
import ws.sdk.system.AbstractRouteProcessor;
import ws.sdk.system.WsRouteBuilder;
import ws.sdk.system.camel.JsonExchangeUtils;
import ws.sdk.system.config.AppConfig;

public class UniqueOuterAccountRouteBuilder extends WsRouteBuilder {

    @Override
    public void _configure() throws Exception {
        from(AppConfig.get().getString(AppConfig.Key.WS_Sdk_uri_unique_outer_account)).process(new AbstractRouteProcessor() {

            @Override
            public void _process(Exchange exchange) throws Exception {

                JSONObject body = new JSONObject();
                body.put("accountId", ObjectId.get().toString());
                JsonExchangeUtils.Out.success(exchange, body);
            }
        });
    }
}
