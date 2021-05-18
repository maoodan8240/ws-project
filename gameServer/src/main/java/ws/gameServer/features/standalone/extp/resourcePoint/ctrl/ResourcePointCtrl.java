package ws.gameServer.features.standalone.extp.resourcePoint.ctrl;

import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.topLevelPojos.resourcePoint.ResourcePoint;

public interface ResourcePointCtrl extends PlayerExteControler<ResourcePoint> {

    /**
     * 获得某个ResourceType的资源点数
     *
     * @param resourceType
     * @return
     */
    long getResourcePoint(ResourceTypeEnum resourceType);

    /**
     * 是否可以增加资源，以组的形式增加，要么都成功要么都失败
     *
     * @param
     * @return
     */
    boolean canAddResourcePoint(IdMaptoCount idMaptoCount);

    /**
     * 增加资源, 同类型叠加
     *
     * @param
     */
    IdMaptoCount addResourcePoint(IdMaptoCount idMaptoCount);

    /**
     * 是否可以减少资源点, 同类型叠加
     *
     * @param
     * @return
     */
    boolean canReduceResourcePoint(IdMaptoCount idMaptoCount);

    /**
     * 减少资源,通过ResourceType.parse(value)获得ResourceType
     *
     * @param
     * @param
     */
    IdMaptoCount reduceResourcePoint(IdMaptoCount idMaptoCount);
}
