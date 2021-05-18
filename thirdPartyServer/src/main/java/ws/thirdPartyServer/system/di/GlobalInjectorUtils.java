package ws.thirdPartyServer.system.di;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import ws.common.redis.RedisOpration;
import ws.common.redis._RedisOpration;
import ws.common.utils.di.GlobalInjector;
import ws.thirdPartyServer.features.actor.payment.payment.platform.PaymentPlatformHandlerManager;
import ws.thirdPartyServer.features.actor.payment.payment.platform._PaymentPlatformHandlerManager;

public class GlobalInjectorUtils {
    public static void init() throws Exception {
        Injector injector = Guice.createInjector(binder -> {
            _bindAll(binder);
        });
        GlobalInjector.setDefaultInjector(injector);
    }

    private static void _bindAll(Binder binder) {
        BindAllDaos.bind(binder);
        BindAllControlers.bind(binder);
        binder.bind(PaymentPlatformHandlerManager.class).to(_PaymentPlatformHandlerManager.class).in(Scopes.SINGLETON);
        binder.bind(RedisOpration.class).to(_RedisOpration.class).in(Scopes.SINGLETON);
    }
}
