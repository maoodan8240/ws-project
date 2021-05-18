package ws.relationship.daos.sdk.loginRecord;

import ws.common.mongoDB.interfaces.BaseDao;
import ws.protos.EnumsProtos.PlatformTypeEnum;
import ws.relationship.topLevelPojos.sdk.loginRecord.PlayerLoginRecord;

public interface PlayerLoginRecordDao extends BaseDao<PlayerLoginRecord> {

    void updateLoginRecord(String playerId, int outerRealmId);

    PlayerLoginRecord queryByPlatformUid(String platformUid, PlatformTypeEnum platformType);
}
