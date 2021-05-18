package ws.gameServer.features.standalone.extp.hof.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.di.GlobalInjector;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.heros.HerosExtp;
import ws.gameServer.features.standalone.extp.heros.ctrl.HerosCtrl;
import ws.gameServer.features.standalone.extp.hof.utils.HOFCtrlProtos;
import ws.gameServer.features.standalone.extp.hof.utils.HOFCtrlUtils;
import ws.gameServer.features.standalone.extp.itemBag.ItemBagExtp;
import ws.gameServer.features.standalone.extp.itemBag.ctrl.ItemBagCtrl;
import ws.gameServer.features.standalone.extp.itemBag.utils.ItemBagUtils;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.gameServer.features.standalone.extp.utils.UpgradeLevel;
import ws.gameServer.system.date.dayChanged.DayChanged;
import ws.protos.HOFProtos.Cm_HOF_HeroAndFood;
import ws.protos.HOFProtos.Sm_HOF;
import ws.protos.HOFProtos.Sm_HOF.Action;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_Exp_Row;
import ws.relationship.table.tableRows.Table_Favorable_Row;
import ws.relationship.table.tableRows.Table_HOF_Row;
import ws.relationship.table.tableRows.Table_Item_Row;
import ws.relationship.topLevelPojos.common.LevelUpObj;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.topLevelPojos.hof.HOF;
import ws.relationship.topLevelPojos.hof.HOF_Info;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class _HOFCtrl extends AbstractPlayerExteControler<HOF> implements HOFCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_HOFCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;
    private ItemBagCtrl itemBagCtrl;
    private HerosCtrl herosCtrl;

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
        itemBagCtrl = getPlayerCtrl().getExtension(ItemBagExtp.class).getControlerForQuery();
        herosCtrl = getPlayerCtrl().getExtension(HerosExtp.class).getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
        target.setLastResetDay(GlobalInjector.getInstance(DayChanged.class).getDayChangedStr());
    }

    @Override
    public void sync() {
        if (!HOFCtrlUtils.isHOFOpened(getPlayerCtrl().getCurLevel())) {
            String msg = String.format("未到功能开启等级");
            throw new BusinessLogicMismatchConditionException(msg);
        }
        SenderFunc.sendInner(this, Sm_HOF.class, Sm_HOF.Builder.class, Sm_HOF.Action.RESP_SYNC, (b, br) -> {
            b.addAllHofInfo(HOFCtrlProtos.create_Sm_HOF_Info_List(target.getIdToHOFInfo()));
        });
    }


    @Override
    public void eat(List<Cm_HOF_HeroAndFood> heroAndFoodList) {
        Action action = Sm_HOF.Action.RESP_EAT;
        Map<Integer, IdMaptoCount> heroIdToFoodIds = HOFCtrlUtils.getHeroIdToFoodIds(heroAndFoodList);
        if (!HOFCtrlUtils.isHOFOpened(getPlayerCtrl().getCurLevel())) {
            String msg = String.format("未到功能开启等级");
            throw new BusinessLogicMismatchConditionException(msg);
        }
        int heroId = new ArrayList<>(heroIdToFoodIds.keySet()).get(MagicNumbers.DEFAULT_ZERO);
        if (!itemIoCtrl.canRemove(new IdAndCount(heroId, MagicNumbers.DEFAULT_ONE))) {
            String msg = String.format("没有这个英雄,无法喂养 heroId=%s", heroId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        Hero hero = herosCtrl.getHero(heroId);
        HOF_Info hofInfo = _getHeroHOFInfo(hero.getTpId());
        IdMaptoCount foodIdsAndCount = heroIdToFoodIds.get(hero.getId());
        if (!_doesHeroCanEatFood(hero.getTpId(), foodIdsAndCount)) {
            String msg = String.format("食物和英雄不匹配");
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!itemIoCtrl.canRemove(foodIdsAndCount)) {
            String msg = String.format("食品不足,无法喂养");
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (_isMaxLevel(hofInfo)) {
            String msg = String.format("好感度等级已经满了");
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdMaptoCount refresh1 = itemIoExtp.getControlerForUpdate(action).removeItem(foodIdsAndCount);
        _eat(hero.getTpId(), foodIdsAndCount);
        save();
        SenderFunc.sendInner(this, Sm_HOF.class, Sm_HOF.Builder.class, action, (b, br) -> {
            b.addHofInfo(HOFCtrlProtos.create_Sm_HOF_Info(target.getIdToHOFInfo().get(hero.getTpId())));
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh1), br);
        });
    }

    @Override
    public void breakthrough(int heroId) {
        LogicCheckUtils.validateParam(Integer.class, heroId);
        Action action = Sm_HOF.Action.RESP_BREAK;
        if (!HOFCtrlUtils.isHOFOpened(getPlayerCtrl().getCurLevel())) {
            String msg = String.format("未到功能开启等级");
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!itemIoCtrl.canRemove(new IdAndCount(heroId, MagicNumbers.DEFAULT_ONE))) {
            String msg = String.format("没有这个英雄,无法喂养 heroId=%s", heroId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        Hero hero = herosCtrl.getHero(heroId);
        HOF_Info hofInfo = _getHeroHOFInfo(hero.getTpId());
        if (!_isMaxLevel(hofInfo)) {
            String msg = String.format("好感度等级未满，不能突破");
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (_isMaxStage(hofInfo) && _isMaxLevel(hofInfo)) {
            String msg = String.format("你真牛逼，已经达到了最高好感阶段的最高等级，再升就是生小孩了");
            throw new BusinessLogicMismatchConditionException(msg);
        }
        _breakthrough(hofInfo, false);
        IdMaptoCount presentIdToCount = Table_Favorable_Row.getBreakthroughPresent(hofInfo.getFavorStage());
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(action).addItem(presentIdToCount);
        save();
        SenderFunc.sendInner(this, Sm_HOF.class, Sm_HOF.Builder.class, action, (b, br) -> {
            b.addHofInfo(HOFCtrlProtos.create_Sm_HOF_Info(target.getIdToHOFInfo().get(hero.getTpId())));
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1), br);
        });

    }

    @Override
    public void eatAndBreak(int heroId) {
        Action action = Sm_HOF.Action.RESP_EAT_AND_BREAK;
        if (!HOFCtrlUtils.isHOFOpened(getPlayerCtrl().getCurLevel())) {
            String msg = String.format("未到功能开启等级");
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!itemIoCtrl.canRemove(new IdAndCount(heroId, 1))) {
            String msg = String.format("没有这个英雄,无法喂养 heroId=%s", heroId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        Hero hero = herosCtrl.getHero(heroId);
        HOF_Info hofInfo = _getHeroHOFInfo(hero.getTpId());
        if (_isMaxStage(hofInfo) && _isMaxLevel(hofInfo)) {
            String msg = String.format("你真牛逼，已经达到了最高好感阶段的最高等级，再升就是生小孩了");
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdMaptoCount foodIdsAndCount = _getEatFood(hero.getTpId());
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(action).removeItem(foodIdsAndCount);
        int upStageCount = _eatAndBreak(hero.getTpId(), foodIdsAndCount);
        //如果upStageCount不为0说明升过阶
        IdMaptoCount refresh_2 = new IdMaptoCount();
        if (upStageCount != 0) {
            refresh_2.addAll(itemIoExtp.getControlerForUpdate(action).addItem(_getUpStagePresent(hofInfo, upStageCount)));
        }
        save();
        SenderFunc.sendInner(this, Sm_HOF.class, Sm_HOF.Builder.class, action, (b, br) -> {
            b.addHofInfo(HOFCtrlProtos.create_Sm_HOF_Info(target.getIdToHOFInfo().get(hero.getTpId())));
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1).addAll(refresh_2), br);
        });
    }


    /**
     * 根据突破阶段返回突破奖励
     *
     * @param hofInfo
     * @param upStageCount
     * @return
     */
    private IdMaptoCount _getUpStagePresent(HOF_Info hofInfo, int upStageCount) {
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        for (int i = 1; i <= upStageCount; i++) {
            IdMaptoCount presentIdToCount = Table_Favorable_Row.getBreakthroughPresent(hofInfo.getFavorStage() - i);
            idMaptoCount.addAll(presentIdToCount);
        }
        return idMaptoCount;
    }

    /**
     * 突破
     *
     * @param hofInfo
     * @param saveExp 是否保留经验
     */
    private void _breakthrough(HOF_Info hofInfo, boolean saveExp) {
        Table_Favorable_Row favorableRow = HOFCtrlUtils.getFavorableRow(hofInfo.getFavorStage());
        hofInfo.setFavorStage(favorableRow.getAfterClass());
        hofInfo.setFavorLevel(0);
        if (!saveExp) {
            hofInfo.setOvfFavoExp(0);
        }
    }


    /**
     * 是否可以突破
     *
     * @param hofInfo
     * @return
     */
    private boolean _isCanBreakthrough(HOF_Info hofInfo) {
        Table_Favorable_Row favorableRow = RootTc.get(Table_Favorable_Row.class).get(hofInfo.getFavorStage());
        if (favorableRow.getAfterClass() != 0) {
            return _isMaxLevel(hofInfo);
        }
        return false;
    }

    /**
     * 获取可以喂养的食品（全吃或者正好满足所需经验值）
     *
     * @param heroId
     * @return
     */
    private IdMaptoCount _getEatFood(int heroId) {
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        HOF_Info hofInfo = _getHeroHOFInfo(heroId);
        long hasExp = hofInfo.getHasExp();
        long sumExp = Table_Exp_Row.getSumExp();
        Map<Integer, Long> foodIdsAndExp = _hasFoodExpToMap(heroId);
        // 身上的食品是否能超过最高阶最高等级最高经验，如果不能，吃掉所有食品
        if (!_isCanbeMax(heroId)) {
            for (Integer id : foodIdsAndExp.keySet()) {
                long count = ItemBagUtils.templateItemCount(itemBagCtrl.getTarget(), id);
                idMaptoCount.add(new IdAndCount(id, count));
            }
        } else { //食品经验总数超过了所需要的经验，从最低级等级的食品开始吃
            long needExp = sumExp - hasExp;
            for (Entry<Integer, Long> entry : foodIdsAndExp.entrySet()) {
                // 当前食品的所有经验超过需要的经验,直接算needExp是几个食品，扣除
                int oneExp = RootTc.get(Table_Item_Row.class).get(entry.getKey()).getUseItemNumber();
                if (entry.getValue() > needExp) {
                    int count = (int) (needExp % oneExp == 0 ? (needExp / oneExp) : (needExp / oneExp) + 1);
                    idMaptoCount.add(new IdAndCount(entry.getKey(), count));
                    break;
                } else { // 没超过所需要的满级经验，全吃
                    long count = ItemBagUtils.templateItemCount(itemBagCtrl.getTarget(), entry.getKey());
                    idMaptoCount.add(new IdAndCount(entry.getKey(), count));
                    needExp = needExp - (oneExp * count);
                }
            }
        }
        return idMaptoCount;

    }

    /**
     * 吃食物
     *
     * @param heroId
     * @param foodIdsAndCount
     */
    private void _eat(int heroId, IdMaptoCount foodIdsAndCount) {
        HOF_Info hofInfo = _getHeroHOFInfo(heroId);
        int exp = _mathExp(foodIdsAndCount);
        hofInfo = _addExp(hofInfo, exp);
        System.out.println("hofInfo.>>>>>>>>>>>>>>>>>hofInfo.hasExp = = " + hofInfo.getHasExp() + ",exp==" + exp);
        target.getIdToHOFInfo().put(heroId, hofInfo);
    }


    /**
     * 身上的食品是否可以喂养到满级经验溢出
     *
     * @param heroId
     * @return
     */
    private boolean _isCanbeMax(int heroId) {
        HOF_Info hofInfo = _getHeroHOFInfo(heroId);
        long hasExp = hofInfo.getHasExp();
        long sumExp = Table_Exp_Row.getSumExp();
        long hasFoodExp = _hasFoodExpToLong(_hasFoodExpToMap(heroId));
        return hasExp + hasFoodExp > sumExp;
    }

    /**
     * 喂养和突破，返回突破了几阶
     *
     * @param heroId
     * @return
     */
    private int _eatAndBreak(int heroId, IdMaptoCount foodIdsAndCount) {
        HOF_Info hofInfo = _getHeroHOFInfo(heroId);
        int oldStage = hofInfo.getFavorStage();
        int exp = _mathExp(foodIdsAndCount);
        hofInfo = _addExpAndBreak(hofInfo, exp);
        System.out.println(">>>>>>>>>>>>>>>>>hofInfo.hasExp = " + hofInfo.getHasExp() + ",exp==" + exp);
        target.getIdToHOFInfo().put(heroId, hofInfo);
        return hofInfo.getFavorStage() - oldStage;
    }

    /**
     * 获取传入食品的总经验值
     *
     * @param foodIdsAndExp
     * @return
     */
    private long _hasFoodExpToLong(Map<Integer, Long> foodIdsAndExp) {
        long exp = 0;
        for (Entry<Integer, Long> entry : foodIdsAndExp.entrySet()) {
            exp += entry.getValue();
        }
        return exp;
    }

    /**
     * 获取玩家身上匹配卡牌喂养的食物的经验值id对应经验值
     *
     * @param heroId
     * @return
     */
    private Map<Integer, Long> _hasFoodExpToMap(int heroId) {
        Map<Integer, Long> foodIdsAndExp = new LinkedHashMap<>();
        List<Integer> foodIds = Table_HOF_Row.getFoodIdList(heroId);
        //按照食品id排序，id小的优先使用
        Collections.sort(foodIds);
        for (Integer foodId : foodIds) {
            Table_Item_Row itemRow = RootTc.get(Table_Item_Row.class).get(foodId);
            if (itemIoCtrl.canRemove(new IdAndCount(foodId, MagicNumbers.DEFAULT_ONE))) {
                long count = ItemBagUtils.templateItemCount(itemBagCtrl.getTarget(), foodId);
                Long sumExp = (itemRow.getUseItemNumber() * count);
                foodIdsAndExp.put(foodId, sumExp);
            }
        }
        return foodIdsAndExp;
    }


    /**
     * 加经验升级突破
     *
     * @param hofInfo
     * @param exp
     * @return
     */

    private HOF_Info _addExpAndBreak(HOF_Info hofInfo, int exp) {
        hofInfo.setOvfFavoExp(hofInfo.getOvfFavoExp() + exp);
        hofInfo.setHasExp(hofInfo.getHasExp() + exp);
        exp = 0;
        for (int times = 0; times < _maxStageNum() + 1; times++) {
            if (_isMaxStage(hofInfo) && _isMaxLevel(hofInfo)) {
                return hofInfo;
            }
            //尝试突破
            if (_isCanBreakthrough(hofInfo)) {
                _breakthrough(hofInfo, true);
            }
            //获取最高等级
            int maxLevel = HOFCtrlUtils.getFavorableRow(hofInfo.getFavorStage()).getClassLevel();
            LevelUpObj levelUpObj = new LevelUpObj(hofInfo.getFavorLevel(), hofInfo.getOvfFavoExp());
            UpgradeLevel.levelUpKeepOvf(levelUpObj, exp, maxLevel, (oldLevel) -> {
                //获取下一级满级经验
                return Table_Exp_Row.getClassLevelExp(oldLevel + 1, hofInfo.getFavorStage());
            }, () -> {
                //每次加完经验尝试突破阶段
                hofInfo.setFavorLevel(levelUpObj.getLevel());
                hofInfo.setOvfFavoExp(levelUpObj.getOvfExp());
                if (_isCanBreakthrough(hofInfo)) {
                    return;
                }
                if (_isMaxStage(hofInfo) && _isMaxLevel(hofInfo)) {
                    return;
                }
            }, null);
        }
        return hofInfo;
    }


    /**
     * 加经验升级不突破
     *
     * @param hofInfo
     * @param exp
     * @return
     */
    private HOF_Info _addExp(HOF_Info hofInfo, int exp) {
        //获取最高等级
        List<Integer> useSumExp = new ArrayList<>();
        int maxLevel = HOFCtrlUtils.getFavorableRow(hofInfo.getFavorStage()).getClassLevel();
        LevelUpObj levelUpObj = new LevelUpObj(hofInfo.getFavorLevel(), hofInfo.getOvfFavoExp());
        UpgradeLevel.levelUpKeepOvf(levelUpObj, exp, maxLevel, (oldLevel) -> {
            //获取下一级满级经验
            int oneLevelExp = Table_Exp_Row.getClassLevelExp(oldLevel + 1, hofInfo.getFavorStage());
            if (levelUpObj.getLevel() != maxLevel) {
                useSumExp.add(oneLevelExp);
            }
            return oneLevelExp;
        });
        hofInfo.setFavorLevel(levelUpObj.getLevel());
        int maxExp = _getLevelMaxExp(maxLevel, hofInfo.getFavorStage());
        if (levelUpObj.getLevel() >= maxLevel && levelUpObj.getOvfExp() >= maxExp) {
            hofInfo.setHasExp(hofInfo.getHasExp() + _mathSumExp(useSumExp));
            hofInfo.setOvfFavoExp(maxExp);
        } else {
            hofInfo.setHasExp(hofInfo.getHasExp() + exp);
            hofInfo.setOvfFavoExp(levelUpObj.getOvfExp());
        }
        return hofInfo;
    }


    /**
     * 计算集合中经验值的和
     *
     * @param sumExp
     * @return
     */
    private int _mathSumExp(List<Integer> sumExp) {
        int sum = 0;
        for (Integer one : sumExp) {
            sum += one;

        }
        return sum;
    }

    /**
     * 获取当前阶段最高等级的最大经验值
     *
     * @param level
     * @param stage
     * @return
     */
    private int _getLevelMaxExp(int level, int stage) {
        return Table_Exp_Row.getClassLevelExp(level, stage);

    }

    /**
     * 检查这个英雄是否能喂养这些食品
     *
     * @param heroId
     * @param foodIdsAndCount
     * @return
     */
    private boolean _doesHeroCanEatFood(int heroId, IdMaptoCount foodIdsAndCount) {
        List<IdAndCount> list = foodIdsAndCount.getAll();
        List<Integer> foodIds = Table_HOF_Row.getFoodIdList(heroId);
        for (IdAndCount idAndCount : list) {
            if (!foodIds.contains(idAndCount.getId())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算所有食品的经验
     *
     * @param foodIdsAndCount
     * @return
     */
    private int _mathExp(IdMaptoCount foodIdsAndCount) {
        int exp = 0;
        List<IdAndCount> list = foodIdsAndCount.getAll();
        for (IdAndCount idAndCount : list) {
            exp += idAndCount.getCount() * _getExp(idAndCount.getId());
        }
        return exp;
    }


    /**
     * 获取使用道具所获得的经验值
     *
     * @param id
     * @return
     */
    private int _getExp(int id) {
        return HOFCtrlUtils.getItemRow(id).getUseItemNumber();
    }

    private HOF_Info _getHeroHOFInfo(int heroId) {
        if (target.getIdToHOFInfo().containsKey(heroId)) {
            return target.getIdToHOFInfo().get(heroId);
        }
        HOF_Info hofInfo = new HOF_Info();
        hofInfo.setHeroId(heroId);
        target.getIdToHOFInfo().put(heroId, hofInfo);
        return hofInfo;
    }


    /**
     * 是否达到好感阶段上限
     *
     * @param hofInfo
     * @return
     */
    private boolean _isMaxStage(HOF_Info hofInfo) {
        int maxStage = 0;
        List<Table_Favorable_Row> tableRowList = RootTc.get(Table_Favorable_Row.class).values();
        for (Table_Favorable_Row tableRow : tableRowList) {
            if (tableRow.getId() > maxStage) {
                maxStage = tableRow.getId();
            }
        }
        return hofInfo.getFavorStage() >= maxStage;
    }

    /**
     * 最大有几阶
     *
     * @return
     */
    private int _maxStageNum() {
        List<Table_Favorable_Row> tableRowList = RootTc.get(Table_Favorable_Row.class).values();
        return tableRowList.size();
    }


    /**
     * 是否是最高好感度等级
     *
     * @param hofInfo
     * @return
     */
    private boolean _isMaxLevel(HOF_Info hofInfo) {
        int maxLevel = HOFCtrlUtils.getFavorableRow(hofInfo.getFavorStage()).getClassLevel();
        return hofInfo.getFavorLevel() >= maxLevel;
    }

    /**
     * 是否达到好感度经验上限
     *
     * @param hofInfo
     * @return
     */
    private boolean _isExpMax(HOF_Info hofInfo) {
        int maxLevel = HOFCtrlUtils.getFavorableRow(hofInfo.getFavorStage()).getClassLevel();
        int maxExp = Table_Exp_Row.getClassLevelExp(maxLevel, hofInfo.getFavorStage());
        return hofInfo.getOvfFavoExp() >= maxExp;
    }
}
