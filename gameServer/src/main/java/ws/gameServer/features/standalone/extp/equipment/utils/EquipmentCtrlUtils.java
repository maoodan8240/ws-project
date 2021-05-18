package ws.gameServer.features.standalone.extp.equipment.utils;

import ws.common.table.table.interfaces.cell.ListCell;
import ws.gameServer.features.standalone.extp.itemBag.utils.ItemBagUtils;
import ws.gameServer.features.standalone.extp.utils.UpgradeLevel;
import ws.protos.EnumsProtos.ColorDetailTypeEnum;
import ws.protos.EnumsProtos.EquipmentPositionEnum;
import ws.protos.EnumsProtos.ItemSmallTypeEnum;
import ws.protos.EnumsProtos.SpecialItemTemplateIdEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_BadgeCheatsUpQualityLvConsumes_Row;
import ws.relationship.table.tableRows.Table_BadgeCheats_Row;
import ws.relationship.table.tableRows.Table_EquipmentUpLvConsumes_Row;
import ws.relationship.table.tableRows.Table_EquipmentUpQualityLvConsumes_Row;
import ws.relationship.table.tableRows.Table_Equipment_Row;
import ws.relationship.table.tableRows.Table_Exp_Row;
import ws.relationship.table.tableRows.Table_Item_Row;
import ws.relationship.topLevelPojos.common.LevelUpObj;
import ws.relationship.topLevelPojos.heros.Equipment;
import ws.relationship.topLevelPojos.itemBag.ItemBag;

/**
 * Created by zhangweiwei on 16-9-29.
 */
public class EquipmentCtrlUtils {


    /**
     * 根据索引获取 ColorDetailTypeEnum
     *
     * @param index
     * @return
     */
    public static ColorDetailTypeEnum colorOfIndex(int index) {
        int num = 0;
        for (ColorDetailTypeEnum color : ColorDetailTypeEnum.values()) {
            if (num == index) {
                return color;
            }
            num++;
        }
        return null;
    }

    /**
     * 获取Color的索引值
     *
     * @param color
     * @return
     */
    public static int indexOfColor(ColorDetailTypeEnum color) {
        int num = 0;
        for (ColorDetailTypeEnum c : ColorDetailTypeEnum.values()) {
            if (c == color) {
                return num;
            }
            num++;
        }
        return -1;
    }


    /**
     * 获取升级一级星级的消耗
     *
     * @param equipPos
     * @param equipTpId
     * @param curStarLv
     * @return
     */
    public static IdMaptoCount getUpStarLvConsumes(EquipmentPositionEnum equipPos, int equipTpId, int curStarLv) {
        switch (equipPos) {
            case POS_A:
            case POS_B:
            case POS_C:
            case POS_D:
                Table_Equipment_Row equipmentRow = RootTc.get(Table_Equipment_Row.class, equipTpId);
                return equipmentRow.getUpStarLvConsumes(curStarLv);
            case POS_E:
            case POS_F:
                Table_BadgeCheats_Row badgeCheatsRow = RootTc.get(Table_BadgeCheats_Row.class, equipTpId);
                return badgeCheatsRow.getUpStarLvConsumes(curStarLv);
        }
        String msg = String.format("curStarLv=%s, equipTpId=%s, 不支持的equipPos=%s！", equipPos, equipTpId, curStarLv);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    /**
     * 获取升级一级品质的消耗
     *
     * @param equipPos
     * @param curColor
     * @return
     */
    public static IdMaptoCount getUpOneQualityLvConsumes(EquipmentPositionEnum equipPos, ColorDetailTypeEnum curColor) {
        switch (equipPos) {
            case POS_A:
            case POS_B:
            case POS_C:
            case POS_D:
                return Table_EquipmentUpQualityLvConsumes_Row.getUpOneQualityLvConsumes(curColor, equipPos);
            case POS_E:
            case POS_F:
                return Table_BadgeCheatsUpQualityLvConsumes_Row.getUpOneQualityLvConsumes(curColor, equipPos);
        }
        String msg = String.format("curColor=%s，不支持的equipPos=%s！", curColor, equipPos);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    /**
     * 装备当前品质的最大装备等级
     *
     * @param equipPos
     * @param curColor
     * @return
     */
    public static int curColorMaxLv(EquipmentPositionEnum equipPos, ColorDetailTypeEnum curColor) {
        switch (equipPos) {
            case POS_A:
            case POS_B:
            case POS_C:
            case POS_D:
                return curColorMaxLv_ABCD(curColor);
            case POS_E:
            case POS_F:
                return curColorMaxLv_EF(curColor);
        }
        String msg = String.format("curColor=%s，不支持的equipPos=%s！", curColor, equipPos);
        throw new BusinessLogicMismatchConditionException(msg);
    }


    /**
     * 装备升品需要的战队等级
     *
     * @param equipPos
     * @param curColor
     * @return
     */
    public static int upQualityNeedPlayerLv(EquipmentPositionEnum equipPos, ColorDetailTypeEnum curColor) {
        switch (equipPos) {
            case POS_A:
            case POS_B:
            case POS_C:
            case POS_D:
                return Table_EquipmentUpQualityLvConsumes_Row.getUpOneQualityRow(curColor, equipPos).getNeedPlayerLv();
            case POS_E:
            case POS_F:
                return Table_BadgeCheatsUpQualityLvConsumes_Row.getUpOneQualityRow(curColor, equipPos).getNeedPlayerLv();
        }
        String msg = String.format("curColor=%s，不支持的equipPos=%s！", curColor, equipPos);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    /**
     * ABCD武器位置，当前颜色的最大等级
     *
     * @param color
     * @return
     */
    public static int curColorMaxLv_ABCD(ColorDetailTypeEnum color) {
        ListCell<Integer> listCell = AllServerConfig.Equipment_ABCD_UpQuality_Need_Lv.getConfig();
        return listCell.get(indexOfColor(color));
    }

    /**
     * EF武器位置，当前颜色的最大等级
     *
     * @param color
     * @return
     */
    public static int curColorMaxLv_EF(ColorDetailTypeEnum color) {
        ListCell<Integer> listCell = AllServerConfig.Equipment_EF_UpQuality_Need_Lv.getConfig();
        return listCell.get(indexOfColor(color));
    }


    /**
     * 获取下一个品质
     *
     * @param color
     * @return
     */
    public static ColorDetailTypeEnum nextColor(ColorDetailTypeEnum color) {
        boolean find = false;
        for (ColorDetailTypeEnum type : ColorDetailTypeEnum.values()) {
            if (find) {
                return type;
            }
            if (type == color) {
                find = true;
            }
        }
        String msg = String.format("curColor=%s，未找到那个下一个品质颜色！", color);
        throw new BusinessLogicMismatchConditionException(msg);
    }


    /**
     * 获取上一个品质
     *
     * @param color
     * @return
     */
    public static ColorDetailTypeEnum preColor(ColorDetailTypeEnum color) {
        int idx = indexOfColor(color);
        int preIdx = idx - 1;
        if (idx == 0) {
            String msg = String.format("color=%s，已经是最小的品级了！", color);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        return colorOfIndex(preIdx);
    }


    /**
     * 当前最大的品质
     *
     * @return
     */
    public static ColorDetailTypeEnum maxColor() {
        ColorDetailTypeEnum[] all = ColorDetailTypeEnum.values();
        return all[all.length - 1];
    }

    /**
     * 当前最小的品质
     *
     * @return
     */
    public static ColorDetailTypeEnum minColor() {
        ColorDetailTypeEnum[] all = ColorDetailTypeEnum.values();
        return all[0];
    }


    /**
     * ABCD装备位置 尝试升级到限制的等级
     *
     * @param limitLv
     * @param oriLv
     * @param allHasMoney
     * @return
     */
    public static UpgradeResult_ABCD tryUpLvToLimitLv_ABCD(int heroTpId, EquipmentPositionEnum position, int limitLv, int oriLv, long allHasMoney) {
        int times = 0;
        int newLv = oriLv;
        long allNeedMoney = 0;
        for (; ; ) {
            // 计算升级1级的消耗
            int oneNeedMoney = Table_EquipmentUpLvConsumes_Row.getConsumes(heroTpId, position, newLv);
            long nextAllNeedMoney = allNeedMoney + oneNeedMoney;
            UpgradeResult_ABCD upgradeResult = new UpgradeResult_ABCD(times, oriLv, newLv, limitLv, allNeedMoney, nextAllNeedMoney, allHasMoney);
            if (newLv >= limitLv) {
                return upgradeResult;
            }
            if ((allHasMoney - allNeedMoney - oneNeedMoney) < 0) {
                return upgradeResult;
            }
            allNeedMoney += oneNeedMoney;
            newLv = newLv + 1;
            times++;
        }
    }

    /**
     * EF装备位置 尝试升级到限制的等级
     *
     * @param limitLv
     * @param oriLv
     * @param allHasExp
     * @return
     */
    public static UpgradeResult_EF tryUpLvToLimitLv_EF(int limitLv, int oriLv, long allHasExp, long ovfExp) {
        int times = 0;
        int newLv = oriLv;
        long allNeedExp = 0;
        for (; ; ) {
            // 计算升级1级的消耗
            int oneNeedExp = Table_Exp_Row.getBadgeOrCheatsUpNeedExp(newLv);
            long nextAllNeedExp = allNeedExp + oneNeedExp - ovfExp;
            UpgradeResult_EF upgradeResult = new UpgradeResult_EF(times, oriLv, newLv, limitLv, (allNeedExp - ovfExp), nextAllNeedExp, allHasExp);
            if (newLv >= limitLv) {
                return upgradeResult;
            }
            if ((allHasExp - nextAllNeedExp) < 0) {
                return upgradeResult;
            }
            allNeedExp += oneNeedExp;
            newLv = newLv + 1;
            times++;
        }
    }


    /**
     * 背包中所有指定类型的（经验徽章/经验秘籍）经验总和
     *
     * @param itemBag
     * @param smallType
     * @return
     */
    public static long sumExpOfAllExpItem(ItemBag itemBag, ItemSmallTypeEnum smallType) {
        long sum = 0;
        if (smallType == ItemSmallTypeEnum.IST_EXP_BADGE) {
            sum += _oneTypeExp(itemBag, SpecialItemTemplateIdEnum.SPE_HIGH_BADGE_EXP_VALUE, smallType);
            sum += _oneTypeExp(itemBag, SpecialItemTemplateIdEnum.SPE_MIDDLE_BADGE_EXP_VALUE, smallType);
            sum += _oneTypeExp(itemBag, SpecialItemTemplateIdEnum.SPE_SUPER_BADGE_EXP_VALUE, smallType);
        } else if (smallType == ItemSmallTypeEnum.IST_EXP_CHEATS) {
            sum += _oneTypeExp(itemBag, SpecialItemTemplateIdEnum.SPE_HIGH_CHEATS_EXP_VALUE, smallType);
            sum += _oneTypeExp(itemBag, SpecialItemTemplateIdEnum.SPE_MIDDLE_CHEATS_EXP_VALUE, smallType);
            sum += _oneTypeExp(itemBag, SpecialItemTemplateIdEnum.SPE_SUPER_CHEATS_EXP_VALUE, smallType);
        }
        return sum;
    }

    /**
     * 背包中一个种类经验道具的经验值总和
     *
     * @param itemBag
     * @param tpId
     * @param smallType
     * @return
     */
    private static long _oneTypeExp(ItemBag itemBag, int tpId, ItemSmallTypeEnum smallType) {
        int oneExp = 0;
        if (smallType == ItemSmallTypeEnum.IST_EXP_BADGE) {
            oneExp = RootTc.get(Table_Item_Row.class, tpId).getBadgeExp();
        } else if (smallType == ItemSmallTypeEnum.IST_EXP_CHEATS) {
            oneExp = RootTc.get(Table_Item_Row.class, tpId).getCheatsExp();
        }
        long has = ItemBagUtils.templateItemCount(itemBag, tpId);
        return oneExp * has;
    }


    /**
     * 计算所有道具的经验值总和，道具要么都是经验徽章，要么都是经验秘籍
     *
     * @param idMaptoCount
     * @param smallType
     * @return
     */
    public static long calcuSumExpOfItems(IdMaptoCount idMaptoCount, ItemSmallTypeEnum smallType) {
        int sum = 0;
        for (IdAndCount idAndCount : idMaptoCount.getAll()) {
            int tpId = idAndCount.getId();
            long count = idAndCount.getCount();
            if (smallType == ItemSmallTypeEnum.IST_EXP_BADGE) {
                sum += count * RootTc.get(Table_Item_Row.class, tpId).getBadgeExp();
            } else if (smallType == ItemSmallTypeEnum.IST_EXP_CHEATS) {
                sum += count * RootTc.get(Table_Item_Row.class, tpId).getCheatsExp();
            }
        }
        return sum;
    }

    /**
     * 根据需要的经验值，使用背包中经验道具
     *
     * @param sum
     * @param itemBag
     * @param smallType
     * @return
     */
    public static IdMaptoCount convertExpToExpItem(long sum, ItemBag itemBag, ItemSmallTypeEnum smallType) {
        long sumNew = sum;
        IdMaptoCount ret = new IdMaptoCount();
        if (smallType == ItemSmallTypeEnum.IST_EXP_BADGE) {
            sumNew = _convertExpToOneTypeExpItem(sumNew, ret, itemBag, SpecialItemTemplateIdEnum.SPE_HIGH_BADGE_EXP_VALUE, smallType);
            sumNew = _convertExpToOneTypeExpItem(sumNew, ret, itemBag, SpecialItemTemplateIdEnum.SPE_MIDDLE_BADGE_EXP_VALUE, smallType);
            sumNew = _convertExpToOneTypeExpItem(sumNew, ret, itemBag, SpecialItemTemplateIdEnum.SPE_SUPER_BADGE_EXP_VALUE, smallType);
        } else if (smallType == ItemSmallTypeEnum.IST_EXP_CHEATS) {
            sumNew = _convertExpToOneTypeExpItem(sumNew, ret, itemBag, SpecialItemTemplateIdEnum.SPE_HIGH_CHEATS_EXP_VALUE, smallType);
            sumNew = _convertExpToOneTypeExpItem(sumNew, ret, itemBag, SpecialItemTemplateIdEnum.SPE_MIDDLE_CHEATS_EXP_VALUE, smallType);
            sumNew = _convertExpToOneTypeExpItem(sumNew, ret, itemBag, SpecialItemTemplateIdEnum.SPE_SUPER_CHEATS_EXP_VALUE, smallType);
        }
        return ret;
    }

    /**
     * 使用背包中一个种类的经验值
     *
     * @param sum          总的需要转换的经验值
     * @param idMaptoCount 已经使用了的经验道具
     * @param itemBag
     * @param tpId
     * @param smallType
     * @return 剩余转换的经验值  <=0 表示：已经有足够的经验道具
     */
    private static long _convertExpToOneTypeExpItem(long sum, IdMaptoCount idMaptoCount, ItemBag itemBag, int tpId, ItemSmallTypeEnum smallType) {
        if (sum <= 0) {
            return sum;
        }
        long need;
        int oneExp = _getOneItemExp(tpId, smallType);
        long has = ItemBagUtils.templateItemCount(itemBag, tpId);
        if (has * oneExp >= sum) {
            need = (int) (sum % oneExp == 0 ? sum / oneExp : (sum / oneExp) + 1);
        } else {
            need = has;
        }
        idMaptoCount.add(new IdAndCount(tpId, need));
        return sum - need * oneExp;
    }

    /**
     * 获取一个道具提供的经验值
     *
     * @param tpId
     * @param smallType
     * @return
     */
    private static int _getOneItemExp(int tpId, ItemSmallTypeEnum smallType) {
        if (smallType == ItemSmallTypeEnum.IST_EXP_BADGE) {
            return RootTc.get(Table_Item_Row.class, tpId).getBadgeExp();
        } else if (smallType == ItemSmallTypeEnum.IST_EXP_CHEATS) {
            return RootTc.get(Table_Item_Row.class, tpId).getCheatsExp();
        }
        return 0;
    }

    /**
     * ABCD:装备刚刚【突破后】，装备的等级对应的装备的品级
     *
     * @param lv
     * @return
     */
    public static ColorDetailTypeEnum colorOfLv_ABCD(int lv) {
        ListCell<Integer> listCell = AllServerConfig.Equipment_ABCD_UpQuality_Need_Lv.getConfig();
        int idx = listCell.getAll().indexOf(lv) + 1;
        return colorOfIndex(idx);
    }


    /**
     * EF:装备刚刚【突破后】，装备的等级对应的装备的品级
     *
     * @param lv
     * @return
     */
    public static ColorDetailTypeEnum colorOfLv_EF(int lv) {
        ListCell<Integer> listCell = AllServerConfig.Equipment_EF_UpQuality_Need_Lv.getConfig();
        int idx = listCell.getAll().indexOf(lv) + 1;
        return colorOfIndex(idx);
    }


    /**
     * 装备EF升级
     *
     * @param expOffered
     * @param equipment
     */
    public static void upExpLv_EF(long expOffered, int maxLv, Equipment equipment) {
        int curEquipLv = equipment.getLv();
        long curEquipOvfExp = equipment.getOvfExp();
        LevelUpObj levelUpObj = new LevelUpObj(curEquipLv, curEquipOvfExp);
        UpgradeLevel.levelUpKeepOvf(levelUpObj, expOffered, maxLv, (oldLevel) -> {
            return Table_Exp_Row.getBadgeOrCheatsUpNeedExp(oldLevel);
        });
        equipment.setLv(levelUpObj.getLevel());
        equipment.setOvfExp(levelUpObj.getOvfExp());
    }


    public static class UpgradeResult_ABCD extends UpgradeResult {
        private long allNeedMoney;
        private long nextAllNeedMoney;
        private long allHasMoney;

        public UpgradeResult_ABCD(int times, int oriLv, int newLv, int limitLv, long allNeedMoney, long nextAllNeedMoney, long allHasMoney) {
            super(times, oriLv, newLv, limitLv);
            this.allNeedMoney = allNeedMoney;
            this.nextAllNeedMoney = nextAllNeedMoney;
            this.allHasMoney = allHasMoney;
        }

        public long getAllNeedMoney() {
            return allNeedMoney;
        }

        public long getNextAllNeedMoney() {
            return nextAllNeedMoney;
        }

        public long getAllHasMoney() {
            return allHasMoney;
        }

        @Override
        public String toString() {
            StringBuffer t = new StringBuffer();
            t.append("\nMoney-->:")
                    .append(" allNeedMoney=" + allNeedMoney)
                    .append(" nextAllNeedMoney=" + nextAllNeedMoney)
                    .append(" allHasMoney=" + allHasMoney);
            return super.toString() + t;
        }
    }

    public static class UpgradeResult_EF extends UpgradeResult {
        private long allNeedExp;
        private long allNewNeedExp;
        private long nextAllNeedExp;
        private long allHasExp;
        private long allHasMoney;
        private long newNeedMoney;
        private boolean success;


        public UpgradeResult_EF(int times, int oriLv, int newLv, int limitLv, long allNeedExp, long nextAllNeedExp, long allHasExp) {
            super(times, oriLv, newLv, limitLv);
            this.allNeedExp = allNeedExp;
            this.nextAllNeedExp = nextAllNeedExp;
            this.allHasExp = allHasExp;
        }

        public long getAllNeedExp() {
            return allNeedExp;
        }

        public long getNextAllNeedExp() {
            return nextAllNeedExp;
        }

        public long getAllHasExp() {
            return allHasExp;
        }

        public long getAllHasMoney() {
            return allHasMoney;
        }

        public void setAllHasMoney(long allHasMoney) {
            this.allHasMoney = allHasMoney;
        }

        public long getNewNeedMoney() {
            return newNeedMoney;
        }

        public void setNewNeedMoney(long newNeedMoney) {
            this.newNeedMoney = newNeedMoney;
        }

        public void setAllNewNeedExp(long allNewNeedExp) {
            this.allNewNeedExp = allNewNeedExp;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        @Override
        public String toString() {
            StringBuffer t = new StringBuffer();
            t.append("\nExp-->:")
                    .append(" allNeedExp=" + allNeedExp)
                    .append(" allNewNeedExp=" + allNewNeedExp)
                    .append(" nextAllNeedExp=" + nextAllNeedExp)
                    .append(" allHasExp=" + allHasExp)
                    .append(" allHasMoney=" + allHasMoney)
                    .append(" newNeedMoney=" + newNeedMoney)
                    .append(" success=" + success);
            return super.toString() + t;
        }
    }

    public static class UpgradeResult {
        protected int times;
        protected int oriLv;
        protected int newLv;
        protected int limitLv;


        protected UpgradeResult(int times, int oriLv, int newLv, int limitLv) {
            this.times = times;
            this.oriLv = oriLv;
            this.newLv = newLv;
            this.limitLv = limitLv;
        }


        public int getTimes() {
            return times;
        }

        public int getOriLv() {
            return oriLv;
        }

        public int getNewLv() {
            return newLv;
        }

        public int getLimitLv() {
            return limitLv;
        }

        @Override
        public String toString() {
            StringBuffer t = new StringBuffer();
            t.append("Rs-->:")
                    .append(" times=" + this.times)
                    .append(" oriLv=" + this.oriLv)
                    .append(" newLv=" + this.newLv)
                    .append(" limitLv=" + this.limitLv);
            return t.toString();
        }
    }
}
