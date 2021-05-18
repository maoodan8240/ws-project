package ws.relationship.logServer.daos;

import ws.common.mongoDB.interfaces.BaseDao;
import ws.common.mongoDB.interfaces.TopLevelPojo;
import ws.common.utils.di.GlobalInjector;
import ws.relationship.exception.BusinessLogicMismatchConditionException;

public class LogDaoContainer {

    @SuppressWarnings("unchecked")
    public static <K extends BaseDao> K getDao(Class<? extends TopLevelPojo> clazz) {
        if (AllLogDaoClassHolder.containsTopLevelPojoClass(clazz)) {
            Class<? extends BaseDao> daoClass = AllLogDaoClassHolder.getTopLevelPojoClassToInstanceClass().get(clazz);
            return (K) GlobalInjector.getInstance(daoClass);
        } else {
            String msg = String.format("无法获取clazz=%s 对应匹配的Dao !", clazz);
            throw new BusinessLogicMismatchConditionException(msg);
        }
    }
}
