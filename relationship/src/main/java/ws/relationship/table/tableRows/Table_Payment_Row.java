package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.protos.EnumsProtos.PlatformTypeEnum;

import java.util.Map;

public class Table_Payment_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 内部编号
     */
    private Integer innerId;
    /**
     * int 渠道
     */
    private Integer platformType;
    /**
     * string 支付商品名称
     */
    private String goodName;
    /**
     * int 买的元宝数
     */
    private Integer vipMoneyCount;
    /**
     * int 支付人民币金额
     */
    private Integer rmbPrice;
    /**
     * int 第一次赠送的元宝
     */
    private Integer firstVipMoney;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"下标"}
        innerId = CellParser.parseSimpleCell("InnerId", map, Integer.class); //int
        platformType = CellParser.parseSimpleCell("PlatformType", map, Integer.class); //int
        goodName = CellParser.parseSimpleCell("GoodName", map, String.class); //string
        vipMoneyCount = CellParser.parseSimpleCell("VipMoneyCount", map, Integer.class); //int
        rmbPrice = CellParser.parseSimpleCell("RMBPrice", map, Integer.class); //int
        firstVipMoney = CellParser.parseSimpleCell("FirstVipMoney", map, Integer.class); //int

    }

    public Integer getInnerId() {
        return innerId;
    }

    public PlatformTypeEnum getPlatformType() {
        return PlatformTypeEnum.valueOf(platformType);
    }

    public String getGoodName() {
        return goodName;
    }

    public Integer getVipMoneyCount() {
        return vipMoneyCount;
    }

    public Integer getRmbPrice() {
        return rmbPrice;
    }

    public Integer getFirstVipMoney() {
        return firstVipMoney;
    }
}
