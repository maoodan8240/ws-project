package ws.gameServer.features.standalone.extp.resourcePoint.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.resourcePoint.utils.ResourceTypeEnumHandler;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PlayerProtos.Sm_ResourcePoint;
import ws.protos.PlayerProtos.Sm_ResourcePoint.Action;
import ws.protos.PlayerProtos.Sm_ResourcePointPo;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.OpResult;
import ws.relationship.topLevelPojos.resourcePoint.ResourcePoint;
import ws.relationship.utils.ProtoUtils;

import java.util.Objects;

public class _ResourcePointCtrl extends AbstractPlayerExteControler<ResourcePoint> implements ResourcePointCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_ResourcePointCtrl.class);
    private final ResourceTypeEnumHandler handler = new ResourceTypeEnumHandler();

    @Override
    public void _initReference() throws Exception {
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
    }

    @Override
    public void sync() {
        SenderFunc.sendInner(this, Sm_ResourcePoint.class, Sm_ResourcePoint.Builder.class, Sm_ResourcePoint.Action.RESP_SYNC, (b, br) -> {
            fill_Sm_ResourcePoint(b);
        });
    }

    @Override
    public void refreshItem(IdMaptoCount idMaptoCount) {
        Response.Builder br = ProtoUtils.create_Response(Code.Sm_ResourcePoint, Action.RESP_SYNC);
        Sm_ResourcePoint.Builder b = create_Sm_ResourcePoint();
        br.setSmResourcePointSyncPart(b);
        br.setResult(true);
        send(br.build());
    }

    @Override
    public void refreshItemAddToResponse(IdMaptoCount idMaptoCount, Response.Builder br) {
        br.setSmResourcePointSyncPart(create_Sm_ResourcePoint());
    }

    private Sm_ResourcePoint.Builder fill_Sm_ResourcePoint(Sm_ResourcePoint.Builder b) {
        for (ResourceTypeEnum resourceType : ResourceTypeEnum.values()) {
            long value = getResourceValue(resourceType);
            Sm_ResourcePointPo.Builder resBuilder = Sm_ResourcePointPo.newBuilder();
            resBuilder.setItemId(resourceType.getNumber());
            resBuilder.setCount(value);
            b.addPoints(resBuilder);
        }
        return b;
    }

    private Sm_ResourcePoint.Builder create_Sm_ResourcePoint() {
        Sm_ResourcePoint.Builder b = Sm_ResourcePoint.newBuilder();
        b.setAction(Action.RESP_SYNC);
        fill_Sm_ResourcePoint(b);
        return b;
    }

    @Override
    public long getResourcePoint(ResourceTypeEnum resourceType) {
        Objects.requireNonNull(resourceType);
        return getResourceValue(resourceType);
    }

    @Override
    public boolean canAddResourcePoint(IdMaptoCount idMaptoCount) {
        _logicCheck_ArgumentIdMaptoCount(idMaptoCount);
        for (IdAndCount idAndCount : idMaptoCount.getAll()) {
            ResourceTypeEnum resourceType = ResourceTypeEnum.valueOf(idAndCount.getId());
            if (!handler.parse(resourceType).canAdd(idAndCount.getCount(), this)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public IdMaptoCount addResourcePoint(IdMaptoCount idMaptoCount) {
        IdMaptoCount refresh_IdMaptoCount = new IdMaptoCount();
        for (IdAndCount idAndCount : idMaptoCount.getAll()) {
            ResourceTypeEnum resourceType = ResourceTypeEnum.valueOf(idAndCount.getId());
            OpResult result = handler.parse(resourceType).add(idAndCount.getCount(), this);
            refresh_IdMaptoCount.add(new IdAndCount(resourceType.getNumber(), result.getChange()));
        }
        save();
        LOGGER.debug("----------[增加]资源项为： {}", idMaptoCount);
        return refresh_IdMaptoCount;
    }

    @Override
    public boolean canReduceResourcePoint(IdMaptoCount idMaptoCount) {
        _logicCheck_ArgumentIdMaptoCount(idMaptoCount);
        for (IdAndCount idAndCount : idMaptoCount.getAll()) {
            ResourceTypeEnum resourceType = ResourceTypeEnum.valueOf(idAndCount.getId());
            if (!handler.parse(resourceType).canReduce(idAndCount.getCount(), this)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public IdMaptoCount reduceResourcePoint(IdMaptoCount idMaptoCount) {
        IdMaptoCount refresh_IdMaptoCount = new IdMaptoCount();
        for (IdAndCount idAndCount : idMaptoCount.getAll()) {
            ResourceTypeEnum resourceType = ResourceTypeEnum.valueOf(idAndCount.getId());
            OpResult result = handler.parse(resourceType).reduce(idAndCount.getCount(), this);
            refresh_IdMaptoCount.add(new IdAndCount(resourceType.getNumber(), result.getChange()));
        }
        save();
        LOGGER.debug("----------[扣除]资源项为： {}", idMaptoCount);
        return refresh_IdMaptoCount;
    }

    private boolean contains(ResourceTypeEnum resourceType) {
        return target.getResourceTypeMapToValue().containsKey(resourceType);
    }

    private long getResourceValue(ResourceTypeEnum resourceType) {
        return contains(resourceType) ? target.getResourceTypeMapToValue().get(resourceType) : 0;
    }

    /**
     * 检测参数
     */
    private void _logicCheck_ArgumentIdMaptoCount(IdMaptoCount idMaptoCount) {
        Objects.requireNonNull(idMaptoCount);
        for (IdAndCount idAndCount : idMaptoCount.getAll()) {
            ResourceTypeEnum resourceType = ResourceTypeEnum.valueOf(idAndCount.getId());
            if (resourceType == null) {
                String msg = String.format("检测的对象idMaptoCount=%s 其中idAndCount=%s的id不为资源类型id！", idMaptoCount, idAndCount);
                throw new IllegalArgumentException(msg);
            }
            if (idAndCount.getCount() <= 0) {
                String msg = String.format("检测的对象idMaptoCount=%s 其中idAndCount=%s的count不大于0！", idMaptoCount, idAndCount);
                throw new IllegalArgumentException(msg);
            }
        }
    }
}
