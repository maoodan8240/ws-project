import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import ws.common.table.data.PlanningTableData;
import ws.common.utils.dataSource.txt._PlanningTableData;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.formula.FormulaContainer;
import ws.common.utils.formula._FormulaContainer;
import ws.gameServer.features.standalone.extp.pve2.utils.NewPveCtrlUtils;
import ws.gameServer.features.standalone.extp.ultimateTest.utils.UltimateTestCtrlUtils;
import ws.gameServer.system.config.AppConfig;
import ws.gameServer.system.table.RootTcListener;
import ws.protos.EnumsProtos.UltimateTestBuffIndexTypeEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.LauncherInitException;
import ws.relationship.table.RootTc;
import ws.relationship.topLevelPojos.ultimateTest.UltimatetestBuff;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by lee on 8/17/17.
 */
public class Test2 {

    public static void main(String[] args) {
        try {
            AppConfig.init();
            Injector injector = Guice.createInjector(binder -> {
                binder.bind(FormulaContainer.class).to(_FormulaContainer.class).in(Scopes.SINGLETON);
            });
            GlobalInjector.setDefaultInjector(injector);
            _initPlanningTableData();
            Map<UltimateTestBuffIndexTypeEnum, UltimatetestBuff> buffTypeAndBuff = UltimateTestCtrlUtils.randomBuff(6);
            for (Entry<UltimateTestBuffIndexTypeEnum, UltimatetestBuff> entry : buffTypeAndBuff.entrySet()) {
                System.out.println("value:" + entry.getKey().getNumber() + ",key:" + entry.getValue().getBuffId());
            }
        } catch (Exception e) {
            throw new LauncherInitException("初始化异常！", e);
        }

    }


    private static void _initPlanningTableData() throws Exception {
        PlanningTableData planningTableData = new _PlanningTableData(AppConfig.getString(AppConfig.Key.WS_Common_Config_planningTableData_tab_file_path));
        planningTableData.loadAllTablesData();
        RootTc.init(planningTableData, new RootTcListener());
        RootTc.loadAllTables();
    }


    /**
     * 一次性获取扫荡多次的物品
     *
     * @param protoIdMapToCount
     * @param stageId
     * @param times
     * @return
     */
    private static IdMaptoCount _getDropByTimes(List<IdMaptoCount> protoIdMapToCount, int stageId, int times) {
        IdMaptoCount addIdMaptoCount = new IdMaptoCount();
        for (int i = MagicNumbers.DEFAULT_ONE; i <= times; i++) {
            IdMaptoCount drop = NewPveCtrlUtils.getStageDrop(stageId, 25);
            IdMaptoCount activityDrop = NewPveCtrlUtils.getActivityDrop(stageId, 25);
            IdAndCount exp = NewPveCtrlUtils.getFinishStageRoleExp(stageId);
            IdAndCount money = NewPveCtrlUtils.getFinishStageMoney(stageId);
            protoIdMapToCount.add(drop);
            protoIdMapToCount.add(activityDrop);
            addIdMaptoCount.addAll(drop.add(money).add(exp)).addAll(activityDrop);
        }
        System.out.println(addIdMaptoCount);

        return addIdMaptoCount;
    }
}
