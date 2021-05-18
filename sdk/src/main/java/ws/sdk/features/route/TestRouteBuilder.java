package ws.sdk.features.route;

import com.alibaba.fastjson.JSONObject;
import org.apache.camel.Exchange;
import org.apache.camel.converter.stream.InputStreamCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.sdk.features.utils.RequestUtils;
import ws.sdk.system.AbstractRouteProcessor;
import ws.sdk.system.WsRouteBuilder;
import ws.sdk.system.camel.JsonExchangeUtils;
import ws.sdk.system.config.AppConfig;

public class TestRouteBuilder extends WsRouteBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestRouteBuilder.class);

    @Override
    public void _configure() throws Exception {
        from(AppConfig.get().getString(AppConfig.Key.WS_Sdk_uri_test)).process(new AbstractRouteProcessor() {
            @Override
            public void _process(Exchange exchange) throws Exception {
                String host = RequestUtils.getHeaderContentByKey(exchange, RequestUtils.Key.Host);
                String encoding = RequestUtils.getHeaderContentByKey(exchange, RequestUtils.Key.CamelHttpCharacterEncoding);
                String method = RequestUtils.getHeaderContentByKey(exchange, RequestUtils.Key.CamelHttpMethod);
                String wsReqData = RequestUtils.getHeaderContentByKey(exchange, RequestUtils.Key.WsReqData);
                String wsReqType = RequestUtils.getHeaderContentByKey(exchange, RequestUtils.Key.WsReqType);
                // System.out.println("contentLen --->" + contentLen);
                System.out.println("method --->" + method);
                System.out.println("host --->" + host);
                System.out.println("encoding --->" + encoding);
                System.out.println("wsReqData --->" + wsReqData);
                System.out.println("wsReqType --->" + wsReqType);

                System.out.println("\n==================================================\n");
                Exchange exchange2 = exchange.copy();
                InputStreamCache cache2 = (InputStreamCache) RequestUtils.getBody(exchange2);
                byte[] allBytes = new byte[cache2.available()];
                System.out.println(cache2.available());
                cache2.read(allBytes);

                JSONObject bodys = new JSONObject();
                String s = org.apache.commons.io.IOUtils.toString(allBytes, "UTF-8");
                for (String one : s.split("&")) {
                    String[] oneEles = one.split("=");
                    String key = java.net.URLDecoder.decode(oneEles[0], "UTF-8");
                    String val = java.net.URLDecoder.decode(oneEles[1], "UTF-8");
                    bodys.put(key, val);
                }
                System.out.println("decode 1--->\n" + s + "");
                System.out.println("result --->\n" + bodys.toJSONString());


                JSONObject body = new JSONObject();
                body.put("input", bodys.toJSONString());
                JsonExchangeUtils.Out.success(exchange, body);
            }
        });
    }
}
