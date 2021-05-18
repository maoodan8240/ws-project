package ws.gameServer.features.standalone.extp.resourcePoint._module;

import ws.gameServer.features.standalone.extp.resourcePoint.ctrl.ResourcePointCtrl;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.relationship.base.OpResult;

/**
 * 该类的实现类可以有状态
 */
public interface ActionStateful {

    void setResourceType(ResourceTypeEnum resourceType);

    /**
     * 判断是否能够增加
     *
     * @param value
     * @param ctrl
     * @return true:能够增加
     */
    boolean canAdd(long value, ResourcePointCtrl ctrl);

    /**
     * 增加
     *
     * @param value
     * @param ctrl
     * @return 增加的值
     */
    OpResult add(long value, ResourcePointCtrl ctrl);

    /**
     * 判断是否能够扣除
     *
     * @param value
     * @param ctrl
     * @return true:能够扣除
     */
    boolean canReduce(long value, ResourcePointCtrl ctrl);

    /**
     * 扣除
     *
     * @param value
     * @param ctrl
     * @return 扣除后剩余的值
     */
    OpResult reduce(long value, ResourcePointCtrl ctrl);
}
