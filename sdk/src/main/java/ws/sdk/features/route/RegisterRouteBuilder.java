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

public class RegisterRouteBuilder extends WsRouteBuilder {
    private SessionManager sessionManager = GlobalInjector.getInstance(SessionManager.class);
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);

    @Override
    public void _configure() throws Exception {
        from(AppConfig.get().getString(AppConfig.Key.WS_Sdk_uri_register)).process(new AbstractRouteProcessor() {
            @Override
            public void _process(Exchange exchange) throws Exception {
                AccountDao accountDao = GlobalInjector.getInstance(AccountDao.class);
                accountDao.init(MONGO_DB_CLIENT, MagicWords_Mongodb.DbName_Sdk);
                Request request = RequestUtils.get(exchange);
                String newAccountName = request.getParameter("newAccountName");
                String password = request.getParameter("password");
                if (StringUtils.isBlank(newAccountName)) {
                    JsonExchangeUtils.Out.fail(exchange, ErrorCodeEnum.SDK_INVALID_ACCOUNT.getNumber());
                    return;
                }
                if (StringUtils.isBlank(password)) {
                    JsonExchangeUtils.Out.fail(exchange, ErrorCodeEnum.SDK_INVALID_PASSWORD.getNumber());
                    return;
                }
                Account oldAccount = accountDao.query(newAccountName.trim());
                if (oldAccount != null) {
                    JsonExchangeUtils.Out.fail(exchange, ErrorCodeEnum.SDK_ACCOUNT_ALREADY_EXISTS.getNumber());
                    return;
                }
                Account newAccountObj = new Account(newAccountName.trim(), password.trim());
                accountDao.insert(newAccountObj);
                log.info("Register Success, newAccount={}", newAccountObj.getAccountName());

                String sessionId = sessionManager.newSessionId();
                sessionManager.put(sessionId, newAccountObj);

                JSONObject body = new JSONObject();
                body.put("sessionId", sessionId);
                body.put("accountId", newAccountObj.getAccountId());
                body.put("accountName", newAccountObj.getAccountName());
                JsonExchangeUtils.Out.success(exchange, body);
            }
        });
    }
}
