package ws.sdk.features.route;

import com.alibaba.fastjson.JSONObject;
import org.apache.camel.Exchange;
import org.eclipse.jetty.server.Request;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.utils.di.GlobalInjector;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.daos.sdk.account.AccountDao;
import ws.relationship.topLevelPojos.sdk.account.Account;
import ws.sdk.features.utils.RequestUtils;
import ws.sdk.system.AbstractRouteProcessor;
import ws.sdk.system.WsRouteBuilder;
import ws.sdk.system.camel.JsonExchangeUtils;
import ws.sdk.system.config.AppConfig;

import java.util.ArrayList;
import java.util.List;

public class ListAllAccountRouteBuilder extends WsRouteBuilder {
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);

    @Override
    public void _configure() throws Exception {
        AccountDao accountDao = GlobalInjector.getInstance(AccountDao.class);
        accountDao.init(MONGO_DB_CLIENT, MagicWords_Mongodb.DbName_Sdk);
        from(AppConfig.get().getString(AppConfig.Key.WS_Sdk_uri_list_all_account)).process(new AbstractRouteProcessor() {

            @Override
            public void _process(Exchange exchange) throws Exception {
                Request request = RequestUtils.get(exchange);
                boolean isOnlySummary = RequestUtils.getBoolParameter(request, "is-only-summary");
                List<Account> allAccounts = accountDao.findAll();
                JSONObject body = new JSONObject();
                body.put("allAccounts", isOnlySummary ? _getAllAccountNamesFrom(allAccounts) : allAccounts);
                JsonExchangeUtils.Out.success(exchange, body);
            }
        });
    }

    private static List<String> _getAllAccountNamesFrom(List<Account> allAccounts) {
        List<String> list = new ArrayList<>();
        allAccounts.forEach(x -> {
            list.add(x.getAccountName());
        });
        return list;
    }
}
