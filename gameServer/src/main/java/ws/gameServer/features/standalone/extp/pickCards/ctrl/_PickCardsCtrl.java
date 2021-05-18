package ws.gameServer.features.standalone.extp.pickCards.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.dataCenter.enums.PrivateNotifyTypeEnum;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_NotifyMsg;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.pickCards.utils.PickCardsCtrlProtos;
import ws.gameServer.features.standalone.extp.pickCards.utils.PickCardsCtrlUtils;
import ws.gameServer.features.standalone.extp.utils.CommonUtils;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.PickCardsProtos.Sm_PickCards;
import ws.protos.PickCardsProtos.Sm_PickCards.Action;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_DropLibrary_Row;
import ws.relationship.table.tableRows.Table_PickCards_Row;
import ws.relationship.topLevelPojos.pickCards.PickCard;
import ws.relationship.topLevelPojos.pickCards.PickCards;
import ws.relationship.utils.ProtoUtils;
import ws.relationship.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;


public class _PickCardsCtrl extends AbstractPlayerExteControler<PickCards> implements PickCardsCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_PickCardsCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
    }

    @Override
    public void _initBeforeChanged() throws Exception {
        initPickCards();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
        _resetPickCard();
    }

    @Override
    public void sync() {
        SenderFunc.sendInner(this, Sm_PickCards.class, Sm_PickCards.Builder.class, Sm_PickCards.Action.RESP_SYNC, (b, br) -> {
            b.addAllPickCards(PickCardsCtrlProtos.create_Sm_OnePickCard_Lis(target.getIdToPickCard()));
        });
    }


    @Override
    public void freePick(int pickId) {
        _logicCheck_PickId(pickId);
        Table_PickCards_Row row = RootTc.get(Table_PickCards_Row.class, pickId);
        PickCard pickCard = getPickCard(pickId);
        boolean useFreeTimes = canUseFreeTimes(row, pickCard);
        if (!useFreeTimes) {
            String msg = String.format("pickId=%s 当前不可以使用免费抽卡！ 当前时间=%s， 免费时间=%s， 已经使用的免费次数=%s 总的可以使用的次数=%s .", pickId, System.currentTimeMillis(),
                    pickCard.getNextFreeTime(), pickCard.getUseFreeTimes(), row.getFreeTimes());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdAndCount idAndCount_Drop = freePick(row, pickCard);
        List<IdAndCount> idAndCountList = new ArrayList<>();
        idAndCountList.add(idAndCount_Drop);
        // 抽卡购买的对像
        IdAndCount idAndCount_ToBuy = getToBuyItemIdAndCount(row, MagicNumbers.PICKCARD_ONE_PICK_CARD);
        IdMaptoCount toAdd = new IdMaptoCount().add(idAndCount_Drop).add(idAndCount_ToBuy);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Sm_PickCards.Action.RESP_FREE_PICK).addItem(toAdd);
        SenderFunc.sendInner(this, Sm_PickCards.class, Sm_PickCards.Builder.class, Action.RESP_FREE_PICK, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1), br);
            b.addPickCards(PickCardsCtrlProtos.create_Sm_OnePickCard(pickCard));
            b.setIdcList(ProtoUtils.create_Sm_Common_IdAndCountList(idAndCountList));
        });
        save();
        statisticsDaliyPickTimes();
    }

    @Override
    public void pick(int pickId) {
        commonPick(pickId, Action.RESP_PICK, new InnerPick() {
            @Override
            public void selfCheck(Table_PickCards_Row row, PickCard pickCard) {
            }

            @Override
            public IdMaptoCount consumes(Table_PickCards_Row row, PickCard pickCard) {
                int count = MagicNumbers.PICKCARD_ONE_PICK_CARD;
                if (canUseItemPick(row, count)) {
                    return new IdMaptoCount().add(new IdAndCount(row.getItemId(), count));
                }
                if (pickCard.getDaliy1Times() == pickCard.getUseFreeTimes()) {
                    long newCount = (row.getOnePickConsume().getCount() * row.getOneTimesDiscount()) / MagicNumbers.RANDOM_BASE_VALUE;
                    return new IdMaptoCount().add(new IdAndCount(row.getOnePickConsume().getId(), newCount));
                } else {
                    return new IdMaptoCount().add(row.getOnePickConsume());
                }
            }

            @Override
            public List<IdAndCount> pick(Table_PickCards_Row row, PickCard pickCard) {
                List<IdAndCount> lis = new ArrayList<>();
                lis.add(onePickDrop(row, pickCard));
                return lis;
            }

            @Override
            public IdAndCount toBuy(Table_PickCards_Row row, PickCard pickCard) {
                return getToBuyItemIdAndCount(row, MagicNumbers.PICKCARD_ONE_PICK_CARD);
            }
        });
    }


    @Override
    public void tenPick(int pickId) {
        commonPick(pickId, Action.RESP_TEN_PICK, new InnerPick() {
            @Override
            public void selfCheck(Table_PickCards_Row row, PickCard pickCard) {
            }

            @Override
            public IdMaptoCount consumes(Table_PickCards_Row row, PickCard pickCard) {
                int count = MagicNumbers.PICKCARD_TEN_PICK_CARD;
                if (canUseItemPick(row, count)) {
                    return new IdMaptoCount().add(new IdAndCount(row.getItemId(), count));
                }
                if (pickCard.getDaliy10Times() == pickCard.getUseFreeTimes()) {
                    long newCount = (row.getTenPickConsume().getCount() * row.getTenTimesDiscount()) / MagicNumbers.RANDOM_BASE_VALUE;
                    return new IdMaptoCount().add(new IdAndCount(row.getTenPickConsume().getId(), newCount));
                } else {
                    return new IdMaptoCount().add(row.getTenPickConsume());
                }
            }

            @Override
            public List<IdAndCount> pick(Table_PickCards_Row row, PickCard pickCard) {
                return tenPickDrop(row, pickCard);
            }

            @Override
            public IdAndCount toBuy(Table_PickCards_Row row, PickCard pickCard) {
                return getToBuyItemIdAndCount(row, MagicNumbers.PICKCARD_TEN_PICK_CARD);
            }
        });
    }


    public void hundredPick(int pickId) {
        commonPick(pickId, Action.RESP_HUNDRED_PICK, new InnerPick() {
            @Override
            public void selfCheck(Table_PickCards_Row row, PickCard pickCard) {
                if (!row.getSupportHundered()) {
                    String msg = String.format("pickId=%s 不支持100连抽.", pickId);
                    throw new BusinessLogicMismatchConditionException(msg);
                }
            }

            @Override
            public IdMaptoCount consumes(Table_PickCards_Row row, PickCard pickCard) {
                int count = MagicNumbers.PICKCARD_HUNDRED_PICK_CARD;
                if (canUseItemPick(row, count)) {
                    return new IdMaptoCount().add(new IdAndCount(row.getItemId(), count));
                }
                if (pickCard.getDaliy100Times() == pickCard.getUseFreeTimes()) {
                    long newCount = (row.getHundredPickConsume().getCount() * row.getHundredTimesDiscount()) / MagicNumbers.RANDOM_BASE_VALUE;
                    return new IdMaptoCount().add(new IdAndCount(row.getHundredPickConsume().getId(), newCount));
                } else {
                    return new IdMaptoCount().add(row.getHundredPickConsume());
                }
            }

            @Override
            public List<IdAndCount> pick(Table_PickCards_Row row, PickCard pickCard) {
                return hundredPickDrop(row, pickCard);
            }

            @Override
            public IdAndCount toBuy(Table_PickCards_Row row, PickCard pickCard) {
                return getToBuyItemIdAndCount(row, MagicNumbers.PICKCARD_HUNDRED_PICK_CARD);
            }
        });
    }

    /**
     * 抽卡
     *
     * @param pickId
     * @param action
     * @param innerPick
     */
    private void commonPick(int pickId, Action action, InnerPick innerPick) {
        _logicCheck_PickId(pickId);
        Table_PickCards_Row row = RootTc.get(Table_PickCards_Row.class, pickId);
        PickCard pickCard = getPickCard(pickId);
        innerPick.selfCheck(row, pickCard); // 定制检查
        IdMaptoCount consume = innerPick.consumes(row, pickCard); // 消耗
        LogicCheckUtils.canRemove(itemIoCtrl, consume);
        List<IdAndCount> idAndCountList = innerPick.pick(row, pickCard); // 掉落
        IdMaptoCount idAndCount_Drop = new IdMaptoCount(idAndCountList);
        // 抽卡购买的对像
        IdAndCount idAndCount_ToBuy = innerPick.toBuy(row, pickCard); //
        IdMaptoCount toAdd = new IdMaptoCount().addAll(idAndCount_Drop).add(idAndCount_ToBuy);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(action).removeItem(consume);
        IdMaptoCount refresh_2 = itemIoExtp.getControlerForUpdate(action).addItem(toAdd);
        SenderFunc.sendInner(this, Sm_PickCards.class, Sm_PickCards.Builder.class, action, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1).addAll(refresh_2), br);
            b.addPickCards(PickCardsCtrlProtos.create_Sm_OnePickCard(pickCard));
            b.setIdcList(ProtoUtils.create_Sm_Common_IdAndCountList(idAndCountList));
        });
        save();
        statisticsDaliyPickTimes();
    }

    /**
     * 免费时间的抽取
     *
     * @param row
     * @param pickCard
     * @return
     */
    private IdAndCount freePick(Table_PickCards_Row row, PickCard pickCard) {
        int needPlayerLv = AllServerConfig.PickCards_NoCD_NeedPlayerLv.getConfig();
        if (getPlayerCtrl().getCurLevel() >= needPlayerLv) {
            pickCard.setNextFreeTime(-1);
        } else {
            int gap = AllServerConfig.PickCards_FreeTime_Gap.getConfig();
            pickCard.setNextFreeTime(System.currentTimeMillis() + gap * 1000l);
        }
        pickCard.setUseFreeTimes(pickCard.getUseFreeTimes() + 1);
        return onePickDrop(row, pickCard);
    }


    /**
     * 单抽
     *
     * @param row
     * @param pickCard
     */
    private IdAndCount onePickDrop(Table_PickCards_Row row, PickCard pickCard) {
        int roundTimes = pickCard.getSum1Times() % MagicNumbers.PICKCARD_TEN_PICK_CARD;
        pickCard.setSum1Times(pickCard.getSum1Times() + 1);
        pickCard.setDaliy1Times(pickCard.getDaliy1Times() + 1);
        if (pickCard.getSum1Times() == 1 && row.getFirstPick() > 0) {  // 执行首抽，历史第一次抽奖
            return new IdAndCount(row.getFirstPick());
        }
        return _drop(row, roundTimes);
    }


    /**
     * 掉落
     *
     * @param row
     * @param roundTimes
     * @return
     */
    private IdAndCount _drop(Table_PickCards_Row row, int roundTimes) {
        IdMaptoCount idMaptoCount = Table_DropLibrary_Row.drop(row.getDropLibraryId(roundTimes), getPlayerCtrl().getCurLevel());
        return CommonUtils.getFirstIdAndCount(idMaptoCount);
    }


    /**
     * 十连抽
     *
     * @param row
     * @param pickCard
     */
    private List<IdAndCount> tenPickDrop(Table_PickCards_Row row, PickCard pickCard) {
        List<IdAndCount> idAndCountList = new ArrayList<>();
        _tenDrop(row, idAndCountList);
        pickCard.setSum10Times(pickCard.getSum10Times() + 1);
        pickCard.setDaliy10Times(pickCard.getDaliy10Times() + 1);
        return idAndCountList;
    }


    /**
     * 一百连抽
     *
     * @param row
     */
    private List<IdAndCount> hundredPickDrop(Table_PickCards_Row row, PickCard pickCard) {
        List<IdAndCount> idAndCountList = new ArrayList<>();
        for (int i = 0; i < MagicNumbers.PICKCARD_TEN_PICK_CARD; i++) {
            _tenDrop(row, idAndCountList);
        }
        pickCard.setSum100Times(pickCard.getSum100Times() + 1);
        pickCard.setDaliy100Times(pickCard.getDaliy100Times() + 1);
        return idAndCountList;
    }

    /**
     * 10次掉落
     *
     * @param row
     * @param idAndCountList
     */
    private void _tenDrop(Table_PickCards_Row row, List<IdAndCount> idAndCountList) {
        int lastIdx = (MagicNumbers.PICKCARD_TEN_PICK_CARD - 1);
        int mustDrop = RandomUtils.dropBetweenTowNum(0, lastIdx); // 随机一个0～9之间的数字，用来提前第10次掉落
        int replaceIdx = 0;
        for (int i = 0; i < MagicNumbers.PICKCARD_TEN_PICK_CARD; i++) {
            int libIndx = i;
            if (i == mustDrop) {
                libIndx = lastIdx;
                replaceIdx = i;
            }
            if (i == lastIdx) {
                libIndx = replaceIdx;
            }
            idAndCountList.add(_drop(row, libIndx));
        }
    }


    /**
     * 是否可以使用免费次数
     *
     * @param pickCard
     * @return
     */
    private boolean canUseFreeTimes(Table_PickCards_Row row, PickCard pickCard) {
        return row.getFreeTimes() > pickCard.getUseFreeTimes() && pickCard.getNextFreeTime() <= System.currentTimeMillis();
    }


    /**
     * 是否先有足够的道具抽卡
     *
     * @param row
     * @param count
     * @return
     */
    private boolean canUseItemPick(Table_PickCards_Row row, int count) {
        return row.getItemId() > 0 && itemIoCtrl.canRemove(new IdMaptoCount(new IdAndCount(row.getItemId(), count)));
    }


    private PickCard getPickCard(int pickId) {
        return target.getIdToPickCard().get(pickId);
    }

    private boolean containsPickCard(int pickId) {
        return target.getIdToPickCard().containsKey(pickId);
    }


    /**
     * 添加新增的抽卡
     */
    private void initPickCards() {
        for (Table_PickCards_Row row : RootTc.get(Table_PickCards_Row.class).values()) {
            if (!target.getIdToPickCard().containsKey(row.getId())) {
                PickCard pickCard = new PickCard(row.getId());
                target.getIdToPickCard().put(row.getId(), pickCard);
            }
        }
        for (Integer id : new ArrayList<>(target.getIdToPickCard().keySet())) {
            if (!RootTc.get(Table_PickCards_Row.class).has(id)) {
                target.getIdToPickCard().remove(id);
            }
        }
    }


    /**
     * 重置每日抽卡信息
     */
    private void _resetPickCard() {
        for (PickCard pickCard : target.getIdToPickCard().values()) {
            pickCard.setUseFreeTimes(0);
            pickCard.setDaliy1Times(0);
            pickCard.setDaliy10Times(0);
            pickCard.setDaliy100Times(0);
            pickCard.setNextFreeTime(-1);
        }
    }

    /**
     * 获取每次抽卡购买的道具Id
     *
     * @param row
     * @param count
     * @return
     */
    private IdAndCount getToBuyItemIdAndCount(Table_PickCards_Row row, int count) {
        IdMaptoCount idMaptoCount = Table_DropLibrary_Row.drop(row.getToBuyLibraryId(), getPlayerCtrl().getCurLevel());
        return new IdAndCount(CommonUtils.getFirstIdAndCount(idMaptoCount).getId(), count);
    }

    private void _logicCheck_PickId(int pickId) {
        if (!RootTc.get(Table_PickCards_Row.class).has(pickId)) {
            String msg = String.format("Table_PickCards_Row 无此pickId=%s ！", pickId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!containsPickCard(pickId)) {
            String msg = String.format("target 无此pickId=%s ！", pickId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
    }


    // ======================== 数据采集 start start start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private void statisticsDaliyPickTimes() {
        int sum = PickCardsCtrlUtils.calcuDaliyPickTimes(target, MagicNumbers.PICKCARD_MONEY_ID_1);
        sum += PickCardsCtrlUtils.calcuDaliyPickTimes(target, MagicNumbers.PICKCARD_VIPMONEY_ID_2);
        Pr_NotifyMsg notifyMsg2 = new Pr_NotifyMsg(PrivateNotifyTypeEnum.PickCards_DaliyTimes, sum);
        sendPrivateMsg(notifyMsg2);
    }

    // ======================== 数据采集 end end end <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


    private interface InnerPick {
        /**
         * 定制检查
         *
         * @param row
         * @param pickCard
         */
        void selfCheck(Table_PickCards_Row row, PickCard pickCard);

        /**
         * 抽卡消耗
         *
         * @param row
         * @param pickCard
         * @return
         */
        IdMaptoCount consumes(Table_PickCards_Row row, PickCard pickCard);

        /**
         * 抽卡掉落
         *
         * @param row
         * @param pickCard
         * @return
         */
        List<IdAndCount> pick(Table_PickCards_Row row, PickCard pickCard);

        /**
         * 抽卡购买的物品
         *
         * @param row
         * @param pickCard
         * @return
         */
        IdAndCount toBuy(Table_PickCards_Row row, PickCard pickCard);
    }
}
