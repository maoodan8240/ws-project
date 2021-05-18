package ws.gameServer.features.standalone.extp.shops.gm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.extp.shops.ctrl.ShopsCtrl;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.PlayerProtos.Sm_GmCommand;
import ws.relationship.enums.GmCommandFromTypeEnum;
import ws.relationship.gm.GmCommand;
import ws.relationship.gm.GmCommandGroupNameConstants.ShopsGmSupportEnum;

public class ShopsGmSupport implements GmCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopsGmSupport.class);
    private ShopsCtrl shopsCtrl;

    public ShopsGmSupport(ShopsCtrl shopsCtrl) {
        this.shopsCtrl = shopsCtrl;
    }

    @Override
    public void exec(GmCommandFromTypeEnum fromTypeEnum, String commandName, String[] args) {
        if (ShopsGmSupportEnum.TriggerMysterious.getStr().equals(commandName)) {
            onTriggerMysterious(args);
        }
    }

    private void onTriggerMysterious(String[] args) {
        shopsCtrl.triggerMysteriousShop();
        SenderFunc.sendInner(shopsCtrl, Sm_GmCommand.class, Sm_GmCommand.Builder.class, Sm_GmCommand.Action.RESP_SEND, (b, br) -> {
        });
    }

}
