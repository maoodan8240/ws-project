package ws.gameServer.features.standalone.extp.resourcePoint._module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.extp.resourcePoint.ctrl.ResourcePointCtrl;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.relationship.base.OpResult;

/**
 * 经验
 */
public class ActionOnExp implements ActionStateful {
    private static final Logger logger = LoggerFactory.getLogger(ActionOnExp.class);

    @Override
    public boolean canAdd(long value, ResourcePointCtrl ctrl) {
        return true;
    }

    @Override
    public OpResult add(long value, ResourcePointCtrl ctrl) {
        long before = ctrl.getResourcePoint(ResourceTypeEnum.RES_ROLE_EXP);
        ctrl.getTarget().getResourceTypeMapToValue().put(ResourceTypeEnum.RES_ROLE_EXP, before + value);
        ctrl.getPlayerCtrl().addExp(value);
        long after = ctrl.getResourcePoint(ResourceTypeEnum.RES_ROLE_EXP);
        return new OpResult(before, after);
    }

    @Override
    public boolean canReduce(long value, ResourcePointCtrl ctrl) {
        long curValue = ctrl.getResourcePoint(ResourceTypeEnum.RES_ROLE_EXP);
        return curValue >= value;
    }

    @Override
    public OpResult reduce(long value, ResourcePointCtrl ctrl) {
        long before = ctrl.getResourcePoint(ResourceTypeEnum.RES_ROLE_EXP);
        ctrl.getTarget().getResourceTypeMapToValue().put(ResourceTypeEnum.RES_ROLE_EXP, before - value);
        long after = ctrl.getResourcePoint(ResourceTypeEnum.RES_ROLE_EXP);
        if (after < 0) {
            ctrl.getTarget().getResourceTypeMapToValue().put(ResourceTypeEnum.RES_ROLE_EXP, 0l);
            logger.warn("playerId={} 角色经验出现了负数！ value={} ", ctrl.getTarget().getPlayerId(), after);
            after = 0l;
        }
        return new OpResult(before, after);
    }

    @Override
    public void setResourceType(ResourceTypeEnum resourceType) {

    }
}
