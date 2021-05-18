package ws.gameServer.features.standalone.extp.resourcePoint._module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.extp.resourcePoint.ctrl.ResourcePointCtrl;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.relationship.base.OpResult;

/**
 * 铜币
 */
public class ActionOnMoney implements ActionStateful {
    private static final Logger logger = LoggerFactory.getLogger(ActionOnMoney.class);

    @Override
    public boolean canAdd(long value, ResourcePointCtrl ctrl) {
        return true;
    }

    @Override
    public OpResult add(long value, ResourcePointCtrl ctrl) {
        long before = ctrl.getResourcePoint(ResourceTypeEnum.RES_MONEY);
        ctrl.getTarget().getResourceTypeMapToValue().put(ResourceTypeEnum.RES_MONEY, before + value);
        long after = ctrl.getResourcePoint(ResourceTypeEnum.RES_MONEY);
        return new OpResult(before, after);
    }

    @Override
    public boolean canReduce(long value, ResourcePointCtrl ctrl) {
        long curValue = ctrl.getResourcePoint(ResourceTypeEnum.RES_MONEY);
        return curValue >= value;
    }

    @Override
    public OpResult reduce(long value, ResourcePointCtrl ctrl) {
        long before = ctrl.getResourcePoint(ResourceTypeEnum.RES_MONEY);
        ctrl.getTarget().getResourceTypeMapToValue().put(ResourceTypeEnum.RES_MONEY, before - value);
        long after = ctrl.getResourcePoint(ResourceTypeEnum.RES_MONEY);
        if (after < 0) {
            ctrl.getTarget().getResourceTypeMapToValue().put(ResourceTypeEnum.RES_MONEY, 0l);
            logger.warn("playerId={} 铜币出现了负数！ value={} ", ctrl.getTarget().getPlayerId(), after);
            after = 0l;
        }
        return new OpResult(before, after);
    }

    @Override
    public void setResourceType(ResourceTypeEnum resourceType) {

    }
}
