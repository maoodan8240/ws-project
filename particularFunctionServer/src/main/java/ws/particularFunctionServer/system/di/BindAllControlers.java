package ws.particularFunctionServer.system.di;

import com.google.inject.Binder;
import com.google.inject.Scopes;
import ws.particularFunctionServer.features.standalone.mailCenter.ctrl.MailsCenterCtrl;
import ws.particularFunctionServer.features.standalone.mailCenter.ctrl._MailsCenterCtrl;

/**
 * 绑定所有Control
 */
public class BindAllControlers {
    public static void bind(Binder binder) {
        binder.bind(MailsCenterCtrl.class).to(_MailsCenterCtrl.class).in(Scopes.SINGLETON);
    }
}
