package ws.relationship.utils;

import org.apache.commons.lang3.StringUtils;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.utils.di.GlobalInjector;
import ws.relationship.base.MagicNumbers;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.daos.DaoContainer;
import ws.relationship.daos.names.NamesDao;
import ws.relationship.table.tableRows.Table_NameLibrary_Row;
import ws.relationship.topLevelPojos.names.Names;

/**
 * Created by zhangweiwei on 17-6-6.
 */
public class NameUtils {
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private static final NamesDao NAMES_DAO = DaoContainer.getDao(Names.class);

    static {
        NAMES_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
    }


    /**
     * 随机生成玩家Game名字
     *
     * @return
     */
    public static String random() {
        for (int i = 0; i < MagicNumbers.DEFAULT_100; i++) { //尝试100次
            String name = Table_NameLibrary_Row.randomName();
            if (NAMES_DAO.inertNewName(name)) {
                return name;
            }
        }
        return null;
    }


    /**
     * 尝试插入新的名字
     *
     * @param newName
     * @return
     */
    public static boolean inertNewName(String newName) {
        if (StringUtils.isBlank(newName)) {
            return false;
        }
        if (NAMES_DAO.inertNewName(newName)) {
            return true;
        }
        return false;
    }

}
