package ws.gameServer.features.actor.register.utils.create.ext;

import ws.gameServer.features.actor.register.utils.create.ext.base.ExtCommonData;
import ws.gameServer.features.actor.register.utils.create.ext.base.ExtensionIniter;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.relationship.table.AllServerConfig;
import ws.relationship.topLevelPojos.resourcePoint.ResourcePoint;

public class _110_ResourcePointExtIniter implements ExtensionIniter {

    @Override
    public void init(ExtCommonData commonData) throws Exception {
        ResourcePoint resourcePoint = new ResourcePoint(commonData.getPlayer().getPlayerId());
        int initEnergy = AllServerConfig.PlayerInit_Energy.getConfig();
        resourcePoint.getResourceTypeMapToValue().put(ResourceTypeEnum.RES_ENERGY, (long) initEnergy);
        int initMoney = AllServerConfig.PlayerInit_Money.getConfig();
        resourcePoint.getResourceTypeMapToValue().put(ResourceTypeEnum.RES_MONEY, (long) initMoney);
        commonData.addPojo(resourcePoint);
    }
}
