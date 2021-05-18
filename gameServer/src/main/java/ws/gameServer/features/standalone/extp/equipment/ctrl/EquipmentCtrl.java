package ws.gameServer.features.standalone.extp.equipment.ctrl;

import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.protos.EnumsProtos.EquipmentPositionEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.topLevelPojos.common.TopLevelHolder;

import java.util.List;

/**
 * Created by zb on 8/17/16.
 */
public interface EquipmentCtrl extends PlayerExteControler<TopLevelHolder> {


    // ============================Cm_Equipment start==================================

    /**
     * 装备位置ABCD 单次升级
     *
     * @param heroId
     * @param equipPos
     */
    void up_ABCD_OneLv(int heroId, EquipmentPositionEnum equipPos);

    /**
     * 装备位置ABCD 一键升级(尝试达到当前品级的最大等级)
     *
     * @param heroId
     * @param equipPos
     */
    void up_ABCD_OneKeyLv(int heroId, EquipmentPositionEnum equipPos);

    /**
     * <pre>
     * 装备位置ABCD 快速升级(尝试达到当前角色等级的最大等级)
     * 如：角色74级，最大可以达到的品级为紫色+4，需要的角色等级为70
     * </pre>
     *
     * @param heroId
     * @param equipPos
     * @param fastToLv
     */
    void up_ABCD_FastLv(int heroId, EquipmentPositionEnum equipPos, int fastToLv);

    /**
     * @param heroId
     * @param equipPos
     * @param consumeTpIds
     */
    void up_EF_SimpleLv(int heroId, EquipmentPositionEnum equipPos, List<IdAndCount> consumeTpIds);

    /**
     * @param heroId
     * @param equipPos
     * @param fastToLv
     */
    void up_EF_FastLv(int heroId, EquipmentPositionEnum equipPos, int fastToLv);

    /**
     * 升品
     *
     * @param heroId
     * @param equipPos
     * @return
     */
    public void upgradeQuality(int heroId, EquipmentPositionEnum equipPos);

    /**
     * 升星/觉醒
     *
     * @param heroId
     * @param equipPos
     */
    void upgradeStar(int heroId, EquipmentPositionEnum equipPos);


    /**
     * 将星
     *
     * @param heroId
     * @param equipPos
     */
    void reduceStar(int heroId, EquipmentPositionEnum equipPos);
    // ============================Cm_Equipment end==================================


}
