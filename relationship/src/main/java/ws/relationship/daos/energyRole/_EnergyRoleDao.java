package ws.relationship.daos.energyRole;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.energyRole.EnergyRole;

public class _EnergyRoleDao extends AbstractBaseDao<EnergyRole> implements EnergyRoleDao {

    public _EnergyRoleDao() {
        super(EnergyRole.class);
    }
}
