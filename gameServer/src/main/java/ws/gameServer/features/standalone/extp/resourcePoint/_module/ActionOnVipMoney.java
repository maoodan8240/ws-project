package ws.gameServer.features.standalone.extp.resourcePoint._module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.extp.resourcePoint.ctrl.ResourcePointCtrl;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.relationship.base.OpResult;

/**
 * 元宝
 */
public class ActionOnVipMoney implements ActionStateful {
    private static final Logger logger = LoggerFactory.getLogger(ActionOnVipMoney.class);

    @Override
    public boolean canAdd(long value, ResourcePointCtrl ctrl) {
        return true;
    }

    @Override
    public OpResult add(long value, ResourcePointCtrl ctrl) {
        long before = ctrl.getResourcePoint(ResourceTypeEnum.RES_VIPMONEY);
        ctrl.getTarget().getResourceTypeMapToValue().put(ResourceTypeEnum.RES_VIPMONEY, before + value);
        long after = ctrl.getResourcePoint(ResourceTypeEnum.RES_VIPMONEY);
        return new OpResult(before, after);
    }

    @Override
    public boolean canReduce(long value, ResourcePointCtrl ctrl) {
        long curValue = ctrl.getResourcePoint(ResourceTypeEnum.RES_VIPMONEY);
        return curValue >= value;
    }

    @Override
    public OpResult reduce(long value, ResourcePointCtrl ctrl) {
        long before = ctrl.getResourcePoint(ResourceTypeEnum.RES_VIPMONEY);
        ctrl.getTarget().getResourceTypeMapToValue().put(ResourceTypeEnum.RES_VIPMONEY, before - value);
        long after = ctrl.getResourcePoint(ResourceTypeEnum.RES_VIPMONEY);
        if (after < 0) {
            ctrl.getTarget().getResourceTypeMapToValue().put(ResourceTypeEnum.RES_VIPMONEY, 0l);
            logger.warn("playerId={} VIPMoney出现了负数！ value={} ", ctrl.getTarget().getPlayerId(), after);
            after = 0l;
        }
        long newValue = ctrl.getResourcePoint(ResourceTypeEnum.RES_VIPMONEY);
        statisticsDaliyConsumes((before - newValue), ctrl);
        return new OpResult(before, after);
    }

    @Override
    public void setResourceType(ResourceTypeEnum resourceType) {

    }


    // ======================== 数据采集 start start start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private void statisticsDaliyConsumes(long value, ResourcePointCtrl ctrl) {
//        Pr_NotifyMsg notifyMsg2 = new Pr_NotifyMsg(PrivateNotifyTypeEnum.ResourcePoint_DaliyConsumes, ResourceTypeEnum.RES_VIPMONEY_VALUE, value);
//        ctrl.sendPrivateMsg(notifyMsg2);
    }

    // ======================== 数据采集 end end end <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


}
