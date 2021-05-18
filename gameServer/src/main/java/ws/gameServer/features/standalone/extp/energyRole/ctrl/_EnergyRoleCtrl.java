package ws.gameServer.features.standalone.extp.energyRole.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.dataCenter.enums.PrivateNotifyTypeEnum;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_NotifyMsg;
import ws.gameServer.features.standalone.extp.energyRole.utils.EnergyRoleCtrlUtils;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.resourcePoint.ResourcePointExtp;
import ws.gameServer.features.standalone.extp.resourcePoint.ctrl.ResourcePointCtrl;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.EnergyRoleProtos.Sm_EnergyRole;
import ws.protos.EnergyRoleProtos.Sm_EnergyRole.Action;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.tableRows.Table_Consume_Row;
import ws.relationship.table.tableRows.Table_Exp_Row;
import ws.relationship.table.tableRows.Table_Vip_Row;
import ws.relationship.topLevelPojos.energyRole.EnergyRole;

public class _EnergyRoleCtrl extends AbstractPlayerExteControler<EnergyRole> implements EnergyRoleCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_EnergyRoleCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;
    private ResourcePointCtrl resourcePointCtrl;

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
        resourcePointCtrl = getPlayerCtrl().getExtension(ResourcePointExtp.class).getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
        target.setHasBuyEnergyTs(0);
    }

    @Override
    public void sync() {
        IdMaptoCount refresh_1 = settleAutoIncreaseInner();
        SenderFunc.sendInner(this, Sm_EnergyRole.class, Sm_EnergyRole.Builder.class, Sm_EnergyRole.Action.RESP_SYNC, (b, br) -> {
            b.setTodayHasBuyEnergyTimes(target.getHasBuyEnergyTs());
            b.setLastUpEnergyTime(target.getLastUpEneryTime());
            if (refresh_1 != null) {
                itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1), br);
            }
        });
    }

    @Override
    public void buyEnergy() {
        int curVipLv = getPlayerCtrl().getTarget().getPayment().getVipLevel();
        int maxCanBuyTimes = Table_Vip_Row.getVipRow(curVipLv).getSpiritShop();
        if (target.getHasBuyEnergyTs() >= maxCanBuyTimes) {
            String msg = String.format("curVipLv=%s hasBuyEnergyTs=%s maxCanBuyTimes=%s！不能再购买了.", curVipLv, target.getHasBuyEnergyTs(), maxCanBuyTimes);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        int get = AllServerConfig.Energy_OneTimesBuyCanGet.getConfig();
        int nextBuyEnergyTs = target.getHasBuyEnergyTs() + 1;
        IdAndCount reduce = Table_Consume_Row.energyBuyConsume(nextBuyEnergyTs);
        LogicCheckUtils.canRemove(itemIoCtrl, reduce);
        IdAndCount add = new IdAndCount(ResourceTypeEnum.RES_ENERGY_VALUE, get);
        target.setHasBuyEnergyTs(nextBuyEnergyTs);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_BUY_ENERGY).removeItem(reduce);
        IdMaptoCount refresh_2 = itemIoExtp.getControlerForUpdate(Action.RESP_BUY_ENERGY).addItem(add);
        settleAutoIncreaseInner(); // 顺便结算体力
        SenderFunc.sendInner(this, Sm_EnergyRole.class, Sm_EnergyRole.Builder.class, Action.RESP_BUY_ENERGY, (b, br) -> {
            b.setTodayHasBuyEnergyTimes(nextBuyEnergyTs);
            b.setLastUpEnergyTime(target.getLastUpEneryTime());
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1).addAll(refresh_2), br);
        });
        save();
        statisticsDaliyBuyEnergyTimes();
    }

    @Override
    public void onEnergyValueChanged(long before, long now) {
        int energyUpperLimit = Table_Exp_Row.getPlayerEnergyUpperLimit(getPlayerCtrl().getCurLevel());
        long curEnergy = resourcePointCtrl.getResourcePoint(ResourceTypeEnum.RES_ENERGY);
        if (before >= energyUpperLimit && curEnergy < energyUpperLimit) {
            // 之前>=，现在<，开始増涨
            LOGGER.debug("energy before={} curEnergy={} 之前>=，现在<，开始増涨.", before, curEnergy);
            settleAutoIncrease();
        } else if (before < energyUpperLimit && curEnergy >= energyUpperLimit) {
            // 之前<，现在>=，停止増涨
            LOGGER.debug("energy before={} curEnergy={} 之前<，现在>=，停止増涨.", before, curEnergy);
            settleAutoIncrease();
        }
    }

    @Override
    public void settleAutoIncrease() {
        sendSettleEnergy(settleAutoIncreaseInner());
    }


    @Override
    public void setMaxEnergyValueWhenPlayerLvUp() {
        // 升级体力达到最大值
        int energyUpperLimit = Table_Exp_Row.getPlayerEnergyUpperLimit(getPlayerCtrl().getCurLevel());
        long curEnergy = resourcePointCtrl.getResourcePoint(ResourceTypeEnum.RES_ENERGY);
        if (curEnergy >= energyUpperLimit) {
            return;
        }
        long toAddNew = energyUpperLimit - curEnergy;
        IdAndCount add = new IdAndCount(ResourceTypeEnum.RES_ENERGY_VALUE, toAddNew);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_SETTLE_ENERGY).addItem(add);
        sendSettleEnergy(refresh_1);
    }

    public IdMaptoCount settleAutoIncreaseInner() {
        long currentTimeMillis = System.currentTimeMillis();
        if (reachCurPlayerLvEnergyUpperLimit()) {
            target.setLastUpEneryTime(currentTimeMillis);
            save();
            LOGGER.debug("体力达到当前角色等级的最大体力，停止増涨.");
            return null;
        }
        // 体力增涨速度
        long speed = EnergyRoleCtrlUtils.getIncreaseSpeed();
        long timeDValue = currentTimeMillis - target.getLastUpEneryTime();
        if (timeDValue <= 0 || timeDValue < speed) {
            LOGGER.debug("刚结算体力没多久，没到増涨体力时间点.");
            return null;
        }
        long toAdd = timeDValue / speed;
        long remain = timeDValue - toAdd * speed;
        long curEnergy = resourcePointCtrl.getResourcePoint(ResourceTypeEnum.RES_ENERGY);
        int energyUpperLimit = Table_Exp_Row.getPlayerEnergyUpperLimit(getPlayerCtrl().getCurLevel());
        long afterAdd = Math.min((curEnergy + toAdd), energyUpperLimit);
        long toAddNew = afterAdd - curEnergy;
        if (toAddNew < toAdd) {
            remain = 0; // 体力涨满了
        }
        IdAndCount add = new IdAndCount(ResourceTypeEnum.RES_ENERGY_VALUE, toAddNew);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_SETTLE_ENERGY).addItem(add);
        target.setLastUpEneryTime(currentTimeMillis - remain);
        save();
        LOGGER.debug("体力结算了，add={} 增涨被数为={}.", add, toAdd);
        return refresh_1;
    }


    private void sendSettleEnergy(IdMaptoCount refresh) {
        SenderFunc.sendInner(this, Sm_EnergyRole.class, Sm_EnergyRole.Builder.class, Action.RESP_SETTLE_ENERGY, (b, br) -> {
            b.setLastUpEnergyTime(target.getLastUpEneryTime());
            if (refresh != null) {
                itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
            }
        });
    }

    /**
     * 是否达到了当前玩家等级体力的上限
     *
     * @return
     */
    public boolean reachCurPlayerLvEnergyUpperLimit() {
        long curEnergy = resourcePointCtrl.getResourcePoint(ResourceTypeEnum.RES_ENERGY);
        int energyUpperLimit = Table_Exp_Row.getPlayerEnergyUpperLimit(getPlayerCtrl().getCurLevel());
        if (curEnergy >= energyUpperLimit) {
            return true;
        }
        return false;
    }


    // ======================== 数据采集 start start start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private void statisticsDaliyBuyEnergyTimes() {
        Pr_NotifyMsg notifyMsg2 = new Pr_NotifyMsg(PrivateNotifyTypeEnum.Energy_DaliyBuy, target.getHasBuyEnergyTs());
        sendPrivateMsg(notifyMsg2);
    }

    // ======================== 数据采集 end end end <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

}
