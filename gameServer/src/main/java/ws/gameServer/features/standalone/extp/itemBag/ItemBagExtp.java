package ws.gameServer.features.standalone.extp.itemBag;

import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.itemBag.ctrl.ItemBagCtrl;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.ItemBagProtos.Cm_ItemBag;
import ws.protos.ItemBagProtos.Sm_ItemBag;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.topLevelPojos.itemBag.ItemBag;
import ws.relationship.utils.ProtoUtils;

public class ItemBagExtp extends AbstractPlayerExtension<ItemBagCtrl> {
    public static boolean useExtension = true;
    
    public ItemBagExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(ItemBagCtrl.class, ItemBag.class);
    }

    @Override
    public void _initReference() throws Exception {
        getControlerForQuery()._initReference();
    }

    @Override
    public void _postInit() throws Exception {
        getControlerForQuery()._initAll();
    }

    @Override
    public void onRecvMyNetworkMsg(Message clientMsg) throws Exception {
        if (clientMsg instanceof Cm_ItemBag) {

            Cm_ItemBag cm = (Cm_ItemBag) clientMsg;
            onCm_ItemBag(cm);
        }
    }

    @Override
    public void onRecvInnerMsg(InnerMsg innerMsg) throws Exception {
        if (innerMsg instanceof In_PlayerLoginedRequest) {
            onPlayerLogined((In_PlayerLoginedRequest) innerMsg);
        } else if (innerMsg instanceof In_DayChanged) {
            onDayChanged();
        }
    }

    private void onCm_ItemBag(Cm_ItemBag cm) throws Exception {
        Sm_ItemBag.Builder b = Sm_ItemBag.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_ItemBag.Action.SYNC_VALUE:
                    b.setAction(Sm_ItemBag.Action.RESP_SYNC);
                    sync();
                    break;
                case Cm_ItemBag.Action.USE_ITEM_VALUE:
                    b.setAction(Sm_ItemBag.Action.RESP_USE_ITEM);
                    useItem(cm);
                    break;
                case Cm_ItemBag.Action.SELL_ITEM_VALUE:
                    b.setAction(Sm_ItemBag.Action.RESP_SELL_ITEM);
                    sellItem(cm);
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_ItemBag, b.getAction());

            getControlerForQuery().send(br.build());
            throw e;
        }
    }

    private void onDayChanged() throws Exception {
        getControlerForQuery()._resetDataAtDayChanged();
        getControlerForQuery().sync();
    }

    private void onPlayerLogined(In_PlayerLoginedRequest im_PlayerLogined) throws Exception {
        if (im_PlayerLogined.getPlayerId().equals(ownerCtrl.getTarget().getPlayerId())) {
            sync();
        }
    }

    private void sync() throws Exception {
        getControlerForQuery().sync();
    }

    private void useItem(Cm_ItemBag cm) {
        getControlerForQuery().useItem(cm.getId(), cm.getCount());
    }


    private void sellItem(Cm_ItemBag cm) {
        getControlerForQuery().sellItem(cm.getId(), cm.getCount());
    }
}
