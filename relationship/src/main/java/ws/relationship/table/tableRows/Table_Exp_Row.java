package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.protos.EnumsProtos.WarSoulPositionEnum;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.RootTc;

import java.util.List;
import java.util.Map;

public class Table_Exp_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 玩家体力
     */
    private Integer roleSpirit;
    /**
     * int 战队经验
     */
    private Integer corpsExp;
    /**
     * int 11资质卡片经验
     */
    private Integer card11Exp;
    /**
     * int 12资质卡片经验
     */
    private Integer card12Exp;
    /**
     * int 13资质卡片经验
     */
    private Integer card13Exp;
    /**
     * int 14资质卡片经验
     */
    private Integer card14Exp;
    /**
     * int 社团训练所增加经验/分钟
     */
    private Integer guildTrainRoleExp;
    /**
     * int 好感度阶段1经验
     */
    private Integer class1Level;
    /**
     * int 好感度阶段2经验
     */
    private Integer class2Level;
    /**
     * int 好感度阶段3经验
     */
    private Integer class3Level;
    /**
     * int 好感度阶段4经验
     */
    private Integer class4Level;
    /**
     * int 好感度阶段5经验
     */
    private Integer class5Level;
    /**
     * int 好感度阶段6经验
     */
    private Integer class6Level;
    /**
     * int 好感度阶段7经验
     */
    private Integer class7Level;
    /**
     * int 特殊装备经验
     */
    private Integer specialEquipmentExp;
    /**
     * int 第一战魂经验
     */
    private Integer warSoul1Exp;
    /**
     * int 第二战魂经验
     */
    private Integer warSoul2Exp;
    /**
     * int 第三战魂经验
     */
    private Integer warSoul3Exp;
    /**
     * int 第四战魂经验
     */
    private Integer warSoul4Exp;
    /**
     * 最高阶最高等级最大经验所需要经验总和
     */
    private static long sumExp;


    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"ID"}
        roleSpirit = CellParser.parseSimpleCell("RoleSpirit", map, Integer.class); //int
        corpsExp = CellParser.parseSimpleCell("CorpsExp", map, Integer.class); //int
        card11Exp = CellParser.parseSimpleCell("Card11Exp", map, Integer.class); //int
        card12Exp = CellParser.parseSimpleCell("Card12Exp", map, Integer.class); //int
        card13Exp = CellParser.parseSimpleCell("Card13Exp", map, Integer.class); //int
        card14Exp = CellParser.parseSimpleCell("Card14Exp", map, Integer.class); //int
        guildTrainRoleExp = CellParser.parseSimpleCell("GuildTrainRoleExp", map, Integer.class); //int
        class1Level = CellParser.parseSimpleCell("Class1Level", map, Integer.class); //int
        class2Level = CellParser.parseSimpleCell("Class2Level", map, Integer.class); //int
        class3Level = CellParser.parseSimpleCell("Class3Level", map, Integer.class); //int
        class4Level = CellParser.parseSimpleCell("Class4Level", map, Integer.class); //int
        class5Level = CellParser.parseSimpleCell("Class5Level", map, Integer.class); //int
        class6Level = CellParser.parseSimpleCell("Class6Level", map, Integer.class); //int
        class7Level = CellParser.parseSimpleCell("Class7Level", map, Integer.class); //int
        specialEquipmentExp = CellParser.parseSimpleCell("SpecialEquipmentExp", map, Integer.class); //int
        warSoul1Exp = CellParser.parseSimpleCell("WarSoul1Exp", map, Integer.class); //int
        warSoul2Exp = CellParser.parseSimpleCell("WarSoul2Exp", map, Integer.class); //int
        warSoul3Exp = CellParser.parseSimpleCell("WarSoul3Exp", map, Integer.class); //int
        warSoul4Exp = CellParser.parseSimpleCell("WarSoul4Exp", map, Integer.class); //int
    }


    public static void calcuMaxHofFavorExp() {
        sumExp = 0;
        List<Table_Favorable_Row> rowList = RootTc.getAll(Table_Favorable_Row.class);
        int idx = 1;
        for (Table_Favorable_Row row : rowList) {
            int maxLv = row.getClassLevel();
            for (int i = 1; i <= maxLv; i++) {
                Table_Exp_Row expRow = RootTc.get(Table_Exp_Row.class, i);
                switch (idx) {
                    case 1:
                        sumExp += expRow.class1Level;
                        break;
                    case 2:
                        sumExp += expRow.class2Level;
                        break;
                    case 3:
                        sumExp += expRow.class3Level;
                        break;
                    case 4:
                        sumExp += expRow.class4Level;
                        break;
                    case 5:
                        sumExp += expRow.class5Level;
                        break;
                    case 6:
                        sumExp += expRow.class6Level;
                        break;
                    case 7:
                        sumExp += expRow.class7Level;
                        break;
                }
            }
            idx++;
        }
    }

    public static long getSumExp() {
        return sumExp;
    }


    public static int getClassLevelExp(int favorLevel, int classLevel) {
        Table_Exp_Row expRow = RootTc.get(Table_Exp_Row.class).get(favorLevel);
        if (expRow == null) {
            String msg = String.format("favorLevel=%s,classLevel=%s， 未找到当前等级的经验行！", favorLevel, classLevel);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        switch (classLevel) {
            case MagicNumbers.FAVOR_CLASS_ONE:
                return expRow.class1Level;
            case MagicNumbers.FAVOR_CLASS_TWO:
                return expRow.class2Level;
            case MagicNumbers.FAVOR_CLASS_THREE:
                return expRow.class3Level;
            case MagicNumbers.FAVOR_CLASS_FOUR:
                return expRow.class4Level;
            case MagicNumbers.FAVOR_CLASS_FIVE:
                return expRow.class5Level;
            case MagicNumbers.FAVOR_CLASS_SIX:
                return expRow.class6Level;
            case MagicNumbers.FAVOR_CLASS_SEVEN:
                return expRow.class7Level;
        }
        String msg = String.format("favorLevel=%s,classLevel=%s， 未找到当前等级的经验行！", favorLevel, classLevel);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    /**
     * 获取武将升级需要的经验值
     *
     * @param heroAptitude
     * @param curLv
     * @return
     */
    public static int getHeroUpgradeNeedExp(int heroAptitude, int curLv) {
        Table_Exp_Row expRow = RootTc.get(Table_Exp_Row.class, curLv);
        if (expRow == null) {
            String msg = String.format("aptitude=%s，curLv=%s 未找到当前等级的经验行！", heroAptitude, curLv);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        switch (heroAptitude) {
            case MagicNumbers.APTITUDE_11:
                return expRow.card11Exp;
            case MagicNumbers.APTITUDE_12:
                return expRow.card12Exp;
            case MagicNumbers.APTITUDE_13:
                return expRow.card13Exp;
            case MagicNumbers.APTITUDE_14:
                return expRow.card14Exp;
        }
        String msg = String.format("aptitude=%s，curLv=%s 未匹配上卡牌资质类型！", heroAptitude, curLv);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    /**
     * 获取战魂升级需要的经验值
     *
     * @param soulPosition
     * @param curLv
     * @return
     */
    public static int getWarSoulUpgradeNeedExp(WarSoulPositionEnum soulPosition, int curLv) {
        Table_Exp_Row expRow = RootTc.get(Table_Exp_Row.class, curLv);
        switch (soulPosition) {
            case POS_Soul_A:
                return expRow.warSoul1Exp;
            case POS_Soul_B:
                return expRow.warSoul2Exp;
            case POS_Soul_C:
                return expRow.warSoul3Exp;
            case POS_Soul_D:
                return expRow.warSoul4Exp;
        }
        String msg = String.format("soulPosition=%s，未匹配上战魂位置类型！", soulPosition);
        throw new BusinessLogicMismatchConditionException(msg);
    }


    /**
     * 获取徽章和秘籍的升级经验
     *
     * @param curLv
     * @return
     */
    public static int getBadgeOrCheatsUpNeedExp(int curLv) {
        Table_Exp_Row expRow = RootTc.get(Table_Exp_Row.class, curLv);
        return expRow.specialEquipmentExp;
    }

    /**
     * 获取战队升级经验
     *
     * @param curLv
     * @return
     */
    public static int getPlayerUpNeedExp(int curLv) {
        Table_Exp_Row expRow = RootTc.get(Table_Exp_Row.class, curLv);
        return expRow.corpsExp;
    }


    /**
     * 当前玩家等级的体力上限
     *
     * @param curPlayerLv
     * @return
     */
    public static int getPlayerEnergyUpperLimit(int curPlayerLv) {
        Table_Exp_Row expRow = RootTc.get(Table_Exp_Row.class, curPlayerLv);
        return expRow.roleSpirit;
    }

    /**
     * 当前玩家等级训练所每分钟增加的经验
     *
     * @param curPlayerLv
     * @return
     */
    public static int getGuildTrainRoleExp(int curPlayerLv) {
        Table_Exp_Row expRow = RootTc.get(Table_Exp_Row.class, curPlayerLv);
        return expRow.guildTrainRoleExp;
    }
}
