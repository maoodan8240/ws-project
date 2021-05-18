package ws.gameServer.features.standalone.extp.itemIo.gm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.common.table.table.utils.CellTypeEnum;
import ws.common.table.table.utils.CellUtils;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PlayerProtos.Sm_GmCommand;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.resultCode.ResultCodeEnum;
import ws.relationship.enums.GmCommandFromTypeEnum;
import ws.relationship.gm.GmCommand;
import ws.relationship.gm.GmCommandGroupNameConstants;
import ws.relationship.gm.In_GmCommand;

public class ItemIoGmSupport implements GmCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemIoGmSupport.class);
    private ItemIoCtrl itemIoCtrl;

    public ItemIoGmSupport(ItemIoCtrl itemIoCtrl) {
        this.itemIoCtrl = itemIoCtrl;
    }

    @Override
    public void exec(GmCommandFromTypeEnum fromTypeEnum, String commandName, String[] args) {
        if (GmCommandGroupNameConstants.ItemIoGmSupport.Op.getStr().equals(commandName)) {
            onOp(fromTypeEnum, args);
        }
    }

    @SuppressWarnings("unchecked")
    private void onOp(GmCommandFromTypeEnum fromTypeEnum, String[] args) {
        IdMaptoCount ret = new IdMaptoCount();
        ResultCodeEnum resultCode = ResultCodeEnum.SUCCESS;
        try {
            TupleListCell<Integer> tupleListCell = (TupleListCell<Integer>) CellUtils.parse(CellTypeEnum.ARRAY_INT_2.getName(), args[0].replaceAll(" ", ""));
            IdMaptoCount idMaptoCountAdd = new IdMaptoCount();
            IdMaptoCount idMaptoCountReduce = new IdMaptoCount();
            for (TupleCell<Integer> cell : tupleListCell.getAll()) {
                int id = cell.get(TupleCell.FIRST);
                int count = cell.get(TupleCell.SECOND);
                if (count < 0) {
                    idMaptoCountReduce.add(new IdAndCount(id, count * -1));
                } else if (count > 0) {
                    idMaptoCountAdd.add(new IdAndCount(id, count));
                }
            }
            onAddOp(idMaptoCountAdd, ret);
            onReduceOp(idMaptoCountReduce, ret);
        } catch (Exception e) {
            LOGGER.error("", e);
            resultCode = ResultCodeEnum.ERR_UNKNOWN;
        }
        Response response = SenderFunc.buildResponse(Sm_GmCommand.class, Sm_GmCommand.Builder.class, Sm_GmCommand.Action.RESP_SEND, (b, br) -> {
            if (ret.getAll().size() == 0) {
                br.setResult(false);
            } else {
                itemIoCtrl.refreshItemAddToResponse(ret, br);
            }
        });
        itemIoCtrl.send(response);
        if (fromTypeEnum == GmCommandFromTypeEnum.MANAGER) {
            itemIoCtrl.getPlayerCtrl().getCurSendActorRef().tell(new In_GmCommand.Response(resultCode), itemIoCtrl.getPlayerCtrl().getActorRef());
        }
    }

    private void onAddOp(IdMaptoCount idMaptoCount, IdMaptoCount ret) {
        if (idMaptoCount.getAll().size() > 0 && itemIoCtrl.canAdd(idMaptoCount)) {
            itemIoCtrl.setCallerAction(GmCommandGroupNameConstants.ItemIoGmSupport.Op);
            ret.addAll(itemIoCtrl.addItem(idMaptoCount));
        }
    }

    private void onReduceOp(IdMaptoCount idMaptoCount, IdMaptoCount ret) {
        if (idMaptoCount.getAll().size() > 0 && itemIoCtrl.canRemove(idMaptoCount)) {
            itemIoCtrl.setCallerAction(GmCommandGroupNameConstants.ItemIoGmSupport.Op);
            ret.addAll(itemIoCtrl.removeItem(idMaptoCount));
        }
    }

}
