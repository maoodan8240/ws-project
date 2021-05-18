package ws.gameServer.features.standalone.extp.shops;

import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.extp.shops.ctrl.ShopsCtrl;
import ws.gameServer.features.standalone.extp.shops.gm.ShopsGmSupport;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.ShopsProtos.Cm_Shops;
import ws.protos.ShopsProtos.Cm_Shops.Action;
import ws.protos.ShopsProtos.Sm_Shops;
import ws.relationship.gm.GmCommandGroupNameConstants;
import ws.relationship.gm.In_GmCommand;
import ws.relationship.topLevelPojos.shop.Shops;
import ws.relationship.utils.ProtoUtils;

/**
 * 商店系统
 */
public class ShopsExtp extends AbstractPlayerExtension<ShopsCtrl> {
    public static boolean useExtension = true;
    private ShopsGmSupport shopsGmSupport;

    public ShopsExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(ShopsCtrl.class, Shops.class);
        shopsGmSupport = new ShopsGmSupport(getControlerForQuery());
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
        if (clientMsg instanceof Cm_Shops) {
            Cm_Shops cm = (Cm_Shops) clientMsg;
            onCm_Shops(cm);
        }
    }


    private void onCm_Shops(Cm_Shops cm) throws Exception {
        Sm_Shops.Builder b = Sm_Shops.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_Shops.Action.SYNC_VALUE:
                    b.setAction(Sm_Shops.Action.RESP_SYNC);
                    sync();
                    break;
                case Action.BUY_VALUE:
                    b.setAction(Sm_Shops.Action.RESP_BUY);
                    buy(cm);
                    break;
                case Action.REFRESH_VALUE:
                    b.setAction(Sm_Shops.Action.RESP_REFRESH);
                    refresh(cm);
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_Shops, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
    }

    private void sync() {
        getControlerForQuery().sync();
    }

    private void buy(Cm_Shops cm) {
        getControlerForQuery().buy(cm.getShopType(), cm.getIdx(), cm.getCount());
    }

    private void refresh(Cm_Shops cm) {
        getControlerForQuery().refresh(cm.getShopType());
    }


    @Override
    public void onRecvInnerMsg(InnerMsg innerMsg) throws Exception {
        if (innerMsg instanceof In_GmCommand.Request) {
            onIm_GmCommand((In_GmCommand.Request) innerMsg);
        } else if (innerMsg instanceof In_DayChanged) {
            onDayChanged();
        }
    }

    @Override
    public void onRecvPrivateMsg(PrivateMsg privateMsg) throws Exception {
    }

    private void onIm_GmCommand(In_GmCommand.Request im_GmCommand) {
        if (im_GmCommand.getGroupName().equals(GmCommandGroupNameConstants.Shops)) {
            shopsGmSupport.exec(im_GmCommand.getFromType(), im_GmCommand.getCommandName(), im_GmCommand.getArgs());
        }
    }


    private void onDayChanged() throws Exception {
    }


}
