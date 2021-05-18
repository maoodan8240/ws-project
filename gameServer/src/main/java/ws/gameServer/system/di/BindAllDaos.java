package ws.gameServer.system.di;

import com.google.inject.Binder;
import ws.common.mongoDB.interfaces.BaseDao;
import ws.relationship.daos.AllDaoClassHolder;

import java.util.Map.Entry;

/**
 * 绑定全部Dao
 */
public class BindAllDaos {
    public static void bind(Binder binder) {
        for (Entry<Class<? extends BaseDao>, Class<? extends BaseDao>> entry : AllDaoClassHolder.getIntefaceClassToInstanceClass().entrySet()) {
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
    private static <X extends BaseDao, Y extends X> void forceBind(Binder binder, Class<? extends BaseDao> interfaceClass, Class<? extends BaseDao> instanceClass) {
        Class<X> interfaceClassX = (Class<X>) interfaceClass;
        Class<Y> instanceClassY = (Class<Y>) instanceClass;
        binder.bind(interfaceClassX).to(instanceClassY);
    }
}
