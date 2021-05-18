package ws.gameServer.features.standalone.actor.player.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.extp.utils.UpgradeLevel;
import ws.relationship.table.tableRows.Table_Vip_Row;
import ws.relationship.topLevelPojos.common.LevelUpObj;

public class PlayerUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerUtils.class);


    /**
     * 玩家vip升级
     *
     * @param allVipExpOffered 当前总的vip经验值
     * @param maxVipLv
     * @return
     */
    public static LevelUpObj upPlayerVipLv(int allVipExpOffered, int maxVipLv) {
        LevelUpObj levelUpObj = new LevelUpObj(0, 0);
        UpgradeLevel.levelUpKeepOvf(levelUpObj, allVipExpOffered, maxVipLv, (oldLevel) -> {
            return Table_Vip_Row.getVipRow(oldLevel).getvIPExpAdd();
        });
        return levelUpObj;
    }
}


