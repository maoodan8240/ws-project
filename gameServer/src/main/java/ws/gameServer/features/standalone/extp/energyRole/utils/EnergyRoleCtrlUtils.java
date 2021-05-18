package ws.gameServer.features.standalone.extp.energyRole.utils;

import org.apache.commons.lang3.time.DateUtils;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.relationship.table.AllServerConfig;

public class EnergyRoleCtrlUtils {

    /**
     * 增涨的值
     *
     * @return
     */
    public static int getIncreaseValue() {
        TupleCell<Integer> cell = AllServerConfig.Energy_Auto_IncreaseSpeed.getConfig();
        return cell.get(TupleCell.SECOND);
    }

    /**
     * 增涨速度
     *
     * @return
     */
    public static long getIncreaseSpeed() {
        TupleCell<Integer> cell = AllServerConfig.Energy_Auto_IncreaseSpeed.getConfig();
        return cell.get(TupleCell.FIRST) * DateUtils.MILLIS_PER_MINUTE;
    }

}
