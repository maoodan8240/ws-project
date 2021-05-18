package ws.gameServer.features.actor.register.utils.create.ext;


import ws.gameServer.features.actor.register.utils.create.ext.base.ExtCommonData;
import ws.gameServer.features.actor.register.utils.create.ext.base.ExtensionIniter;
import ws.relationship.topLevelPojos.payment.Payment;

//NewPve
public class _570_PaymentExtIniter implements ExtensionIniter {
    @Override
    public void init(ExtCommonData commonData) throws Exception {
        Payment payment = new Payment();
        payment.setPlayerId(commonData.getPlayer().getPlayerId());
        commonData.addPojo(payment);
    }
}
