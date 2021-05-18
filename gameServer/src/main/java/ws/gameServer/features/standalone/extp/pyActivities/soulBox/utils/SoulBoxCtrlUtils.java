package ws.gameServer.features.standalone.extp.pyActivities.soulBox.utils;

import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_SoulBox_Row;

public class SoulBoxCtrlUtils {

    public static Table_SoulBox_Row getSoulBoxRowById(int pickId) {
        Table_SoulBox_Row soulBoxRow = RootTc.get(Table_SoulBox_Row.class).get(pickId);
        return soulBoxRow;
    }
}
