package ws.gameServer.system.di;

import com.google.inject.Binder;
import ws.common.utils.mc.controler.Controler;
import ws.gameServer.utils.AllCtrlClassHolder;

import java.util.Map.Entry;


/**
 * 绑定所有Control
 */
public class BindAllControlers {

    public static void bind(Binder binder) {
        for (Entry<Class<? extends Controler>, Class<? extends Controler>> entry : AllCtrlClassHolder.getIntefaceClassToInstanceClass().entrySet()) {
            forceBind(binder, entry.getKey(), entry.getValue());
        }
    }


    /**
     * 强制两个Class绑定为 interface - implements 关系
     *
     * @param binder
     * @param interfaceClass
     * @param instanceClass
     * @param <X>
     * @param <Y>
     */
    private static <X extends Controler, Y extends X> void forceBind(Binder binder, Class<? extends Controler> interfaceClass, Class<? extends Controler> instanceClass) {
        Class<X> interfaceClassX = (Class<X>) interfaceClass;
        Class<Y> instanceClassY = (Class<Y>) instanceClass;
        binder.bind(interfaceClassX).to(instanceClassY);
    }
}
