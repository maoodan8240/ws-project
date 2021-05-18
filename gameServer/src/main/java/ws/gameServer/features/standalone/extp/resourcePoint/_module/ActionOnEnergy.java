package ws.gameServer.features.standalone.extp.resourcePoint._module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.extp.resourcePoint.ctrl.ResourcePointCtrl;
import ws.gameServer.features.standalone.extp.resourcePoint.msg.Pr_EnergyValueChanged;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.relationship.base.OpResult;
import ws.relationship.table.AllServerConfig;

/**
 * 体力
 */
public class ActionOnEnergy implements ActionStateful {
    private static final Logger logger = LoggerFactory.getLogger(ActionOnEnergy.class);

    @Override
    public boolean canAdd(long value, ResourcePointCtrl ctrl) {
        return true;
    }

    @Override
    public OpResult add(long value, ResourcePointCtrl ctrl) {
        long before = ctrl.getResourcePoint(ResourceTypeEnum.RES_ENERGY);
        int maxEnergy = AllServerConfig.Energy_MaxEnergy.getConfig();
        long toAdd = Math.min(maxEnergy, (before + value)) - before;
        ctrl.getTarget().getResourceTypeMapToValue().put(ResourceTypeEnum.RES_ENERGY, before + toAdd);
        long after = ctrl.getResourcePoint(ResourceTypeEnum.RES_ENERGY);
        ctrl.sendPrivateMsg(new Pr_EnergyValueChanged(before, after));
        return new OpResult(before, after);
    }

    @Override
    public boolean canReduce(long value, ResourcePointCtrl ctrl) {
        long curValue = ctrl.getResourcePoint(ResourceTypeEnum.RES_ENERGY);
        return curValue >= value;
    }

    @Override
    public OpResult reduce(long value, ResourcePointCtrl ctrl) {
        long before = ctrl.getResourcePoint(ResourceTypeEnum.RES_ENERGY);
        ctrl.getTarget().getResourceTypeMapToValue().put(ResourceTypeEnum.RES_ENERGY, before - value);
        long after = ctrl.getResourcePoint(ResourceTypeEnum.RES_ENERGY);
        if (after < 0) {
            ctrl.getTarget().getResourceTypeMapToValue().put(ResourceTypeEnum.RES_ENERGY, 0l);
            logger.warn("playerId={} 体力出现了负数！ value={} ", ctrl.getTarget().getPlayerId(), after);
            after = 0l;
        }
        long now = ctrl.getResourcePoint(ResourceTypeEnum.RES_ENERGY);
        ctrl.sendPrivateMsg(new Pr_EnergyValueChanged(before, now));
        return new OpResult(before, after);
    }

    @Override
    public void setResourceType(ResourceTypeEnum resourceType) {

    }
}
