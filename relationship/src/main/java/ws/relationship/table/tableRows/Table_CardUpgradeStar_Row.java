package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.ListCell;
import ws.common.table.table.utils.CellParser;
import ws.protos.EnumsProtos.CardAptitudeTypeEnum;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.table.RootTc;

import java.util.List;
import java.util.Map;

public class Table_CardUpgradeStar_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * ListCell<Integer> 碎片数量
     */
    private ListCell<Integer> pieceNumber;
    /**
     * ListCell<Integer> 金币数量
     */
    private ListCell<Integer> goldNumber;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"基础ID"}
        pieceNumber = CellParser.parseListCell("PieceNumber", map, Integer.class); //string
        goldNumber = CellParser.parseListCell("GoldNumber", map, Integer.class); //string
    }

    public List<Integer> getPieceNumber() {
        return pieceNumber.getAll();
    }

    public List<Integer> getGoldNumber() {
        return goldNumber.getAll();
    }

    /**
     * 获取武将升星消耗
     *
     * @param curStarLv
     * @return
     */
    public static IdMaptoCount getUpgradeConsumes(int heroTpId, int curStarLv) {
        IdMaptoCount ret = new IdMaptoCount();
        int idx = curStarLv;// 0->1,1->2,2->3,3->4,4->5,5->6,6->7
        Table_New_Card_Row cardRow = RootTc.get(Table_New_Card_Row.class, heroTpId);
        CardAptitudeTypeEnum aptitudeType = Table_CardUpgradeQuality_Row.getHeroAptitudeType(cardRow.getCardAptitude());
        Table_CardUpgradeStar_Row upgradeStarRow = RootTc.get(Table_CardUpgradeStar_Row.class, aptitudeType.getNumber());
        ret.add(new IdAndCount(cardRow.getCardPiece(), upgradeStarRow.getPieceNumber().get(idx)));
        ret.add(new IdAndCount(ResourceTypeEnum.RES_MONEY_VALUE, upgradeStarRow.getGoldNumber().get(idx)));
        return ret;
    }


}
