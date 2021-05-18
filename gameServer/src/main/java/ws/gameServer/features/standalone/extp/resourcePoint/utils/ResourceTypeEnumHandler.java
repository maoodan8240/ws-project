package ws.gameServer.features.standalone.extp.resourcePoint.utils;

import ws.gameServer.features.standalone.extp.resourcePoint._module.ActionOnDefault;
import ws.gameServer.features.standalone.extp.resourcePoint._module.ActionOnEnergy;
import ws.gameServer.features.standalone.extp.resourcePoint._module.ActionOnExp;
import ws.gameServer.features.standalone.extp.resourcePoint._module.ActionOnMoney;
import ws.gameServer.features.standalone.extp.resourcePoint._module.ActionOnVipMoney;
import ws.gameServer.features.standalone.extp.resourcePoint._module.ActionStateful;
import ws.gameServer.features.standalone.extp.resourcePoint.ctrl.ResourcePointCtrl;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.relationship.base.OpResult;

import java.util.HashMap;
import java.util.Map;

public class ResourceTypeEnumHandler {
    /**
     * 金币
     */
    private final Handler MONEY = new Handler(ResourceTypeEnum.RES_MONEY, new ActionOnMoney());
    /**
     * 元宝
     */
    private final Handler RES_VIPMONEY = new Handler(ResourceTypeEnum.RES_VIPMONEY, new ActionOnVipMoney());
    /**
     * 体力
     */
    private final Handler RES_ENERGY = new Handler(ResourceTypeEnum.RES_ENERGY, new ActionOnEnergy());
    /**
     * 角色经验
     */
    private final Handler RES_ROLE_EXP = new Handler(ResourceTypeEnum.RES_ROLE_EXP, new ActionOnExp());
    /**
     * 默认处理
     */
    private final Handler DEFAULT = new Handler(null, new ActionOnDefault());

    private final Map<ResourceTypeEnum, Handler> ALL = new HashMap<ResourceTypeEnum, Handler>() {
        private static final long serialVersionUID = -6556174771294962543L;

        {
            put(MONEY.getResourceType(), MONEY);
            put(RES_VIPMONEY.getResourceType(), RES_VIPMONEY);
            put(RES_ENERGY.getResourceType(), RES_ENERGY);
            put(RES_ROLE_EXP.getResourceType(), RES_ROLE_EXP);
        }
    };


    public Handler parse(ResourceTypeEnum resourceTypeEnum) {
        LogicCheckUtils.requireNonNull(resourceTypeEnum, ResourceTypeEnum.class);
        for (Handler handler : ALL.values()) {
            if (handler.resourceType == resourceTypeEnum) {
                return handler;
            }
        }
        DEFAULT.getAction().setResourceType(resourceTypeEnum);
        return DEFAULT;
    }


    /**
     * 内部实际处理类
     */
    public class Handler {
        private ResourceTypeEnum resourceType;
        private ActionStateful actionStateful;

        private Handler(ResourceTypeEnum resourceType, ActionStateful actionStateful) {
            this.resourceType = resourceType;
            this.actionStateful = actionStateful;
        }

        public ResourceTypeEnum getResourceType() {
            return resourceType;
        }

        public ActionStateful getAction() {
            return actionStateful;
        }

        // ---------------------- 业务逻辑 开始 ----------------------------------

        public boolean canAdd(long value, ResourcePointCtrl ctrl) {
            return this.getAction().canAdd(value, ctrl);
        }

        public OpResult add(long value, ResourcePointCtrl ctrl) {
            return this.getAction().add(value, ctrl);
        }

        public boolean canReduce(long value, ResourcePointCtrl ctrl) {
            return this.getAction().canReduce(value, ctrl);
        }

        public OpResult reduce(long value, ResourcePointCtrl ctrl) {
            return this.getAction().reduce(value, ctrl);
        }

        // ---------------------- 业务逻辑 结束 ----------------------------------
    }
}
