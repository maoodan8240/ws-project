package ws.gameServer.features.standalone.extp.talent.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.heros.HerosExtp;
import ws.gameServer.features.standalone.extp.heros.ctrl.HerosCtrl;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.mails.MailsExtp;
import ws.gameServer.features.standalone.extp.mails.ctrl.MailsCtrl;
import ws.gameServer.features.standalone.extp.talent.utils.TalentCtrlUtils;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.protos.TalentProtos.Sm_Talent;
import ws.protos.TalentProtos.Sm_Talent.Action;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.tableRows.Table_Talent_Row;
import ws.relationship.topLevelPojos.talent.Talent;

import java.util.Iterator;
import java.util.List;

public class _TalentCtrl extends AbstractPlayerExteControler<Talent> implements TalentCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_TalentCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;
    private MailsCtrl mailsCtrl;
    private HerosCtrl herosCtrl;

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
        mailsCtrl = getPlayerCtrl().getExtension(MailsExtp.class).getControlerForQuery();
        herosCtrl = getPlayerCtrl().getExtension(HerosExtp.class).getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {

    }

    @Override
    public void sync() {
        SenderFunc.sendInner(this, Sm_Talent.class, Sm_Talent.Builder.class, Sm_Talent.Action.RESP_SYNC, (b, br) -> {
            b.addAllTalentIds(target.getTalentLevelIds());
        });
    }


    @Override
    public void upLevel(int talentLevelId) {
        LogicCheckUtils.validateParam(Integer.class, talentLevelId);
        Action action = Sm_Talent.Action.RESP_UP_LEVEL;
        if (!_isOpenLevel()) {
            String msg = String.format("等级不足, Lv=%s,需要等级%s", getPlayerCtrl().getCurLevel(), AllServerConfig.Talen_OpenLevel.getConfig());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (_containsThisLevelId(talentLevelId)) {
            String msg = String.format("当前等级已经是%s,不需要升级", talentLevelId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!_isPlayerLevelCanUpLeve(talentLevelId)) {
            String msg = String.format("等级不足, Lv=%s,需要等级%s", getPlayerCtrl().getCurLevel(), TalentCtrlUtils.needLevel(talentLevelId));
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!_hasBeforeTalent(talentLevelId)) {
            String msg = String.format("缺少前置天赋 升级天赋ID=%s,缺少天赋Id=%s", talentLevelId, TalentCtrlUtils.needBeforeTalent(talentLevelId));
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdMaptoCount needResource = TalentCtrlUtils.getNeedResource(talentLevelId);
        LogicCheckUtils.canRemove(this.itemIoCtrl, needResource);
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(action).removeItem(needResource);
        _upLevel(talentLevelId);
        SenderFunc.sendInner(this, Sm_Talent.class, Sm_Talent.Builder.class, action, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
        });
        herosCtrl.getAttrsContainer().onTalentChange();
        save();
    }


    @Override
    public void reset() {
        if (!_isCanReset()) {
            String msg = "天赋不需要重置";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        int needVipMoney = AllServerConfig.Talent_Rest_Vip_Money.getConfig();
        IdMaptoCount consume = new IdMaptoCount().add(new IdAndCount(ResourceTypeEnum.RES_VIPMONEY_VALUE, needVipMoney));
        LogicCheckUtils.canRemove(this.itemIoCtrl, consume);
        IdMaptoCount addResource = new IdMaptoCount();
        int potion = _resetCanGetTalentPoint();
        int money = _resetCanGetMoney();
        addResource.add(new IdAndCount(ResourceTypeEnum.RES_MONEY_VALUE, money));
        addResource.add(new IdAndCount(ResourceTypeEnum.RES_TALENT_VALUE, potion));
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Sm_Talent.Action.RESP_RESET).removeItem(consume);
        target.getTalentLevelIds().clear();
        mailsCtrl.addSysMail(MagicNumbers.SYSTEM_MAIL_ID_2, addResource);
        SenderFunc.sendInner(this, Sm_Talent.class, Sm_Talent.Builder.class, Sm_Talent.Action.RESP_RESET, (b, br) -> {
            b.addAllTalentIds(target.getTalentLevelIds());
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1), br);
        });
        herosCtrl.getAttrsContainer().onTalentChange();
        save();
    }


    private boolean _containsThisLevelId(int talentLevelId) {
        return target.getTalentLevelIds().contains(talentLevelId);

    }


    private int _resetCanGetTalentPoint() {
        int point = 0;
        for (Integer leveId : target.getTalentLevelIds()) {
            int treecount = _getResetPointByIdAndLevel(leveId);
            point += treecount;
        }
        return point;
    }


    private int _getResetPointByIdAndLevel(int level) {
        List<Table_Talent_Row> rowList = Table_Talent_Row.getTalentTree(level);
        int point = 0;
        for (Table_Talent_Row row : rowList) {
            if (row.getId() <= level) {
                point += row.getTalentPointUse();
            }
        }
        return point;
    }


    private int _resetCanGetMoney() {
        int money = 0;
        for (Integer leveId : target.getTalentLevelIds()) {
            money += _getResetMoneyByIdAndLevel(leveId);
        }
        return (int) (money * 0.5);
    }

    private int _getResetMoneyByIdAndLevel(int level) {
        List<Table_Talent_Row> rowList = Table_Talent_Row.getTalentTree(level);
        int money = 0;
        for (Table_Talent_Row row : rowList) {
            if (row.getId() <= level) {
                money += row.getGoldUse();
            }
        }
        return money;
    }

    private boolean _isCanReset() {
        for (Integer leveId : target.getTalentLevelIds()) {
            if (leveId != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 升级天赋
     *
     * @param talentLevelId
     */
    private void _upLevel(int talentLevelId) {
        int beforeTalentLevelId = TalentCtrlUtils.needBeforeTalent(talentLevelId);
        int talentId = TalentCtrlUtils.getTalentId(talentLevelId);
        Iterator<Integer> it = target.getTalentLevelIds().iterator();
        //如果前置天赋是0，说明这个技能是技能树的头部，什么也不用校验
        if (beforeTalentLevelId != 0) {
            int beforeLv = TalentCtrlUtils.getTalentRow(beforeTalentLevelId).getTalentLv();
            int befotalentId = TalentCtrlUtils.getTalentId(beforeTalentLevelId);
            while (it.hasNext()) {
                Integer oldTalentLevelId = it.next();
                // 升级过的天赋等级中有这次升级的等级的前置天赋并且天赋树和现在的天赋树相同就删除,说明这两个技能在一棵技能数，删除前置的技能ID
                int lv = TalentCtrlUtils.getTalentRow(oldTalentLevelId).getTalentLv();
                if (oldTalentLevelId == beforeTalentLevelId && befotalentId == talentId && lv >= beforeLv) {
                    it.remove();
                }
            }
        }
        // 添加新的天赋等级
        target.getTalentLevelIds().add(talentLevelId);
    }


    /**
     * 是否满足前置天赋
     *
     * @param talentLevelId
     * @return
     */
    private boolean _hasBeforeTalent(int talentLevelId) {
        int beforeTalentLevelId = TalentCtrlUtils.needBeforeTalent(talentLevelId);
        //前置ID是技能树的头部，不用校验
        if (beforeTalentLevelId == MagicNumbers.DEFAULT_ZERO) {
            return true;
        }
        //前置等级所属技能树
        int beforeTalentId = TalentCtrlUtils.getTalentId(beforeTalentLevelId);
        //前置天赋需要等级
        int beforeLv = TalentCtrlUtils.getTalentRow(beforeTalentLevelId).getTalentLv();
        for (Integer id : target.getTalentLevelIds()) {
            int talentId = TalentCtrlUtils.getTalentId(id);
            int lv = TalentCtrlUtils.getTalentRow(id).getTalentLv();
            // 和前置天赋一棵树的技能，并且等级比前置天赋高，判定满足
            if (talentId == beforeTalentId && lv >= beforeLv) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查看玩家等级是否满足
     *
     * @param talentLevelId
     * @return
     */
    private boolean _isPlayerLevelCanUpLeve(int talentLevelId) {
        int needLevel = TalentCtrlUtils.needLevel(talentLevelId);
        return getPlayerCtrl().getCurLevel() >= needLevel;
    }

    private boolean _isOpenLevel() {
        int openLv = AllServerConfig.Talen_OpenLevel.getConfig();
        return getPlayerCtrl().getCurLevel() >= openLv;
    }
}
