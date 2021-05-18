package ws.gatewayServer.system.di;

import com.google.inject.Binder;
import com.google.inject.Scopes;
import ws.common.network.server.interfaces.CodeToMessagePrototype;
import ws.common.network.server.interfaces.NetworkListener;
import ws.common.redis.RedisOpration;
import ws.common.redis._RedisOpration;
import ws.common.utils.formula.FormulaContainer;
import ws.common.utils.formula._FormulaContainer;
import ws.gatewayServer.system.network.BroadcastNetworkMessageListener;
import ws.gatewayServer.system.network.CodeToMsgPrototypeFromConfig;

/**
 * 绑定所有Bean
 */
public class BindAllBeans {
    public static void bind(Binder binder) {
        binder.bind(NetworkListener.class).to(BroadcastNetworkMessageListener.class).in(Scopes.SINGLETON);
        binder.bind(CodeToMessagePrototype.class).to(CodeToMsgPrototypeFromConfig.class).in(Scopes.SINGLETON);
        binder.bind(RedisOpration.class).to(_RedisOpration.class).in(Scopes.SINGLETON);
        binder.bind(FormulaContainer.class).to(_FormulaContainer.class).in(Scopes.SINGLETON);
    }
}
