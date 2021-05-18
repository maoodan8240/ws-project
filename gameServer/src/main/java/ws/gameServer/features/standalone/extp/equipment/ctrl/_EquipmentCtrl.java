package ws.gameServer.features.standalone.extp.equipment.ctrl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.table.table.interfaces.cell.ListCell;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.equipment.utils.EquipmentCtrlUtils;
import ws.gameServer.features.standalone.extp.equipment.utils.EquipmentCtrlUtils.UpgradeResult;
import ws.gameServer.features.standalone.extp.equipment.utils.EquipmentCtrlUtils.UpgradeResult_ABCD;
import ws.gameServer.features.standalone.extp.equipment.utils.EquipmentCtrlUtils.UpgradeResult_EF;
import ws.gameServer.features.standalone.extp.heros.HerosExtp;
import ws.gameServer.features.standalone.extp.heros.ctrl.HerosCtrl;
import ws.gameServer.features.standalone.extp.itemBag.ItemBagExtp;
import ws.gameServer.features.standalone.extp.itemBag.ctrl.ItemBagCtrl;
import ws.gameServer.features.standalone.extp.itemBag.utils.ItemBagUtils;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.mails.MailsExtp;
import ws.gameServer.features.standalone.extp.mails.ctrl.MailsCtrl;
import ws.gameServer.features.standalone.extp.resourcePoint.ResourcePointExtp;
import ws.gameServer.features.standalone.extp.resourcePoint.ctrl.ResourcePointCtrl;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.EnumsProtos.ColorDetailTypeEnum;
import ws.protos.EnumsProtos.EquipmentPositionEnum;
import ws.protos.EnumsProtos.ItemSmallTypeEnum;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.protos.EquipmentProtos.Sm_Equipment;
import ws.protos.EquipmentProtos.Sm_Equipment.Action;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.enums.item.IdItemTypeEnum;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_Item_Row;
import ws.relationship.table.tableRows.Table_New_Card_Row;
import ws.relationship.topLevelPojos.common.TopLevelHolder;
import ws.relationship.topLevelPojos.heros.Equipment;
import ws.relationship.topLevelPojos.heros.Hero;

import java.util.List;


public class _EquipmentCtrl extends AbstractPlayerExteControler<TopLevelHolder> implements EquipmentCtrl {
    private static Logger LOGGER = LoggerFactory.getLogger(_EquipmentCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;
    private ItemBagCtrl itemBagCtrl;
    private HerosCtrl herosCtrl;
    private ResourcePointCtrl resourcePointCtrl;
    private MailsCtrl mailsCtrl;


    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();

        itemBagCtrl = getPlayerCtrl().getExtension(ItemBagExtp.class).getControlerForQuery();
        herosCtrl = getPlayerCtrl().getExtension(HerosExtp.class).getControlerForQuery();
        resourcePointCtrl = getPlayerCtrl().getExtension(ResourcePointExtp.class).getControlerForQuery();
        mailsCtrl = getPlayerCtrl().getExtension(MailsExtp.class).getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {

    }

    @Override
    public void sync() {
    }

    @Override
    public void up_ABCD_OneLv(int heroId, EquipmentPositionEnum equipPos) {
        upLv_ABCD(heroId, equipPos, false);
    }

    @Override
    public void up_ABCD_OneKeyLv(int heroId, EquipmentPositionEnum equipPos) {
        upLv_ABCD(heroId, equipPos, true);
    }

    private void upLv_ABCD(int heroId, EquipmentPositionEnum equipPos, boolean oneKey) {
        exeI(heroId, equipPos, (hero, equipment, equipTpId, curLv, curQualityLv, curColor, curMaxLvInCurQuality, curStarLv) -> {
            Sm_Equipment.Action action = Action.RESP_UP_ABCD_ONE_LV;
            if (oneKey) {
                action = Action.RESP_UP_ABCD_ONE_KEY_LV;
            }
            if (curLv >= curMaxLvInCurQuality) {
                String msg = String.format("装备需要升品后才能继续升级！curLv=%s curQualityLv=%s curMaxLv=%s", curLv, curQualityLv, curMaxLvInCurQuality);
                throw new BusinessLogicMismatchConditionException(msg);
            }
            IdMaptoCount refresh = new IdMaptoCount();
            UpgradeResult upgradeResult = _upLv_ABCD(refresh, hero, equipment, equipPos, curLv, curMaxLvInCurQuality, oneKey, action);
            if (upgradeResult.getTimes() <= 0) {
                String msg = String.format("升级失败，upgradeResult=%s, heroLv=%s, curLv=%s, curQualityLv=%s, curMaxLvInCurQuality=%s ", upgradeResult, hero.getLv(), curLv, curQualityLv, curMaxLvInCurQuality);
                throw new BusinessLogicMismatchConditionException(msg);
            }
            SenderFunc.sendInner(this, Sm_Equipment.class, Sm_Equipment.Builder.class, action, (b, br) -> {
                itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh).add(new IdAndCount(heroId)), br);
            });
            herosCtrl.getAttrsContainer().onOneHeroEquipChange(hero);
        });
    }

    @Override
    public void up_ABCD_FastLv(int heroId, EquipmentPositionEnum equipPos, int fastToLv) {
        exeI(heroId, equipPos, (hero1, equipment1, equipTpId1, curLv1, curQualityLv1, curColor1, curMaxLvInCurQuality1, curStarLv1) -> {
            if (curLv1 >= curMaxLvInCurQuality1) {
                String msg = String.format("装备需要升品后才能继续升级！curLv=%s curQualityLv=%s curMaxLv=%s", curLv1, curQualityLv1, curMaxLvInCurQuality1);
                throw new BusinessLogicMismatchConditionException(msg);
            }
            logicCheck_ABCD_FastToLv_Args(fastToLv);
            ColorDetailTypeEnum toColor = EquipmentCtrlUtils.colorOfLv_ABCD(fastToLv);
            ColorDetailTypeEnum finalColor = EquipmentCtrlUtils.preColor(toColor);
            if (EquipmentCtrlUtils.indexOfColor(finalColor) < EquipmentCtrlUtils.indexOfColor(curColor1)) {
                String msg = String.format("fastToLv=%s非法! finalColor=%s，curColor=%s！", fastToLv, finalColor, curColor1);
                throw new BusinessLogicMismatchConditionException(msg);
            }
            IdMaptoCount refresh = new IdMaptoCount();
            for (int i = 0; i < 100; i++) {
                final int idx = i;
                boolean[] isRe = new boolean[]{true, true};
                // 先尝试升级到当前品级的最大等级
                exeI(heroId, equipPos, (hero, equipment, equipTpId, curLv, curQualityLv, curColor, curMaxLvInCurQuality, curStarLv) -> {
                    if (curLv >= fastToLv) {
                        return;
                    }
                    if (curLv >= curMaxLvInCurQuality) {
                        return;
                    }
                    IdMaptoCount refresh_1 = new IdMaptoCount();
                    UpgradeResult_ABCD upgradeResult = _upLv_ABCD(refresh_1, hero, equipment, equipPos, curLv, curMaxLvInCurQuality, true, Action.RESP_UP_ABCD_FAST_LV);
                    LOGGER.debug("{}:ABCD->快速升级结果：\nupgradeResult={}", idx, upgradeResult);
                    if (upgradeResult.getTimes() > 0) {
                        isRe[0] = false;
                    }
                });
                if (isRe[0]) {
                    break;
                }
                // 尝试升品，如果宝石不足自动购买
                exeI(heroId, equipPos, (hero, equipment, equipTpId, curLv, curQualityLv, curColor, curMaxLvInCurQuality, curStarLv) -> {
                    if (EquipmentCtrlUtils.indexOfColor(finalColor) <= EquipmentCtrlUtils.indexOfColor(curColor)) {
                        return;
                    }
                    if (curLv >= curMaxLvInCurQuality) { // 去升品
                        ColorDetailTypeEnum nextColor = EquipmentCtrlUtils.nextColor(curColor);
                        int maxQualityLv = EquipmentCtrlUtils.maxColor().getNumber();
                        if (curQualityLv >= maxQualityLv) {
                            return;
                        }
                        IdMaptoCount consumes = EquipmentCtrlUtils.getUpOneQualityLvConsumes(equipPos, curColor);
                        LOGGER.debug("{}:ABCD->转换前：curColor={},自动升品消耗consumes={}", idx, consumes, curColor);
                        convertShortageToVipMoney(consumes, equipPos);
                        LOGGER.debug("{}:ABCD->转换后：curColor={},自动升品消耗consumes={}", idx, consumes, curColor);
                        if (itemIoCtrl.canRemove(consumes)) {
                            IdMaptoCount refresh_2 = itemIoExtp.getControlerForUpdate(Action.RESP_UP_ABCD_FAST_LV).removeItem(new IdMaptoCount().addAll(consumes));
                            refresh.addAll(refresh_2);
                            equipment.setQualityLv(nextColor.getNumber());
                            isRe[1] = false;
                        }
                    }
                });
                if (isRe[1]) {
                    break;
                }
            }
            SenderFunc.sendInner(this, Sm_Equipment.class, Sm_Equipment.Builder.class, Action.RESP_UP_ABCD_FAST_LV, (b, br) -> {
                itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh).add(new IdAndCount(hero1.getId())), br);
            });
            herosCtrl.getAttrsContainer().onOneHeroEquipChange(hero1);
        });
    }

    @Override
    public void up_EF_SimpleLv(int heroId, EquipmentPositionEnum equipPos, List<IdAndCount> consumeTpIds) {
        exeI(heroId, equipPos, (hero, equipment, equipTpId, curLv, curQualityLv, curColor, curMaxLvInCurQuality, curStarLv) -> {
            IdMaptoCount expItemsConsumes = new IdMaptoCount(consumeTpIds);
            ItemSmallTypeEnum smallType = null;
            if (equipPos == EquipmentPositionEnum.POS_E) {
                smallType = ItemSmallTypeEnum.IST_EXP_BADGE;
            } else if (equipPos == EquipmentPositionEnum.POS_F) {
                smallType = ItemSmallTypeEnum.IST_EXP_CHEATS;
            }
            logicCheck_IsAllConsumeDesignationType(expItemsConsumes, smallType);
            LogicCheckUtils.canRemove(itemIoCtrl, expItemsConsumes);
            if (curLv >= curMaxLvInCurQuality) {
                String msg = String.format("装备equipPos=%s, 需要升品后才能继续升级！curLv=%s curQualityLv=%s curMaxLv=%s", equipPos, curLv, curQualityLv, curMaxLvInCurQuality);
                throw new BusinessLogicMismatchConditionException(msg);
            }
            long allExpOffered = EquipmentCtrlUtils.calcuSumExpOfItems(expItemsConsumes, smallType);
            // 根据经验值消耗金币
            IdAndCount moneyConsumes = new IdAndCount(ResourceTypeEnum.RES_MONEY_VALUE, (int) AllServerConfig.Equipment_EF_Lv_Up_Consume_Ratio.getConfig() * allExpOffered);
            LogicCheckUtils.canRemove(itemIoCtrl, new IdMaptoCount().add(moneyConsumes));
            EquipmentCtrlUtils.upExpLv_EF(allExpOffered, curMaxLvInCurQuality, equipment);
            IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_UP_EF_SIMPLE_LV).removeItem(moneyConsumes);
            IdMaptoCount refresh_2 = itemIoExtp.getControlerForUpdate(Action.RESP_UP_EF_SIMPLE_LV).removeItem(expItemsConsumes);
            itemIoExtp.getControlerForUpdate(Action.RESP_UP_EF_FAST_LV).removeItem(moneyConsumes);
            SenderFunc.sendInner(this, Sm_Equipment.class, Sm_Equipment.Builder.class, Action.RESP_UP_EF_SIMPLE_LV, (b, br) -> {
                IdMaptoCount toAddRefresh = new IdMaptoCount().addAll(refresh_1).addAll(refresh_2).add(new IdAndCount(hero.getId()));
                itemIoCtrl.refreshItemAddToResponse(toAddRefresh, br);
            });
            herosCtrl.getAttrsContainer().onOneHeroEquipChange(hero);
        });
    }


    @Override
    public void up_EF_FastLv(int heroId, EquipmentPositionEnum equipPos, int fastToLv) {
        exeI(heroId, equipPos, (hero1, equipment1, equipTpId1, curLv1, curQualityLv1, curColor1, curMaxLvInCurQuality1, curStarLv1) -> {
            if (curLv1 >= curMaxLvInCurQuality1) {
                String msg = String.format("装备需要升品后才能继续升级！curLv=%s curQualityLv=%s curMaxLv=%s", curLv1, curQualityLv1, curMaxLvInCurQuality1);
                throw new BusinessLogicMismatchConditionException(msg);
            }
            logicCheck_EF_FastToLv_Args(fastToLv);
            ColorDetailTypeEnum toColor = EquipmentCtrlUtils.colorOfLv_EF(fastToLv);
            ColorDetailTypeEnum finalColor = EquipmentCtrlUtils.preColor(toColor);
            if (EquipmentCtrlUtils.indexOfColor(finalColor) < EquipmentCtrlUtils.indexOfColor(curColor1)) {
                String msg = String.format("fastToLv=%s非法! finalColor=%s，curColor=%s！", fastToLv, finalColor, curColor1);
                throw new BusinessLogicMismatchConditionException(msg);
            }
            IdMaptoCount refresh = new IdMaptoCount();
            for (int i = 0; i < 100; i++) {
                final int idx = i;
                boolean[] isRe = new boolean[]{true, true};
                // 先尝试升级到当前品级的最大等级
                exeI(heroId, equipPos, (hero, equipment, equipTpId, curLv, curQualityLv, curColor, curMaxLvInCurQuality, curStarLv) -> {
                    if (curLv >= fastToLv) {
                        return;
                    }
                    if (curLv >= curMaxLvInCurQuality) {
                        return;
                    }
                    ItemSmallTypeEnum smallType = null;
                    if (equipPos == EquipmentPositionEnum.POS_E) {
                        smallType = ItemSmallTypeEnum.IST_EXP_BADGE;
                    } else if (equipPos == EquipmentPositionEnum.POS_F) {
                        smallType = ItemSmallTypeEnum.IST_EXP_CHEATS;
                    }
                    IdMaptoCount refresh_1 = new IdMaptoCount();
                    UpgradeResult_EF upgradeResult = _upLv_EF(refresh_1, smallType, hero, equipment, equipPos, curLv, curMaxLvInCurQuality, Action.RESP_UP_EF_FAST_LV);
                    LOGGER.debug("{}:EF->快速升级结果：\nupgradeResult={}", idx, upgradeResult);
                    if (upgradeResult.isSuccess()) {
                        isRe[0] = false;
                    }
                });
                if (isRe[0]) {
                    break;
                }
                // 尝试升品，如果试炼币或者荣誉币不足自动购买
                exeI(heroId, equipPos, (hero, equipment, equipTpId, curLv, curQualityLv, curColor, curMaxLvInCurQuality, curStarLv) -> {
                    if (EquipmentCtrlUtils.indexOfColor(finalColor) <= EquipmentCtrlUtils.indexOfColor(curColor)) {
                        return;
                    }
                    if (curLv >= curMaxLvInCurQuality) { // 去升品
                        ColorDetailTypeEnum nextColor = EquipmentCtrlUtils.nextColor(curColor);
                        int maxQualityLv = EquipmentCtrlUtils.maxColor().getNumber();
                        if (curQualityLv >= maxQualityLv) {
                            return;
                        }
                        IdMaptoCount consumes = EquipmentCtrlUtils.getUpOneQualityLvConsumes(equipPos, curColor);
                        LOGGER.debug("{}:EF->转换前：curColor={},自动升品消耗consumes={}", idx, consumes, curColor);
                        convertShortageToVipMoney(consumes, equipPos);
                        LOGGER.debug("{}:EF->转换后：curColor={},自动升品消耗consumes={}", idx, consumes, curColor);
                        if (itemIoCtrl.canRemove(consumes)) {
                            IdMaptoCount refresh_2 = itemIoExtp.getControlerForUpdate(Action.RESP_UP_EF_FAST_LV).removeItem(new IdMaptoCount().addAll(consumes));
                            refresh.addAll(refresh_2);
                            equipment.setQualityLv(nextColor.getNumber());
                            isRe[1] = false;
                        }
                    }
                });
                if (isRe[1]) {
                    break;
                }
            }
            SenderFunc.sendInner(this, Sm_Equipment.class, Sm_Equipment.Builder.class, Action.RESP_UP_EF_FAST_LV, (b, br) -> {
                itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh).add(new IdAndCount(hero1.getId())), br);
            });
            herosCtrl.getAttrsContainer().onOneHeroEquipChange(hero1);
        });
    }

    @Override
    public void upgradeQuality(int heroId, EquipmentPositionEnum equipPos) {
        exeI(heroId, equipPos, (hero, equipment, equipTpId, curLv, curQualityLv, curColor, curMaxLvInCurQuality, curStarLv) -> {
            ColorDetailTypeEnum nextColor = EquipmentCtrlUtils.nextColor(curColor);
            int maxQualityLv = EquipmentCtrlUtils.maxColor().getNumber();
            if (curQualityLv >= maxQualityLv) {
                String msg = String.format("装备已经达到最大品级=%s, 无法继续升品！", curQualityLv);
                throw new BusinessLogicMismatchConditionException(msg);
            }
            if (curLv < curMaxLvInCurQuality) {
                String msg = String.format("升品需求装备等级不符合！当前等级=%s, 需要等级=%s, 当前品质=%s", curLv, curMaxLvInCurQuality, curQualityLv);
                throw new BusinessLogicMismatchConditionException(msg);
            }
            int needPlayerLv = EquipmentCtrlUtils.upQualityNeedPlayerLv(equipPos, curColor);
            if (getPlayerCtrl().getCurLevel() < needPlayerLv) {
                String msg = String.format("升品需求战队等级不符合！当前等级=%s, 需要等级=%s, 当前品质=%s", getPlayerCtrl().getCurLevel(), needPlayerLv, curQualityLv);
                throw new BusinessLogicMismatchConditionException(msg);
            }
            IdMaptoCount consumes = EquipmentCtrlUtils.getUpOneQualityLvConsumes(equipPos, curColor);
            convertShortageToVipMoney(consumes, equipPos);
            LogicCheckUtils.canRemove(itemIoCtrl, consumes);
            IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_UP_QUALITY).removeItem(new IdMaptoCount().addAll(consumes));
            equipment.setQualityLv(nextColor.getNumber());
            int nextMaxLvInCurQuality = EquipmentCtrlUtils.curColorMaxLv(equipPos, nextColor);
            if (equipPos == EquipmentPositionEnum.POS_E || equipPos == EquipmentPositionEnum.POS_F) {
                EquipmentCtrlUtils.upExpLv_EF(0, nextMaxLvInCurQuality, equipment); // 尝试继续升级
            }
            SenderFunc.sendInner(this, Sm_Equipment.class, Sm_Equipment.Builder.class, Action.RESP_UP_QUALITY, (b, br) -> {
                IdMaptoCount toAddRefresh = new IdMaptoCount().addAll(refresh_1).add(new IdAndCount(hero.getId()));
                itemIoCtrl.refreshItemAddToResponse(toAddRefresh, br);
            });
            herosCtrl.getAttrsContainer().onOneHeroEquipChange(hero);
        });
    }

    @Override
    public void upgradeStar(int heroId, EquipmentPositionEnum equipPos) {
        exeI(heroId, equipPos, (hero, equipment, equipTpId, curLv, curQualityLv, curColor, curMaxLvInCurQuality, curStarLv) -> {
            int nextStarLv = curStarLv + 1;
            int maxQualityLv = AllServerConfig.Equipment_Max_Star_Lv.getConfig();
            if (curStarLv >= maxQualityLv) {
                String msg = String.format("装备已经达到最大星级=%s, 无法继续升星！", curStarLv);
                throw new BusinessLogicMismatchConditionException(msg);
            }
            IdMaptoCount consumes = EquipmentCtrlUtils.getUpStarLvConsumes(equipPos, equipTpId, curStarLv);
            LogicCheckUtils.canRemove(itemIoCtrl, consumes);
            IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_UP_STAR).removeItem(new IdMaptoCount().addAll(consumes));
            equipment.setStarLv(nextStarLv);
            SenderFunc.sendInner(this, Sm_Equipment.class, Sm_Equipment.Builder.class, Action.RESP_UP_STAR, (b, br) -> {
                IdMaptoCount toAddRefresh = new IdMaptoCount().addAll(refresh_1).add(new IdAndCount(hero.getId()));
                itemIoCtrl.refreshItemAddToResponse(toAddRefresh, br);
            });
            herosCtrl.getAttrsContainer().onOneHeroEquipChange(hero);
        });
    }

    @Override
    public void reduceStar(int heroId, EquipmentPositionEnum equipPos) {
        exeI(heroId, equipPos, (hero, equipment, equipTpId, curLv, curQualityLv, curColor, curMaxLvInCurQuality, curStarLv) -> {
            if (curStarLv <= MagicNumbers.DefaultValue_Equipment_Star_Level) {
                String msg = String.format("装备星级=%s, 已经是最低星级！", curStarLv);
                throw new BusinessLogicMismatchConditionException(msg);
            }
            int needVipMoney = AllServerConfig.Equipment_Reduce_Star_Lv_VipMoney.getConfig();
            IdAndCount moneyConsume = new IdAndCount(ResourceTypeEnum.RES_VIPMONEY_VALUE, needVipMoney);
            LogicCheckUtils.canRemove(itemIoCtrl, moneyConsume);
            int preStarLv = curStarLv - 1;
            IdMaptoCount materialReturn = EquipmentCtrlUtils.getUpStarLvConsumes(equipPos, equipTpId, preStarLv);
            IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_REDUCE_STAR).removeItem(moneyConsume);
            // materialReturn通过邮件返回
            mailsCtrl.addSysMail(MagicNumbers.SYSTEM_MAIL_ID_EQUIPMENT_REDUCESTAR, materialReturn);
            equipment.setStarLv(preStarLv);
            SenderFunc.sendInner(this, Sm_Equipment.class, Sm_Equipment.Builder.class, Action.RESP_REDUCE_STAR, (b, br) -> {
                IdMaptoCount toAddRefresh = new IdMaptoCount().addAll(refresh_1).add(new IdAndCount(hero.getId()));
                itemIoCtrl.refreshItemAddToResponse(toAddRefresh, br);
            });
            herosCtrl.getAttrsContainer().onOneHeroEquipChange(hero);
        });
    }


    /**
     * [try升1级]或者[try升到当前品级的最大等级]
     *
     * @param equipment
     * @param curLv
     * @return
     */
    private UpgradeResult_ABCD _upLv_ABCD(IdMaptoCount refresh, Hero hero, Equipment equipment, EquipmentPositionEnum position,//
                                          int curLv, int curMaxLvInCurQuality, boolean oneKey, Sm_Equipment.Action action) {
        long allHasMoney = resourcePointCtrl.getResourcePoint(ResourceTypeEnum.RES_MONEY);
        int limitLv = curLv + 1;
        if (oneKey) {
            limitLv = curMaxLvInCurQuality;
        }
        UpgradeResult_ABCD upgradeResult = EquipmentCtrlUtils.tryUpLvToLimitLv_ABCD(hero.getTpId(), position, limitLv, curLv, allHasMoney);
        if (upgradeResult.getTimes() > 0) {
            IdMaptoCount cost = new IdMaptoCount(new IdAndCount(ResourceTypeEnum.RES_MONEY_VALUE, upgradeResult.getAllNeedMoney()));
            IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(action).removeItem(cost);
            refresh.addAll(refresh_1);
            //等级判断
            equipment.setLv(upgradeResult.getNewLv());
        }
        return upgradeResult;
    }

    /**
     * [try升到当前品级的最大等级]
     *
     * @param equipment
     * @param curLv
     * @return
     */
    private UpgradeResult_EF _upLv_EF(IdMaptoCount refresh, ItemSmallTypeEnum smallType, Hero hero, Equipment equipment, EquipmentPositionEnum position,//
                                      int curLv, int curMaxLvInCurQuality, Sm_Equipment.Action action) {
        long allHasExp = EquipmentCtrlUtils.sumExpOfAllExpItem(itemBagCtrl.getTarget(), smallType);
        long curOvf = equipment.getOvfExp();
        long allHasMoney = resourcePointCtrl.getResourcePoint(ResourceTypeEnum.RES_MONEY);
        UpgradeResult_EF upgradeResult = EquipmentCtrlUtils.tryUpLvToLimitLv_EF(curMaxLvInCurQuality, curLv, allHasExp, curOvf);
        upgradeResult.setSuccess(false);
        upgradeResult.setAllHasMoney(allHasMoney);
        if (upgradeResult.getTimes() > 0) {
            IdMaptoCount expItemsConsumes = EquipmentCtrlUtils.convertExpToExpItem(upgradeResult.getAllNeedExp(), itemBagCtrl.getTarget(), smallType);
            long allExpOffered = EquipmentCtrlUtils.calcuSumExpOfItems(expItemsConsumes, smallType);
            IdAndCount moneyConsumes = new IdAndCount(ResourceTypeEnum.RES_MONEY_VALUE, (int) AllServerConfig.Equipment_EF_Lv_Up_Consume_Ratio.getConfig() * allExpOffered);
            upgradeResult.setNewNeedMoney(moneyConsumes.getCount());
            upgradeResult.setAllNewNeedExp(allExpOffered);
            if (itemIoCtrl.canRemove(moneyConsumes)) {
                IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(action).removeItem(moneyConsumes);
                IdMaptoCount refresh_2 = itemIoExtp.getControlerForUpdate(action).removeItem(expItemsConsumes);
                refresh.addAll(refresh_1);
                refresh.addAll(refresh_2);
                EquipmentCtrlUtils.upExpLv_EF(allExpOffered, curMaxLvInCurQuality, equipment);
                upgradeResult.setSuccess(true);
            }
        }
        return upgradeResult;
    }


    /**
     * 检查fastToLv是否在固定的【角色等级】需求里
     *
     * @param fastToLv
     */
    private void logicCheck_ABCD_FastToLv_Args(int fastToLv) {
        ListCell<Integer> listCell = AllServerConfig.Equipment_ABCD_UpQuality_Need_Lv.getConfig();
        logicCheck_FastToLv_Args(fastToLv, listCell);
    }

    /**
     * 检查fastToLv是否在固定的【角色等级】需求里
     *
     * @param fastToLv
     */
    private void logicCheck_EF_FastToLv_Args(int fastToLv) {
        ListCell<Integer> listCell = AllServerConfig.Equipment_EF_UpQuality_Need_Lv.getConfig();
        logicCheck_FastToLv_Args(fastToLv, listCell);
    }


    private void logicCheck_FastToLv_Args(int fastToLv, ListCell<Integer> listCell) {
        if (!listCell.getAll().contains(fastToLv)) {
            String msg = String.format("目标等级FastToLv=%s, 不正确！正确的为=%s", fastToLv, listCell.getAll());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (getPlayerCtrl().getCurLevel() < fastToLv) {
            String msg = String.format("目标等级FastToLv=%s, 不能超过角色等级！", fastToLv, getPlayerCtrl().getCurLevel());
            throw new BusinessLogicMismatchConditionException(msg);
        }
    }

    /**
     * 是否所有的消耗都是指定的类型
     *
     * @param consumes
     * @return
     */
    public static void logicCheck_IsAllConsumeDesignationType(IdMaptoCount consumes, ItemSmallTypeEnum type) {
        for (IdAndCount idAndCount : consumes.getAll()) {
            int id = idAndCount.getId();
            IdItemTypeEnum curType = IdItemTypeEnum.parseByItemTemplateId(id);
            if (curType != IdItemTypeEnum.ITEM) {
                String msg = String.format("消耗品consumes=%s, id=%s, type=%s, 不是item类型 ！", consumes, id, curType);
                throw new BusinessLogicMismatchConditionException(msg);
            }
            Table_Item_Row itemRow = RootTc.get(Table_Item_Row.class, id);
            if (itemRow.getItemType() != type) {
                String msg = String.format("消耗品consumes=%s, id=%s, type=%s 不都是=%s！", consumes, id, itemRow.getItemType(), type);
                throw new BusinessLogicMismatchConditionException(msg);
            }
        }
    }


    /**
     * <pre>
     * 把不足的装备宝石部分转化为vipmoney
     * 不足的试炼币、荣誉币转化为vipmoney
     * 例如：需要15个，现有10个，则将需要的替换为10个，另外增加5*宝石单价个vipmoney
     * </pre>
     *
     * @param idMaptoCount
     * @param equipPos
     */
    private void convertShortageToVipMoney(IdMaptoCount idMaptoCount, EquipmentPositionEnum equipPos) {
        int sumVipMoney = 0;
        if (equipPos == EquipmentPositionEnum.POS_A || equipPos == EquipmentPositionEnum.POS_B
                || equipPos == EquipmentPositionEnum.POS_C || equipPos == EquipmentPositionEnum.POS_D) {
            for (IdAndCount idAndCount : idMaptoCount.getAll()) {
                int tpId = idAndCount.getId();
                long needCount = idAndCount.getCount();
                IdItemTypeEnum itemTypeEnum = IdItemTypeEnum.parseByItemTemplateId(idAndCount.getId());
                if (itemTypeEnum == IdItemTypeEnum.ITEM) {
                    Table_Item_Row itemRow = RootTc.get(Table_Item_Row.class, tpId);
                    if (itemRow.getItemType() == ItemSmallTypeEnum.IST_EQUIP_GEM) {
                        long has = ItemBagUtils.templateItemCount(itemBagCtrl.getTarget(), tpId);
                        if (has < needCount) {
                            sumVipMoney += itemRow.getItemJiamond() * (needCount - has);
                            idMaptoCount.remove(tpId);
                            idMaptoCount.add(new IdAndCount(tpId, has));
                        }
                    }
                }
            }
        }
        if (equipPos == EquipmentPositionEnum.POS_E) {
            TupleCell<Integer> tupleCell = AllServerConfig.Equipment_Buy_RES_ARENA_MONEY.getConfig();
            sumVipMoney += _calcuNeedVipMoney(idMaptoCount, tupleCell, ResourceTypeEnum.RES_ARENA_MONEY);
        } else if (equipPos == EquipmentPositionEnum.POS_F) {
            TupleCell<Integer> tupleCell = AllServerConfig.Equipment_Buy_RES_TEST_MONEY.getConfig();
            sumVipMoney += _calcuNeedVipMoney(idMaptoCount, tupleCell, ResourceTypeEnum.RES_TEST_MONEY);
        }
        if (sumVipMoney > 0) {
            idMaptoCount.add(new IdAndCount(ResourceTypeEnum.RES_VIPMONEY_VALUE, sumVipMoney));
        }
    }

    /**
     * 根据所有量和需求量，计算购买需要的钻石
     *
     * @param idMaptoCount
     * @param tupleCell
     * @param resourceType
     * @return
     */
    private long _calcuNeedVipMoney(IdMaptoCount idMaptoCount, TupleCell<Integer> tupleCell, ResourceTypeEnum resourceType) {
        long need = idMaptoCount.getCount(resourceType.getNumber());
        long has = resourcePointCtrl.getResourcePoint(resourceType);
        if (has < need) {
            idMaptoCount.remove(resourceType.getNumber());
            idMaptoCount.add(new IdAndCount(resourceType.getNumber(), has));
            return (need - has) * tupleCell.get(TupleCell.SECOND) / tupleCell.get(TupleCell.FIRST);
        }
        return 0;
    }


    void exeI(int heroId, EquipmentPositionEnum equipPos, ExeO exeO) {
        LogicCheckUtils.requireNonNull(equipPos, EquipmentPositionEnum.class);
        LogicCheckUtils.canRemove(itemIoCtrl, new IdAndCount(heroId));
        Hero hero = herosCtrl.getHero(heroId);
        if (hero == null) {
            String msg = String.format("参数heroId=%s非法！未查询到该武将!", heroId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        Equipment equipment = hero.getEquips().get(equipPos);
        int curLv = equipment.getLv();
        int curQualityLv = equipment.getQualityLv();
        ColorDetailTypeEnum curColor = ColorDetailTypeEnum.valueOf(curQualityLv);
        int curMaxLvInCurQuality = EquipmentCtrlUtils.curColorMaxLv(equipPos, curColor);
        int curStarLv = equipment.getStarLv();
        int equipTpId = RootTc.get(Table_New_Card_Row.class, hero.getTpId()).getEquipmentTpId(equipPos);
        exeO.exe(hero, equipment, equipTpId, curLv, curQualityLv, curColor, curMaxLvInCurQuality, curStarLv);
        herosCtrl.save();
        itemBagCtrl.save();
    }

    private interface ExeO {
        void exe(Hero hero, Equipment equipment, int equipTpId, int curLv, int curQualityLv, ColorDetailTypeEnum curColor, int curMaxLvInCurQuality, int curStarLv);
    }


}