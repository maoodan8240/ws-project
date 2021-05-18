package ws.particularFunctionServer.system.table;

import ws.common.table.table.implement.AbstractRow;
import ws.common.utils.classProcess.ClassFinder;
import ws.relationship.table.tableRows.Table_New_Buff_Row;

import java.util.ArrayList;
import java.util.List;

public class RowsClassHolder {

    private static List<Class<? extends AbstractRow>> abstractRowClasses = null;

    static {
        abstractRowClasses = ClassFinder.getAllAssignedClass(AbstractRow.class, Table_New_Buff_Row.class);
    }

    public static List<Class<? extends AbstractRow>> getAbstractRowClasses() {
        return new ArrayList<>(abstractRowClasses);
    }
}
