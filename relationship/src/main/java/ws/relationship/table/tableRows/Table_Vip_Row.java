package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.common.table.table.utils.CellParser;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.table.RootTc;

import java.util.Map;

public class Table_Vip_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int VIP等级
     */
    private Integer vIPLv;
    /**
     * int VIP经验
     */
    private Integer vIPExp;

    /**
     * int VIP经验自增
     */
    private Integer vIPExpAdd;

    /**
     * int VIP钻石
     */
    private Integer vIPDiamond;
    /**
     * string VIP背景
     */
    private String vIPBG;
    /**
     * int VIP描述
     */
    private String vIPExplain;
    /**
     * int 购买体力
     */
    private Integer spiritShop;
    /**
     * int 精英关卡重置次数
     */
    private Integer stageReset;
    /**
     * int 技能点购买次数
     */
    private Integer buySkillPointTimes;
    /**
     * int 技能点回复时间
     */
    private Integer skillReply;
    /**
     * int 竞技场跳过战斗
     */
    private Integer jJCSkip;
    /**
     * int 十连扫
     */
    private Integer tenSweep;
    /**
     * int 五十连扫
     */
    private Integer fiftySweep;
    /**
     * int 竞技场积分战败同样+2
     */
    private Integer jJCFail;
    /**
     * int 终极试练跳过战斗
     */
    private Integer finalSkip;
    /**
     * int 终极试炼特权扫层(达到该行的vip等级，终极试炼可以跳到最高层数读副服务器配置文件AllServerConfig)
     */
    private Integer finalSweepFloor;
    /**
     * int 终极试练积分
     */
    private Integer finalIntegral;
    /**
     * int 竞技场冷却
     */
    private Integer jJCCool;
    /**
     * int 金币副本冷却时间
     */
    private Integer goldStageCool;
    /**
     * int 神秘商店刷新次数
     */
    private Integer secretShop;
    /**
     * int VIP礼包原价
     */
    private Integer vIPPackageOriginal;
    /**
     * int VIP礼包价格
     */
    private Integer vIPPackagePrice;
    /**
     * int VIP礼包内容
     */
    private TupleListCell<Integer> vIPPackage;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"VIPId", columnDesc:"VIPID"}
        vIPLv = CellParser.parseSimpleCell("VIPLv", map, Integer.class); //int
        vIPExpAdd = CellParser.parseSimpleCell("VIPExpAdd", map, Integer.class); //int
        vIPExp = CellParser.parseSimpleCell("VIPExp", map, Integer.class); //int
        vIPDiamond = CellParser.parseSimpleCell("VIPDiamond", map, Integer.class); //int
        vIPBG = CellParser.parseSimpleCell("VIPBG", map, String.class); //string
        vIPExplain = CellParser.parseSimpleCell("VIPExplain", map, String.class); //int
        spiritShop = CellParser.parseSimpleCell("SpiritShop", map, Integer.class); //int
        stageReset = CellParser.parseSimpleCell("StageReset", map, Integer.class); //int

        buySkillPointTimes = CellParser.parseSimpleCell("BuySkillPointTimes", map, Integer.class); //int
        skillReply = CellParser.parseSimpleCell("SkillReply", map, Integer.class); //int

        jJCSkip = CellParser.parseSimpleCell("JJCSkip", map, Integer.class); //int
        tenSweep = CellParser.parseSimpleCell("TenSweep", map, Integer.class); //int
        fiftySweep = CellParser.parseSimpleCell("FiftySweep", map, Integer.class); //int
        jJCFail = CellParser.parseSimpleCell("JJCFail", map, Integer.class); //int
        finalSkip = CellParser.parseSimpleCell("FinalSkip", map, Integer.class); //int
        finalSweepFloor = CellParser.parseSimpleCell("FinalSweepFloor", map, Integer.class);//int
        finalIntegral = CellParser.parseSimpleCell("FinalIntegral", map, Integer.class); //int
        jJCCool = CellParser.parseSimpleCell("JJCCool", map, Integer.class); //int
        goldStageCool = CellParser.parseSimpleCell("GoldStageCool", map, Integer.class); //int
        secretShop = CellParser.parseSimpleCell("SecretShop", map, Integer.class); //int
        vIPPackageOriginal = CellParser.parseSimpleCell("VIPPackageOriginal", map, Integer.class); //int
        vIPPackagePrice = CellParser.parseSimpleCell("VIPPackagePrice", map, Integer.class); //int
        vIPPackage = CellParser.parseTupleListCell("VIPPackage", map, Integer.class); //string

    }

    public Integer getvIPExpAdd() {
        return vIPExpAdd;
    }

    public Integer getvIPLv() {
        return vIPLv;
    }

    public Integer getvIPExp() {
        return vIPExp;
    }

    public Integer getvIPDiamond() {
        return vIPDiamond;
    }

    public String getvIPBG() {
        return vIPBG;
    }

    public String getvIPExplain() {
        return vIPExplain;
    }

    public Integer getSpiritShop() {
        return spiritShop;
    }

    public Integer getStageReset() {
        return stageReset;
    }

    public Integer getSkillReply() {
        return skillReply;
    }

    public Integer getjJCSkip() {
        return jJCSkip;
    }

    public Integer getTenSweep() {
        return tenSweep;
    }

    public Integer getFiftySweep() {
        return fiftySweep;
    }

    public Integer getjJCFail() {
        return jJCFail;
    }

    public Integer getFinalSkip() {
        return finalSkip;
    }

    public Integer getFinalIntegral() {
        return finalIntegral;
    }

    public Integer getjJCCool() {
        return jJCCool;
    }

    public Integer getGoldStageCool() {
        return goldStageCool;
    }

    public Integer getSecretShop() {
        return secretShop;
    }

    public Integer getvIPPackageOriginal() {
        return vIPPackageOriginal;
    }

    public Integer getvIPPackagePrice() {
        return vIPPackagePrice;
    }

    public IdMaptoCount getvIPPackage() {
        return IdMaptoCount.parse(vIPPackage);
    }

    public Integer getBuySkillPointTimes() {
        return buySkillPointTimes;
    }

    public Integer getFinalSweepFloor() {
        return finalSweepFloor;
    }

    public static Table_Vip_Row getVipRow(int curVipLevel) {
        return RootTc.get(Table_Vip_Row.class).get(curVipLevel + 1);
    }
}
