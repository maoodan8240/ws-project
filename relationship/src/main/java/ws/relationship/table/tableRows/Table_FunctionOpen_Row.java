package ws.relationship.table.tableRows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.common.utils.general.TrueParser;
import ws.relationship.table.RootTc;

import java.util.Map;
import java.util.Objects;

public class Table_FunctionOpen_Row extends AbstractRow {
    private static final Logger LOGGER = LoggerFactory.getLogger(Table_FunctionOpen_Row.class);
    private static final long serialVersionUID = 1L;
    /**
     * string 功能类型
     */
    private String functionType;
    /**
     * int 页签出现等级
     */
    private Integer sheetAppearLv;
    /**
     * int 等级限制
     */
    private Integer limitLv;
    /**
     * int 关卡限制
     */
    private Integer stageLimit;
    /**
     * int vip限制
     */
    private Integer vIPLimit;
    /**
     * int 是否全部限制
     */
    private Integer isAllLimit;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"id"}
        functionType = CellParser.parseSimpleCell("FunctionType", map, String.class); //string
        sheetAppearLv = CellParser.parseSimpleCell("SheetAppearLv", map, Integer.class); //int
        limitLv = CellParser.parseSimpleCell("LimitLv", map, Integer.class); //int
        stageLimit = CellParser.parseSimpleCell("StageLimit", map, Integer.class); //int
        vIPLimit = CellParser.parseSimpleCell("VIPLimit", map, Integer.class); //int
        isAllLimit = CellParser.parseSimpleCell("IsAllLimit", map, Integer.class); //int
    }

    public String getFunctionType() {
        return functionType;
    }

    public Integer getSheetAppearLv() {
        return sheetAppearLv;
    }

    public Integer getLimitLv() {
        return limitLv;
    }

    public Integer getStageLimit() {
        return stageLimit;
    }

    public Integer getvIPLimit() {
        return vIPLimit;
    }

    public Integer getIsAllLimit() {
        return isAllLimit;
    }

    public enum FunctionType {
        FORMATIONS(1),              // 布阵
        SHOP(2),                    // 商店
        ARENA(3),                   // 竞技场
        RANK(4),                    // 排行榜
        CHALLENGE_MONEY(5),         // 活动关卡-金币挑战
        CHAT(6),                    // 聊天
        GUILD(7),                   // 社团
        CHEATS_BADGE_STRENGTHEN(8), // 秘技徽章强化
        ULTIMATE_TEST(9),           // 终极试炼
        AWAKE_FRAGMENT_BOX(10),     // 装备觉醒宝箱
        AWAKE_FRAGMENT(11),         // 商店-觉醒宝箱
        TALENT(12),                 // 战队天赋
        CHALLENGE_EXP(13),          // 活动关卡-经验
        WAR_SOUL(14),               // 战魂
        MOPUP_50(15),               // 扫荡50
        HERO_SKILL(19),             // 格斗家技能
        HERO_LV(20),                // 格斗家升级
        EQUIPMENT_LV(21),           // 装备升级
        EQUIPMENT_COLOR(22),        // 装备升品
        ELITE_STAGE(23),            // 精英关卡
        DAY_MISSION(24),            // 日常任务
        MOPUP_ELITE_STAGE_3(25),    // 扫荡精英关卡3次
        MOPUP_STAGE_10(26),         // 扫荡普通关卡10次
        MOPUP_STAGE(27),            // 主线单扫
        MOPUP_ELITE(28),            // 精英单扫
        NULL(0);

        private int value;

        FunctionType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public FunctionType valueOf(int enumValue) {
            for (FunctionType type : FunctionType.values()) {
                if (type.getValue() == enumValue) {
                    return type;
                }
            }
            String msg = String.format("FunctionType Error enum, type :%s ", enumValue);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * 检查功能是否开启
     *
     * @param functionType
     * @param playerLv
     * @param playerVipLv
     * @return
     */
    public static boolean isFunctionOpen(FunctionType functionType, int playerLv, int playerVipLv) {
        LOGGER.debug("检查功能开启: functionType={} functionTypeValue={} playerLv={} playerVipLv={} .", functionType, functionType.getValue(), playerLv, playerVipLv);
        Objects.requireNonNull(functionType);
        int id = functionType.getValue();
        Table_FunctionOpen_Row functionOpenRow = RootTc.get(Table_FunctionOpen_Row.class).get(id);
        int lv = functionOpenRow.getLimitLv();
        int vipLv = functionOpenRow.getvIPLimit();
        boolean flag = TrueParser.isTrue(functionOpenRow.getIsAllLimit());
        if (flag) {
            return playerLv >= lv && playerVipLv >= vipLv;
        }
        return playerLv >= lv || playerVipLv >= vipLv;
    }
}
