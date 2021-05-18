package ws.gameServer.features.standalone.extp.pyActivities.core.utils;

import ws.protos.PyActivitiesProtos.Sm_PyActivities_GroupAct;
import ws.protos.PyActivitiesProtos.Sm_PyActivities_RealmAct;
import ws.protos.PyActivitiesProtos.Sm_PyActivities_SubAct;
import ws.protos.PyActivitiesProtos.Sm_PyActivities_SubActObj;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_Activity_Realm_Row;
import ws.relationship.topLevelPojos.pyActivities.PyActivities;
import ws.relationship.topLevelPojos.pyActivities.PyGroupActDefault;
import ws.relationship.topLevelPojos.pyActivities.PySubAct;

import java.util.ArrayList;
import java.util.List;

public class PyActivitiesCtrlProtos {

    public static List<Sm_PyActivities_RealmAct> create_Sm_PyActivities_RealmAct_Lis(PyActivities pyActivities, int outerRealmId) {
        List<Sm_PyActivities_RealmAct> bs = new ArrayList<>();
        pyActivities.getRaIdToDefault().forEach((realmAcId, groupActDefault) -> {
            Sm_PyActivities_RealmAct.Builder builder = create_Sm_PyActivities_RealmAct(realmAcId, outerRealmId);
            builder.setGroupAct(create_Sm_PyActivities_GroupAct(groupActDefault));
            bs.add(builder.build());
        });
        pyActivities.getSpecialRaIds().forEach(specialRealmAcId -> {
            Sm_PyActivities_RealmAct.Builder builder = create_Sm_PyActivities_RealmAct(specialRealmAcId, outerRealmId);
            builder.setGroupAct(create_Sm_PyActivities_GroupAct(specialRealmAcId));
            bs.add(builder.build());
        });
        return bs;
    }

    public static Sm_PyActivities_RealmAct.Builder create_Sm_PyActivities_RealmAct(int realmAcId, int outerRealmId) {
        Sm_PyActivities_RealmAct.Builder b = Sm_PyActivities_RealmAct.newBuilder();
        Table_Activity_Realm_Row realmRow = RootTc.get(Table_Activity_Realm_Row.class, realmAcId);
        b.setRealmAcId(realmAcId);
        b.setStartTime(PyActivitiesCtrlUtils.getStartTime(realmRow, outerRealmId));
        b.setEndTime(PyActivitiesCtrlUtils.getEndTime(realmRow, outerRealmId));
        b.setGroupAct(create_Sm_PyActivities_GroupAct(realmAcId));
        return b;
    }


    public static Sm_PyActivities_GroupAct create_Sm_PyActivities_GroupAct(PyGroupActDefault groupActDefault) {
        Sm_PyActivities_GroupAct.Builder b = Sm_PyActivities_GroupAct.newBuilder();
        b.setGroupAcId(groupActDefault.getGroupAcId());
        groupActDefault.getIdToSubAct().forEach((id, subAct) -> {
            b.addSubActs(create_Sm_PyActivities_SubAct(subAct));
        });
        return b.build();
    }

    public static Sm_PyActivities_GroupAct create_Sm_PyActivities_GroupAct(int specialRealmAcId) {
        Table_Activity_Realm_Row realmRow = RootTc.get(Table_Activity_Realm_Row.class, specialRealmAcId);
        Sm_PyActivities_GroupAct.Builder b = Sm_PyActivities_GroupAct.newBuilder();
        b.setGroupAcId(realmRow.getActivityGroupId());
        return b.build();
    }


    public static Sm_PyActivities_SubAct create_Sm_PyActivities_SubAct(PySubAct subAct) {
        Sm_PyActivities_SubAct.Builder b = Sm_PyActivities_SubAct.newBuilder();
        b.setSubAcId(subAct.getSubAcId());
        b.setHasCompleteValue(subAct.getValue());
        b.setGet(subAct.isGet());
        return b.build();
    }

    public static Sm_PyActivities_SubActObj create_Sm_PyActivities_SubActObj(int realmAcId, int groupAcId, PySubAct subAct) {
        Sm_PyActivities_SubActObj.Builder b = Sm_PyActivities_SubActObj.newBuilder();
        b.setRealmAcId(realmAcId);
        b.setGroupAcId(groupAcId);
        b.setSubAc(create_Sm_PyActivities_SubAct(subAct));
        return b.build();
    }
}
