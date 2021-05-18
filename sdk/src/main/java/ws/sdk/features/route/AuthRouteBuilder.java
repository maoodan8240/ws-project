package ws.sdk.features.route;

import com.alibaba.fastjson.JSONObject;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;
import ws.common.utils.di.GlobalInjector;
import ws.protos.EnumsProtos.ErrorCodeEnum;
import ws.relationship.topLevelPojos.sdk.account.Account;
import ws.sdk.features.session.SessionManager;
import ws.sdk.features.utils.RequestUtils;
import ws.sdk.system.AbstractRouteProcessor;
import ws.sdk.system.WsRouteBuilder;
import ws.sdk.system.camel.JsonExchangeUtils;
import ws.sdk.system.config.AppConfig;

public class AuthRouteBuilder extends WsRouteBuilder {
    private static final SessionManager SESSION_MANAGER = GlobalInjector.getInstance(SessionManager.class);

    @Override
    public void _configure() throws Exception {
        from(AppConfig.get().getString(AppConfig.Key.WS_Sdk_uri_auth)).process(new AbstractRouteProcessor() {
            @Override
            public void _process(Exchange exchange) throws Exception {
                Request request = RequestUtils.get(exchange);
                String sessionId = request.getParameter("sessionId");
                if (StringUtils.isBlank(sessionId)) {
                    JsonExchangeUtils.Out.fail(exchange, ErrorCodeEnum.SDK_BLANK_SESSIONID.getNumber());
                    return;
                }
                Account account = SESSION_MANAGER.queryBySessionId(sessionId);
                if (account == null) {
                    JsonExchangeUtils.Out.fail(exchange, ErrorCodeEnum.SDK_SESSION_ID_EXPIRED.getNumber());
                    return;
                }
                JSONObject body = new JSONObject();
                body.put("accountId", account.getAccountId());
                body.put("account", account.getAccountName());
                JsonExchangeUtils.Out.success(exchange, body);
                log.info("Auth Success, account={}, accountId={}", account.getAccountName(), account.getAccountId());
            }
        });
    }
}
