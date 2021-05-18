package ws.sdk.features.utils;

import org.apache.camel.Exchange;
import org.eclipse.jetty.server.Request;

public class RequestUtils {
    public static class Key {
        public static final String Host = "Host";
        public static final String CamelHttpCharacterEncoding = "CamelHttpCharacterEncoding";
        public static final String CamelHttpMethod = "CamelHttpMethod";
        public static final String WsReqData = "WsReqData";
        public static final String WsReqType = "WsReqType";
    }

    public static Object getBody(Exchange exchange) {
        return exchange.getIn().getBody();
    }

    public static String getHeaderContentByKey(Exchange exchange, String key) {
        return exchange.getIn().getHeader(key, String.class);
    }

    public static Request get(Exchange exchange) {
        return exchange.getIn().getHeader("CamelHttpServletRequest", Request.class);
    }

    public static int getIntParameter(Request request, String name) {
        return Integer.parseInt(request.getParameter(name));
    }

    public static boolean getBoolParameter(Request request, String name) {
        return Boolean.parseBoolean(request.getParameter(name));
    }
}
