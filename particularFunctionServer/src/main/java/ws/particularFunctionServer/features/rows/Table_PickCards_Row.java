package ws.particularFunctionServer.features.rows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.protos.EnumsProtos.PickCardTypeEnum;
import ws.relationship.base.IdAndCount;

import java.util.Map;

public class Table_PickCards_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 抽卡库类型
     */
    private Integer drawType;
    /**
     * int 可使用道具ID
     */
    private Integer itemId;
    /**
     * int 消耗货币ID
     */
    private Integer coinId;
    /**
     * int 1次消耗货币数量
     */
    private Integer oneCoinNum;
    /**
     * int 1次每日次数限制
     */
    private Integer oneTimesLimit;
    /**
     * int 10次消耗货币数量
     */
    private Integer tenCoinNum;
    /**
     * int 10次每日次数限制
     */
    private Integer tenTimesLimit;
    /**
     * int 100次消耗货币数量
     */
    private Integer hundredCoinNum;
    /**
     * int 100次每日次数限制
     */
    private Integer hundredTimesLimit;
    /**
     * int 抽卡库ID1
     */
    private Integer drawLib1;
    /**
     * int 抽卡库ID2
     */
    private Integer drawLib2;
    /**
     * int 抽卡库ID3
     */
    private Integer drawLib3;
    /**
     * int 抽卡库ID4
     */
    private Integer drawLib4;
    /**
     * int 抽卡库ID5
     */
    private Integer drawLib5;
    /**
     * int 抽卡库ID6
     */
    private Integer drawLib6;
    /**
     * int 抽卡库ID7
     */
    private Integer drawLib7;
    /**
     * int 抽卡库ID8
     */
    private Integer drawLib8;
    /**
     * int 抽卡库ID9
     */
    private Integer drawLib9;
    /**
     * int 抽卡库ID10
     */
    private Integer drawLib10;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"抽卡ID"}
        drawType = CellParser.parseSimpleCell("DrawType", map, Integer.class); //int
        itemId = CellParser.parseSimpleCell("ItemId", map, Integer.class); //int
        coinId = CellParser.parseSimpleCell("CoinId", map, Integer.class); //int
        oneCoinNum = CellParser.parseSimpleCell("OneCoinNum", map, Integer.class); //int
        oneTimesLimit = CellParser.parseSimpleCell("OneTimesLimit", map, Integer.class); //int
        tenCoinNum = CellParser.parseSimpleCell("TenCoinNum", map, Integer.class); //int
        tenTimesLimit = CellParser.parseSimpleCell("TenTimesLimit", map, Integer.class); //int
        hundredCoinNum = CellParser.parseSimpleCell("HundredCoinNum", map, Integer.class); //int
        hundredTimesLimit = CellParser.parseSimpleCell("HundredTimesLimit", map, Integer.class); //int
        drawLib1 = CellParser.parseSimpleCell("DrawLib1", map, Integer.class); //int
        drawLib2 = CellParser.parseSimpleCell("DrawLib2", map, Integer.class); //int
        drawLib3 = CellParser.parseSimpleCell("DrawLib3", map, Integer.class); //int
        drawLib4 = CellParser.parseSimpleCell("DrawLib4", map, Integer.class); //int
        drawLib5 = CellParser.parseSimpleCell("DrawLib5", map, Integer.class); //int
        drawLib6 = CellParser.parseSimpleCell("DrawLib6", map, Integer.class); //int
        drawLib7 = CellParser.parseSimpleCell("DrawLib7", map, Integer.class); //int
        drawLib8 = CellParser.parseSimpleCell("DrawLib8", map, Integer.class); //int
        drawLib9 = CellParser.parseSimpleCell("DrawLib9", map, Integer.class); //int
        drawLib10 = CellParser.parseSimpleCell("DrawLib10", map, Integer.class); //int

    }

    public PickCardTypeEnum getDrawType() {
        return PickCardTypeEnum.valueOf(drawType);
    }

    public Integer getItemId() {
        return itemId;
    }

    public IdAndCount getOnePickConsume() {
        return new IdAndCount(coinId, oneCoinNum);
    }

    public IdAndCount getTenPickConsume() {
        return new IdAndCount(coinId, tenCoinNum);
    }

    public IdAndCount getHundredPickConsume() {
        return new IdAndCount(coinId, hundredCoinNum);
    }


    public Integer getOneTimesLimit() {
        return oneTimesLimit;
    }

    public Integer getTenTimesLimit() {
        return tenTimesLimit;
    }

    public Integer getHundredTimesLimit() {
        return hundredTimesLimit;
    }

    public Integer getDrawLib1() {
        return drawLib1;
    }

    public Integer getDrawLib2() {
        return drawLib2;
    }

    public Integer getDrawLib3() {
        return drawLib3;
    }

    public Integer getDrawLib4() {
        return drawLib4;
    }

    public Integer getDrawLib5() {
        return drawLib5;
    }

    public Integer getDrawLib6() {
        return drawLib6;
    }

    public Integer getDrawLib7() {
        return drawLib7;
    }

    public Integer getDrawLib8() {
        return drawLib8;
    }

    public Integer getDrawLib9() {
        return drawLib9;
    }

    public Integer getDrawLib10() {
        return drawLib10;
    }


}
