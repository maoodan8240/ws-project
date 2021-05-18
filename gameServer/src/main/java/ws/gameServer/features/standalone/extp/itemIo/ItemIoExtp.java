package ws.gameServer.features.standalone.extp.itemIo;

import ws.common.utils.message.interfaces.InnerMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.itemIo.gm.ItemIoGmSupport;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.relationship.gm.GmCommandGroupNameConstants;
import ws.relationship.gm.In_GmCommand;
import ws.relationship.topLevelPojos.common.TopLevelHolder;

public class ItemIoExtp extends AbstractPlayerExtension<ItemIoCtrl> {
    public static boolean useExtension = true;

    private ItemIoGmSupport itemIoGmSupport;

    public ItemIoExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(ItemIoCtrl.class, TopLevelHolder.class);
        itemIoGmSupport = new ItemIoGmSupport(getControlerForQuery());
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
    public void onRecvInnerMsg(InnerMsg innerMsg) throws Exception {
        if (innerMsg instanceof In_GmCommand.Request) {
            onIm_GmCommand((In_GmCommand.Request) innerMsg);
        } else if (innerMsg instanceof In_DayChanged) {
            onDayChanged();
        }
    }

    private void onIm_GmCommand(In_GmCommand.Request im_GmCommand) {
        if (im_GmCommand.getGroupName().equals(GmCommandGroupNameConstants.ItemIo)) {
            itemIoGmSupport.exec(im_GmCommand.getFromType(), im_GmCommand.getCommandName(), im_GmCommand.getArgs());
        }
    }

    private void onDayChanged() throws Exception {
        getControlerForQuery()._resetDataAtDayChanged();
        getControlerForQuery().sync();
    }
}
