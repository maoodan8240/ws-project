package ws.sdk.system.camel;

import com.alibaba.fastjson.JSONObject;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonExchangeUtils {
    public static class Out {
        private static final Logger LOGGER = LoggerFactory.getLogger(Out.class);
        /**
         * 错误码：未知错误
         */
        public static final int ERR_CODE_UNKNOWN = 0;

        public static void success(Exchange exchange) {
            success(exchange, new JSONObject());
        }

        public static void success(Exchange exchange, JSONObject body) {
            body.put("_success", true);
            _out(exchange, body);
        }

        public static void fail(Exchange exchange) {
            fail(exchange, ERR_CODE_UNKNOWN);
        }

        public static void fail(Exchange exchange, int errCode) {
            fail(exchange, errCode, new JSONObject());
        }

        public static void fail(Exchange exchange, int errCode, JSONObject body) {
            body.put("_success", false);
            body.put("_errorCode", errCode);
            _out(exchange, body);
        }

        private static void _out(Exchange exchange, JSONObject body) {
            body.put("_timestamp", System.currentTimeMillis());
            LOGGER.info(body.toJSONString());
            exchange.getOut().setHeader("Access-Control-Allow-Origin", "*");
            exchange.getOut().setBody(body.toJSONString());
        }
    }
}
