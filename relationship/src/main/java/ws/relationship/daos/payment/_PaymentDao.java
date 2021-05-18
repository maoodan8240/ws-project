package ws.relationship.daos.payment;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.payment.Payment;

public class _PaymentDao extends AbstractBaseDao<Payment> implements PaymentDao {

    public _PaymentDao() {
        super(Payment.class);
    }
}
