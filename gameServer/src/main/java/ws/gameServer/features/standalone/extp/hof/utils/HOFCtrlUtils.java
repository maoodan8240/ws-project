package ws.gameServer.features.standalone.extp.hof.utils;

import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.protos.CommonProtos.Sm_Common_IdAndCount;
import ws.protos.CommonProtos.Sm_Common_IdMaptoCount;
import ws.protos.HOFProtos.Cm_HOF_HeroAndFood;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.exception.CmMessageIllegalArgumentException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_Favorable_Row;
import ws.relationship.table.tableRows.Table_HOF_Row;
import ws.relationship.table.tableRows.Table_Item_Row;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HOFCtrlUtils {
    public static Table_HOF_Row getHOFRow(int heroId) {
        if (!RootTc.get(Table_HOF_Row.class).has(heroId)) {
            String msg = String.format("Table_HOF_Row heroId=%s 不存在", heroId);
            throw new CmMessageIllegalArgumentException(msg);
        }
        return RootTc.get(Table_HOF_Row.class).get(heroId);
    }

    public static Table_Item_Row getItemRow(int itemId) {
        if (!RootTc.get(Table_Item_Row.class).has(itemId)) {
            String msg = String.format("Table_Item_Row itemId=%s 不存在", itemId);
            throw new CmMessageIllegalArgumentException(msg);
        }
        return RootTc.get(Table_Item_Row.class).get(itemId);
    }


    public static Table_Favorable_Row getFavorableRow(int favorableId) {
        if (!RootTc.get(Table_Favorable_Row.class).has(favorableId)) {
            String msg = String.format("Table_HOF_Row heroId=%s 不存在", favorableId);
            throw new CmMessageIllegalArgumentException(msg);
        }
        return RootTc.get(Table_Favorable_Row.class).get(favorableId);
    }

    public static boolean isHOFOpened(int playerLv) {
        return playerLv >= (int) AllServerConfig.HOF_OpenLevel.getConfig();
    }

    public static Map<Integer, IdMaptoCount> getHeroIdToFoodIds(List<Cm_HOF_HeroAndFood> heroAndFoodList) {
        Map<Integer, IdMaptoCount> heroIdToFoodIds = new HashMap<>();
        for (Cm_HOF_HeroAndFood args : heroAndFoodList) {
            int heroId = args.getHeroId();
            LogicCheckUtils.validateParam(Integer.class, heroId);
            Sm_Common_IdMaptoCount foodIdsAndCount = args.getFoodIdAndCount();
            heroIdToFoodIds.put(heroId, parseFoodIdsAndCountoIdMapToCount(foodIdsAndCount));
        }
        return heroIdToFoodIds;
    }

    public static IdMaptoCount parseFoodIdsAndCountoIdMapToCount(Sm_Common_IdMaptoCount foodIdsAndCount) {
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        List<Sm_Common_IdAndCount> list = foodIdsAndCount.getIdAndCountsList();
        for (Sm_Common_IdAndCount entry : list) {
            LogicCheckUtils.validateParam(Integer.class, entry.getId());
            LogicCheckUtils.validateParam(Long.class, entry.getCount());
            idMaptoCount.add(new IdAndCount(entry.getId(), entry.getCount()));
        }
        return idMaptoCount;
    }

}
