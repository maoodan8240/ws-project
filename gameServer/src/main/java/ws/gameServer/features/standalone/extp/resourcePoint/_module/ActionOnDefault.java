package ws.gameServer.features.standalone.extp.resourcePoint._module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.extp.resourcePoint.ctrl.ResourcePointCtrl;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.relationship.base.OpResult;


public class ActionOnDefault implements ActionStateful {
    private static final Logger logger = LoggerFactory.getLogger(ActionOnDefault.class);
    private ResourceTypeEnum resourceType;

    @Override
    public boolean canAdd(long value, ResourcePointCtrl ctrl) {
        return true;
    }

    @Override
    public OpResult add(long value, ResourcePointCtrl ctrl) {
        long before = ctrl.getResourcePoint(resourceType);
        ctrl.getTarget().getResourceTypeMapToValue().put(resourceType, before + value);
        long after = ctrl.getResourcePoint(resourceType);
        return new OpResult(before, after);
    }

    @Override
    public boolean canReduce(long value, ResourcePointCtrl ctrl) {
        long curValue = ctrl.getResourcePoint(resourceType);
        return curValue >= value;
    }

    @Override
    public OpResult reduce(long value, ResourcePointCtrl ctrl) {
        long before = ctrl.getResourcePoint(resourceType);
        ctrl.getTarget().getResourceTypeMapToValue().put(resourceType, before - value);
        long after = ctrl.getResourcePoint(resourceType);
        if (after < 0) {
            ctrl.getTarget().getResourceTypeMapToValue().put(resourceType, 0l);
            logger.warn("playerId={} resourceType={}出现了负数！ value={} ", ctrl.getTarget().getPlayerId(), resourceType, after);
            after = 0l;
        }
        return new OpResult(before, after);
    }


    public void setResourceType(ResourceTypeEnum resourceType) {
        this.resourceType = resourceType;
    }
}
