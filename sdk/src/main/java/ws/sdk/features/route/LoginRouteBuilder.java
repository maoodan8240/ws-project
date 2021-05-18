package ws.sdk.features.route;

import com.alibaba.fastjson.JSONObject;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.utils.di.GlobalInjector;
import ws.protos.EnumsProtos.ErrorCodeEnum;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.daos.sdk.account.AccountDao;
import ws.relationship.topLevelPojos.sdk.account.Account;
import ws.sdk.features.session.SessionManager;
import ws.sdk.features.utils.RequestUtils;
import ws.sdk.system.AbstractRouteProcessor;
import ws.sdk.system.WsRouteBuilder;
import ws.sdk.system.camel.JsonExchangeUtils;
import ws.sdk.system.config.AppConfig;

public class LoginRouteBuilder extends WsRouteBuilder {
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private SessionManager sessionManager = GlobalInjector.getInstance(SessionManager.class);

    @Override
    public void _configure() throws Exception {
        from(AppConfig.get().getString(AppConfig.Key.WS_Sdk_uri_login)).process(new AbstractRouteProcessor() {

            @Override
            public void _process(Exchange exchange) throws Exception {
                AccountDao accountDao = GlobalInjector.getInstance(AccountDao.class);
                accountDao.init(MONGO_DB_CLIENT, MagicWords_Mongodb.DbName_Sdk);
                Request request = RequestUtils.get(exchange);
                String accountName = request.getParameter("accountName");
                String password = request.getParameter("password");
                if (StringUtils.isBlank(accountName)) {
                    JsonExchangeUtils.Out.fail(exchange, ErrorCodeEnum.SDK_BLANK_ACCOUNT.getNumber());
                    return;
                }
                if (StringUtils.isBlank(password)) {
                    JsonExchangeUtils.Out.fail(exchange, ErrorCodeEnum.SDK_BLANK_PASSWORD.getNumber());
                    return;
                }
                Account accountObj = accountDao.query(accountName.trim());
                if (accountObj == null) {
                    JsonExchangeUtils.Out.fail(exchange, ErrorCodeEnum.SDK_ACCOUNT_NOT_EXISTS.getNumber());
                    return;
                }
                if (!accountObj.getPassword().equals(password.trim())) {
                    JsonExchangeUtils.Out.fail(exchange, ErrorCodeEnum.SDK_PASSWORD_NOT_MATCH.getNumber());
                    return;
                }
                String sessionId = sessionManager.newSessionId();
                sessionManager.put(sessionId, accountObj);
                log.info("Login Success, account={}, sessionId={}", accountObj.getAccountName(), sessionId);
                JSONObject body = new JSONObject();
                body.put("sessionId", sessionId);
                body.put("accountId", accountObj.getAccountId());
                body.put("accountName", accountObj.getAccountName());
                JsonExchangeUtils.Out.success(exchange, body);
            }
        });
    }
}
