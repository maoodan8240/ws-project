package ws.gameServer.features.standalone.extp.pyActivities.soulBox.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.heros.HerosExtp;
import ws.gameServer.features.standalone.extp.heros.ctrl.HerosCtrl;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.pyActivities.core.PyActivitiesExtp;
import ws.gameServer.features.standalone.extp.pyActivities.core.ctrl.PyActivitiesCtrl;
import ws.gameServer.features.standalone.extp.pyActivities.core.utils.PyActivitiesCtrlUtils;
import ws.gameServer.features.standalone.extp.pyActivities.soulBox.utils.SoulBoxCtrlUtils;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.EnumsProtos.ActivitySpecialIdsEnum;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.protos.PyActivitySoulBoxProtos.Sm_PyActivity_SoulBox;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.tableRows.Table_DropLibrary_Row;
import ws.relationship.table.tableRows.Table_SoulBox_Row;
import ws.relationship.topLevelPojos.soulBox.SoulBox;
import ws.relationship.utils.ProtoUtils;
import ws.relationship.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class _SoulBoxCtrl extends AbstractPlayerExteControler<SoulBox> implements SoulBoxCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_SoulBoxCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;
    private HerosExtp herosExtp;
    private HerosCtrl herosCtrl;
    private PyActivitiesCtrl pyActivitiesCtrl;

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
        herosExtp = getPlayerCtrl().getExtension(HerosExtp.class);
        herosCtrl = herosExtp.getControlerForQuery();
        pyActivitiesCtrl = getPlayerCtrl().getExtension(PyActivitiesExtp.class).getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
    }

    @Override
    public void sync() {

        SenderFunc.sendInner(this, Sm_PyActivity_SoulBox.class, Sm_PyActivity_SoulBox.Builder.class, Sm_PyActivity_SoulBox.Action.RESP_SYNC, (b, br) -> {
            if (_hasSoulBoxHero()) {
                b.addAllHeroIds(target.getSoulBoxHeroIds());
            }
        });

    }

    @Override
    public void pick(int pickTimes, int pickId) {
        int realmActId = pyActivitiesCtrl.hasGetSpecialGroupActivity(ActivitySpecialIdsEnum.SPID_HERO_BOX_VALUE);
        if (realmActId <= 0) {
            return;
        }
        if (PyActivitiesCtrlUtils.isRealmActOverEndTime(realmActId, getPlayerCtrl().getOuterRealmId())) {
            String msg = "活动已经结束了, 不能继续抽卡";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        LogicCheckUtils.validateParam(Integer.class, pickId);
        if (_hasSoulBoxHero()) {
            String msg = "有未选择的魂匣英雄, 不能抽卡";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdAndCount consume = _getConsumeByTimes(pickTimes, pickId);
        LogicCheckUtils.canRemove(itemIoCtrl, consume);


        IdMaptoCount buyItem = new IdMaptoCount();
        List<IdMaptoCount> dropItemListToShow = new ArrayList<>();
        IdMaptoCount dropItemList = new IdMaptoCount();
        LOGGER.debug("累计消耗为={}", target.getConsume());
        for (int i = 1; i <= pickTimes; i++) {
            List<IdMaptoCount> dropList = _pick(pickId);
            LOGGER.debug("抽取第" + i + "次,奖品是:" + dropList.toString());
            dropItemListToShow.addAll(dropList);
            buyItem.addAll(_getBuyItem(pickId));
        }
        Sm_PyActivity_SoulBox.Action action;
        if (pickTimes == MagicNumbers.SOULBOX_FIVE_PICK_CARD) {
            action = Sm_PyActivity_SoulBox.Action.RESP_PICK_FIVE;
        } else {
            action = Sm_PyActivity_SoulBox.Action.RESP_PICK;
        }
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(action).removeItem(consume);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(action).addItem(buyItem);
        IdMaptoCount refresh_2 = itemIoExtp.getControlerForUpdate(action).addItem(dropItemList);
        target.setConsume(target.getConsume() + consume.getIntCount());
        SenderFunc.sendInner(this, Sm_PyActivity_SoulBox.class, Sm_PyActivity_SoulBox.Builder.class, action, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh).addAll(refresh_1).addAll(refresh_2), br);
            b.setDropItem(ProtoUtils.create_Sm_Common_IdMaptoCountList(dropItemListToShow));
            b.addAllHeroIds(target.getSoulBoxHeroIds());
        });
        save();
    }

    private IdMaptoCount _getBuyItem(int pickId) {
        int dropId = Table_SoulBox_Row.getBuyItem(pickId);
        return Table_DropLibrary_Row.drop(dropId, getPlayerCtrl().getCurLevel());
    }


    private List<IdMaptoCount> _pick(int pickId) {
        Table_SoulBox_Row soulBoxRow = SoulBoxCtrlUtils.getSoulBoxRowById(pickId);
        List<Integer> dropLibIds = Table_SoulBox_Row.getDropLibraryIds(pickId);
        List<IdMaptoCount> idMaptoCountList = Table_DropLibrary_Row.dropList(dropLibIds, getPlayerCtrl().getCurLevel());
        int pickRate = _getPickRate(soulBoxRow);
        LOGGER.debug("抽魂匣机率={}", pickRate);
        if (pickRate > MagicNumbers.DEFAULT_ZERO && RandomUtils.isDropPartsFractionOfBase(pickRate, MagicNumbers.RANDOM_BASE_VALUE)) {
            LOGGER.debug("获得抽魂匣机机会 ={}", pickRate);
            int dropId7 = soulBoxRow.getDropLib7();
            IdMaptoCount dropItem = Table_DropLibrary_Row.drop(dropId7, getPlayerCtrl().getCurLevel());
            if (dropItem.getAll().size() > MagicNumbers.DEFAULT_ZERO) {
                LOGGER.debug("抽到魂匣英雄={}", dropItem.toString());
                _cacheHero(dropItem);
                _clearConsume();
            }
        }
        return idMaptoCountList;
    }

    /**
     * 缓存抽中的魂匣英雄
     *
     * @param dropItem
     */
    private void _cacheHero(IdMaptoCount dropItem) {
        List<IdAndCount> idAndCounts = dropItem.getAll();
        for (IdAndCount idAndCount : idAndCounts) {
            target.getSoulBoxHeroIds().add(idAndCount.getId());
        }
    }

    /**
     * 重置累计消耗
     */
    private void _clearConsume() {
        target.setConsume(MagicNumbers.DEFAULT_ZERO);
    }

    /**
     * 获取概率
     *
     * @param soulBoxRow
     * @return
     */
    private int _getPickRate(Table_SoulBox_Row soulBoxRow) {
        int hasConsume = target.getConsume();
        List<TupleCell<Integer>> pickRate = soulBoxRow.getPickRate().getAll();
        int previous = MagicNumbers.DEFAULT_ZERO;
        for (TupleCell<Integer> tupleCell : pickRate) {
            if (hasConsume >= tupleCell.get(TupleCell.FIRST)) {
                previous = tupleCell.get(TupleCell.SECOND);
            } else if (hasConsume < tupleCell.get(TupleCell.FIRST)) {
                return previous;
            }
        }
        return previous;
    }


    /**
     * 根据次数计算消耗
     *
     * @param pickTimes
     * @param pickId
     * @return
     */
    private IdAndCount _getConsumeByTimes(int pickTimes, int pickId) {
        Table_SoulBox_Row soulBoxRow = SoulBoxCtrlUtils.getSoulBoxRowById(pickId);
        int consume = soulBoxRow.getOneTimeConsume();
        return new IdAndCount(ResourceTypeEnum.RES_VIPMONEY_VALUE, consume * pickTimes);

    }

    @Override
    public void select(int heroId) {
        LogicCheckUtils.validateParam(Integer.class, heroId);
        if (!_hasThisHeroInSoulBox(heroId)) {
            String msg = "选择的英雄ID不在抽中的魂匣中, 选择的heroId:" + heroId;
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(Sm_PyActivity_SoulBox.Action.RESP_SELECT).addItem(new IdAndCount(heroId));
        List<IdMaptoCount> idMapToCountList = new ArrayList<>();
        idMapToCountList.add(new IdMaptoCount(new IdAndCount(heroId)));
        _clearHeros();
        SenderFunc.sendInner(this, Sm_PyActivity_SoulBox.class, Sm_PyActivity_SoulBox.Builder.class, Sm_PyActivity_SoulBox.Action.RESP_SELECT, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
            b.setDropItem(ProtoUtils.create_Sm_Common_IdMaptoCountList(idMapToCountList));
        });
        save();
    }

    /**
     * 清空抽中的魂匣英雄
     */
    private void _clearHeros() {
        target.getSoulBoxHeroIds().clear();
    }

    private boolean _hasThisHeroInSoulBox(int heroId) {
        return target.getSoulBoxHeroIds().contains(heroId);
    }

    private boolean _hasSoulBoxHero() {
        return !target.getSoulBoxHeroIds().isEmpty();
    }


}

