package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.common.table.table.utils.CellParser;
import ws.protos.EnumsProtos.GuildRedBagTypeEnum;
import ws.protos.EnumsProtos.GuildSendRedBagTypeEnum;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.RootTc;

import java.util.Map;

public class Table_GuildRedPacket_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 发送金币特权等级限制
     */
    private Integer goldRedPacketVipLv;
    /**
     * int 发送钻石特权等级限制
     */
    private Integer vipMoneyRedPacketVipLv;
    /**
     * int 发金币红包消耗
     */
    private Integer goldRedPacketCosume;
    /**
     * int 发钻石红包消耗
     */
    private Integer vipMoneyRedPacketCosume;
    /**
     * int 金币红包总个数
     */
    private Integer goldRedPacketCount;
    /**
     * int 钻石红包总个数
     */
    private Integer vipMoneyRedPacketCount;
    /**
     * string 发金币红包获得
     */
    private TupleListCell<Integer> goldRedPacketReward;
    /**
     * string 发钻石红包获得
     */
    private TupleListCell<Integer> vipMoneyRedPacketReward;
    /**
     * int 金币红包总额
     */
    private Integer goldMaxValue;
    /**
     * int 钻石红包总额
     */
    private Integer vipMoneyMaxValue;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        goldRedPacketReward = CellParser.parseTupleListCell("GoldRedPacketReward", map, Integer.class); //string
        vipMoneyRedPacketReward = CellParser.parseTupleListCell("VipMoneyRedPacketReward", map, Integer.class); //string
        // id column = {columnName:"ID", columnDesc:"ID"}
        goldRedPacketVipLv = CellParser.parseSimpleCell("GoldRedPacketVipLv", map, Integer.class); //int
        vipMoneyRedPacketVipLv = CellParser.parseSimpleCell("VipMoneyRedPacketVipLv", map, Integer.class); //int
        goldRedPacketCosume = CellParser.parseSimpleCell("GoldRedPacketCosume", map, Integer.class); //int
        vipMoneyRedPacketCosume = CellParser.parseSimpleCell("VipMoneyRedPacketCosume", map, Integer.class); //int
        goldRedPacketCount = CellParser.parseSimpleCell("GoldRedPacketCount", map, Integer.class); //int
        vipMoneyRedPacketCount = CellParser.parseSimpleCell("VipMoneyRedPacketCount", map, Integer.class); //int
        goldMaxValue = CellParser.parseSimpleCell("GoldMaxValue", map, Integer.class); //int
        vipMoneyMaxValue = CellParser.parseSimpleCell("VipMoneyMaxValue", map, Integer.class); //int

    }

    public Integer getGoldRedPacketVipLv() {
        return goldRedPacketVipLv;
    }

    public Integer getVipMoneyRedPacketVipLv() {
        return vipMoneyRedPacketVipLv;
    }

    public Integer getGoldRedPacketCosume() {
        return goldRedPacketCosume;
    }

    public Integer getVipMoneyRedPacketCosume() {
        return vipMoneyRedPacketCosume;
    }

    public Integer getGoldRedPacketCount() {
        return goldRedPacketCount;
    }

    public Integer getVipMoneyRedPacketCount() {
        return vipMoneyRedPacketCount;
    }

    public Integer getGoldMaxValue() {
        return goldMaxValue;
    }

    public Integer getVipMoneyMaxValue() {
        return vipMoneyMaxValue;
    }

    public TupleListCell<Integer> getGoldRedPacketReward() {
        return goldRedPacketReward;
    }

    public TupleListCell<Integer> getVipMoneyRedPacketReward() {
        return vipMoneyRedPacketReward;
    }


    /**
     * 个人发红包奖励
     *
     * @param sendRedBagType
     * @param redBagType
     * @return
     */
    public static IdMaptoCount getRewardByRedBagType(GuildSendRedBagTypeEnum sendRedBagType, GuildRedBagTypeEnum redBagType) {
        Table_GuildRedPacket_Row guildRedPacketRow = RootTc.get(Table_GuildRedPacket_Row.class).get(sendRedBagType.getNumber());
        switch (redBagType) {
            case PRT_MONEY:
                return IdMaptoCount.parse(guildRedPacketRow.getGoldRedPacketReward());
            case PRT_V_MONEY:
                return IdMaptoCount.parse(guildRedPacketRow.getVipMoneyRedPacketReward());
        }
        String msg = String.format("配置表中没有对应的红包类型:%s", redBagType);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    /**
     * 个人发红包消耗
     *
     * @param sendRedBagType
     * @param redBagType
     * @return
     */
    public static IdAndCount getConsumeByRedBagType(GuildSendRedBagTypeEnum sendRedBagType, GuildRedBagTypeEnum redBagType) {
        Table_GuildRedPacket_Row guildRedPacketRow = RootTc.get(Table_GuildRedPacket_Row.class).get(sendRedBagType.getNumber());
        switch (redBagType) {
            case PRT_MONEY:
                int moneyConsumeCount = guildRedPacketRow.getGoldRedPacketCosume();
                return new IdAndCount(ResourceTypeEnum.RES_MONEY_VALUE, moneyConsumeCount);
            case PRT_V_MONEY:
                int vipMoneyConsumeCount = guildRedPacketRow.getVipMoneyRedPacketCosume();
                return new IdAndCount(ResourceTypeEnum.RES_VIPMONEY_VALUE, vipMoneyConsumeCount);
        }
        String msg = String.format("配置表中没有对应的红包类型:%s", redBagType);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    /**
     * 个人发红包的vip等级限制
     *
     * @param sendRedBagType
     * @param redBagType
     * @return
     */
    public static int getLimitLvByRedBagType(GuildSendRedBagTypeEnum sendRedBagType, GuildRedBagTypeEnum redBagType) {
        Table_GuildRedPacket_Row guildRedPacketRow = RootTc.get(Table_GuildRedPacket_Row.class).get(sendRedBagType.getNumber());
        switch (redBagType) {
            case PRT_MONEY:
                return guildRedPacketRow.getGoldRedPacketVipLv();
            case PRT_V_MONEY:
                return guildRedPacketRow.getVipMoneyRedPacketVipLv();
        }
        String msg = String.format("配置表中没有对应的红包类型:%s", redBagType);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    /**
     * 个人发红包的个数
     *
     * @param sendRedBagType
     * @param redBagType
     * @return
     */
    public static int getCountByRedBagType(GuildSendRedBagTypeEnum sendRedBagType, GuildRedBagTypeEnum redBagType) {
        Table_GuildRedPacket_Row guildRedPacketRow = RootTc.get(Table_GuildRedPacket_Row.class).get(sendRedBagType.getNumber());
        switch (redBagType) {
            case PRT_MONEY:
                return guildRedPacketRow.getGoldRedPacketCount();
            case PRT_V_MONEY:
                return guildRedPacketRow.getVipMoneyRedPacketCount();
        }
        String msg = String.format("配置表中没有对应的红包类型:%s", redBagType);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    /**
     * 个人发红包总额
     *
     * @param sendRedBagType
     * @param redBagType
     * @return
     */
    public static int getSumByRedBagType(GuildSendRedBagTypeEnum sendRedBagType, GuildRedBagTypeEnum redBagType) {
        Table_GuildRedPacket_Row guildRedPacketRow = RootTc.get(Table_GuildRedPacket_Row.class).get(sendRedBagType.getNumber());
        switch (redBagType) {
            case PRT_MONEY:
                return guildRedPacketRow.getGoldMaxValue();
            case PRT_V_MONEY:
                return guildRedPacketRow.getVipMoneyMaxValue();
        }
        String msg = String.format("配置表中没有对应的红包类型:%s", redBagType);
        throw new BusinessLogicMismatchConditionException(msg);
    }

}
