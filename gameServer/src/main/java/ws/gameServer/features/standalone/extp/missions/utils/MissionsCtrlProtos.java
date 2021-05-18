package ws.gameServer.features.standalone.extp.missions.utils;

import ws.gameServer.features.standalone.extp.dataCenter.msg.NotifyObj;
import ws.protos.MissionsProtos.Sm_Missions_Entry;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_Missions_Row;
import ws.relationship.topLevelPojos.mission.Mission;

import java.util.ArrayList;
import java.util.List;

public class MissionsCtrlProtos {

    public static List<Sm_Missions_Entry> create_Sm_Missions_Entry_List(List<Mission> missionList) {
        List<Sm_Missions_Entry> entries = new ArrayList<>();
        for (Mission m : missionList) {
            Table_Missions_Row row = RootTc.get(Table_Missions_Row.class, m.getMid());
            if (m.isGet() && !row.getDisplay()) { // 跳过完成后不显示的任务
                continue;
            }
            entries.add(create_Sm_Missions_Entry(row, m));
        }
        return entries;
    }

    public static Sm_Missions_Entry create_Sm_Missions_Entry(Table_Missions_Row row, Mission m) {
        Sm_Missions_Entry.Builder b = Sm_Missions_Entry.newBuilder();
        b.setMid(m.getMid());
        b.setValue(m.getValue());
        b.setDone(m.isGet());
        b.setCanGetRewards(canGetRewards(row, m));
        b.setFinishTime(m.getValue());
        b.setRemainDays(m.getRemainDays());
        return b.build();
    }

    /**
     * 是否可以领取奖励
     *
     * @param m
     * @return
     */
    private static boolean canGetRewards(Table_Missions_Row row, Mission m) {
        if (m.isGet()) {
            return false;
        }
        NotifyObj notifyObj = MissionsCtrlUtils.parseNotifyObj(row);
        if (m.getValue() >= notifyObj.getValue()) {
            return true;
        }
        return false;
    }

}
