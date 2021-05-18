package ws.gameServer.features.standalone.extp.pve2.ctrl;
/**
 * Created by leetony on 16-8-23.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.dataCenter.enums.PrivateNotifyTypeEnum;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_NotifyMsg;
import ws.gameServer.features.standalone.extp.formations.FormationsExtp;
import ws.gameServer.features.standalone.extp.formations.ctrl.FormationsCtrl;
import ws.gameServer.features.standalone.extp.heros.HerosExtp;
import ws.gameServer.features.standalone.extp.heros.ctrl.HerosCtrl;
import ws.gameServer.features.standalone.extp.itemBag.ItemBagExtp;
import ws.gameServer.features.standalone.extp.itemBag.ctrl.ItemBagCtrl;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.pve2.utils.NewPveCtrlProtos;
import ws.gameServer.features.standalone.extp.pve2.utils.NewPveCtrlUtils;
import ws.gameServer.features.standalone.extp.resourcePoint.ResourcePointExtp;
import ws.gameServer.features.standalone.extp.resourcePoint.ctrl.ResourcePointCtrl;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.gameServer.features.standalone.utils.LogHandler;
import ws.gameServer.system.logHandler.LogExcep;
import ws.protos.CommonProtos.Sm_Common_IdMaptoCount;
import ws.protos.CommonProtos.Sm_Common_IdMaptoCountList;
import ws.protos.EnumsProtos.CommonRankTypeEnum;
import ws.protos.EnumsProtos.FormationTypeEnum;
import ws.protos.EnumsProtos.HeroPositionEnum;
import ws.protos.EnumsProtos.MapTypeEnum;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.protos.NewPveProtos.Sm_NewPve;
import ws.protos.NewPveProtos.Sm_NewPve_Chapter;
import ws.protos.NewPveProtos.Sm_NewPve_Stage;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.tableRows.Table_FunctionOpen_Row;
import ws.relationship.table.tableRows.Table_FunctionOpen_Row.FunctionType;
import ws.relationship.table.tableRows.Table_Maps_Row;
import ws.relationship.table.tableRows.Table_Pve_Row;
import ws.relationship.topLevelPojos.formations.Formation;
import ws.relationship.topLevelPojos.formations.FormationPos;
import ws.relationship.topLevelPojos.newPve.Chapter;
import ws.relationship.topLevelPojos.newPve.NewPve;
import ws.relationship.topLevelPojos.newPve.NewStage;
import ws.relationship.utils.ProtoUtils;
import ws.relationship.utils.RedisRankUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


//主线副本和精英副本
public class _NewPveCtrl extends AbstractPlayerExteControler<NewPve> implements NewPveCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_NewPveCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;
    private ItemBagCtrl itemBagCtrl;
    private ResourcePointCtrl resourcePointCtrl;
    private FormationsCtrl formationsCtrl;
    private HerosCtrl herosCtrl;

    private IdMaptoCount idMaptoCount;

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
        itemBagCtrl = getPlayerCtrl().getExtension(ItemBagExtp.class).getControlerForQuery();
        resourcePointCtrl = getPlayerCtrl().getExtension(ResourcePointExtp.class).getControlerForQuery();
        formationsCtrl = getPlayerCtrl().getExtension(FormationsExtp.class).getControlerForQuery();
        herosCtrl = getPlayerCtrl().getExtension(HerosExtp.class).getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
        Map<Integer, Chapter> idToChapters = target.getIdToChapter();
        for (Integer chapterId : idToChapters.keySet()) {
            Chapter chapter = idToChapters.get(chapterId);
            Map<Integer, NewStage> idToStages = chapter.getIdToStage();
            for (Integer stageId : idToStages.keySet()) {
                NewStage stage = idToStages.get(stageId);
                stage.setDailyAttackTimes(MagicNumbers.DEFAULT_ZERO);
                stage.setResetTimes(MagicNumbers.DEFAULT_ZERO);
                stage.setDailySumAttackTimes(MagicNumbers.DEFAULT_ZERO);
            }
        }
    }

    @Override
    public void sync() {
        _send(Sm_NewPve.Action.RESP_SYNC, NewPveCtrlProtos.create_Sm_NewPve_Maps_List(_getAllChapters()));
    }


    @Override
    public void getStarLevelRewards(int chapterId, int boxId) {
        if (!NewPveCtrlUtils.isValidChaterId(chapterId)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "chapterId=%s  未发布不可以挑战！", chapterId);
            throw new BusinessLogicMismatchConditionException(msg);
        }

        Chapter chapter = getChapter(chapterId);
        int sumStar = chapter.getChapterSumStar();
        int rewardsStarLevel = NewPveCtrlUtils.getChestStar(chapter.getChapterId(), boxId);
        if (rewardsStarLevel == MagicNumbers.DEFAULT_ZERO) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "chapterId=%s ,star=%s,这个宝箱不需要星星，宝箱表里啥也没填", chapterId, rewardsStarLevel);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (chapter.getStarToGetTime().containsKey(boxId)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "chapterId=%s的%s星宝箱已经领过奖了", chapterId, rewardsStarLevel);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (rewardsStarLevel > sumStar) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "chapterId=%s领奖的星级为=%s 需要的星星数=%s，当前总星星数=%s ！ ", chapterId, boxId, rewardsStarLevel, sumStar);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        //可以领奖
        IdMaptoCount idMaptoCount = NewPveCtrlUtils.getStarRewardChest(chapter.getChapterId(), boxId);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Sm_NewPve.Action.RESP_GET_REWARDS).addItem(idMaptoCount);
        chapter.getStarToGetTime().put(boxId, System.currentTimeMillis());
        _send(Sm_NewPve.Action.RESP_GET_REWARDS, NewPveCtrlProtos.create_Sm_NewPve_Chapter_nonStage(chapter), refresh_1);
        save();
    }


    @Override
    public void resetStageAttackTimes(int stageId) {
        if (!NewPveCtrlUtils.isValidStageId(stageId)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "Pve 未发布不可以重置 stageId=%s", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        NewStage stage = getNewStage(stageId);
        if (NewPveCtrlUtils.isAttackTimesEnough(stage.getDailyAttackTimes(), stageId)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "PVE stageId=%s 剩余挑战次数不为0,不可以重置！", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!NewPveCtrlUtils.isCanRestAttackTimesByVipLv(stage.getResetTimes(), getPlayerCtrl().getTarget().getPayment().getVipLevel())) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "PVE stageId=%s,hasResetTimes=%s ,重置次数不足！", stageId, stage.getResetTimes());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdAndCount idAndCount = NewPveCtrlUtils.getBuyResetConsume(stage.getResetTimes());
        LogicCheckUtils.canRemove(itemIoCtrl, idAndCount);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Sm_NewPve.Action.RESP_RESET_ATTACK_TIMES).removeItem(idAndCount);
        _saveRestTimes(stage);
        _send(Sm_NewPve.Action.RESP_RESET_ATTACK_TIMES, NewPveCtrlProtos.create_Sm_NewPve_Stage(stage), refresh_1);
        save();
    }

    @Override
    public void beginAttackOnePve(int stageId) {
        if (!NewPveCtrlUtils.isValidStageId(stageId)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s  未发布不可以挑战！", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!NewPveCtrlUtils.isFirst(stageId)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s ,本副本只能挑战一次", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!NewPveCtrlUtils.isPlayerLevelCanAttack(stageId, getPlayerCtrl().getCurLevel())) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s  玩家等级=%s不够=%s，不可以挑战！", stageId, getPlayerCtrl().getCurLevel(),
                    NewPveCtrlUtils.getStageNeedPlayerLevel(stageId));
            throw new BusinessLogicMismatchConditionException(msg);
        }
        NewStage stage = getNewStage(stageId);
        if (!NewPveCtrlUtils.isAttackTimesEnough(stage.getDailyAttackTimes(), stageId)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s  剩余挑战次数不足！", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        LogicCheckUtils.canRemove(itemIoCtrl, new IdMaptoCount().add(_getEndDeductionEnergy(stageId)).add(_getBeginDeductionEnergy(stageId)));
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(Sm_NewPve.Action.RESP_BEGIN_PVE).removeItem(_getBeginDeductionEnergy(stageId));
        long flag = System.currentTimeMillis();
        //掉落
        this.idMaptoCount = new IdMaptoCount();

        //普通掉落
        this.idMaptoCount.addAll(NewPveCtrlUtils.getStageDrop(stageId, getPlayerCtrl().getCurLevel()));
        if (_isFirstPass(stage)) {
            this.idMaptoCount.addAll(NewPveCtrlUtils.getStageFirstDrop(stageId, getPlayerCtrl().getCurLevel()));
        }
        this.idMaptoCount.addAll(NewPveCtrlUtils.getActivityDrop(stageId, getPlayerCtrl().getCurLevel()));
        _saveBeginAttack(stage, flag, this.idMaptoCount);
        List<IdMaptoCount> idMaptoCountList = new ArrayList<IdMaptoCount>();
        idMaptoCountList.add(this.idMaptoCount);
        _send(Sm_NewPve.Action.RESP_BEGIN_PVE, NewPveCtrlProtos.create_Sm_NewPve_Stage(stage), flag, refresh, idMaptoCountList);
        statisticsAttackTimes(stage, MagicNumbers.DEFAULT_ONE);
        save();
    }

    @Override
    public void endAttackOnePve(int stageId, long flag, boolean isWin, int star) {
        if (!NewPveCtrlUtils.isValidStageId(stageId)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "Pve 未发布不可以重置 stageId=%s", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (star > MagicNumbers.PVE_STAGE_MAX_STAR_LEVEL) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "star=%s  非法关卡星数！", star);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!_isLegalBattle(stageId, flag)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "请求结束的战斗是非法的,stageId=%s,last stageId=%s,last flag=%s, now flag=%s", stageId, target.getLastAttackStageId(), target.getLastAttackTime(), flag);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!isWin) {
            _send(Sm_NewPve.Action.RESP_END_PVE);
            return;
        }
        NewStage stage = getNewStage(stageId);
        Chapter chapter = getChapter(_getChapterIdByStageId(stageId));
        LogicCheckUtils.canRemove(itemIoCtrl, _getEndDeductionEnergy(stageId));
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(Sm_NewPve.Action.RESP_END_PVE).removeItem(_getEndDeductionEnergy(stageId));
        this.idMaptoCount.add(NewPveCtrlUtils.getFinishStageMoney(stageId)).add(NewPveCtrlUtils.getFinishStageRoleExp(stageId));
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Sm_NewPve.Action.RESP_END_PVE).addItem(this.idMaptoCount);
        _saveEndAttack(chapter, stage, star);
        SenderFunc.sendInner(this, Sm_NewPve.class, Sm_NewPve.Builder.class, Sm_NewPve.Action.RESP_END_PVE, (b, br) -> {
            b.setStage(NewPveCtrlProtos.create_Sm_NewPve_Stage(stage));
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh).addAll(refresh_1).addAll(getFormationHerosIdMaptoCount()), br);
        });
        save();
        statisticsSumStar();
        statisticsCompleteStage(stageId);
    }

    @Override
    public void mopUp(int stageId, int mopUpTimes) {
        mopUpTimes = Math.max(MagicNumbers.DEFAULT_ONE, mopUpTimes);
        LogicCheckUtils.validateParam(Integer.class, stageId);
        if (!NewPveCtrlUtils.isValidStageId(stageId)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s  未发布不可以挑战！", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!_isFunctionOpen(stageId, mopUpTimes)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "功能没开启,FunctionOpen检验失败,StageId=%s,lv=%s, viplv=%s", stageId, getPlayerCtrl().getCurLevel(), getPlayerCtrl().getCurVipLevel());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!NewPveCtrlUtils.isFirst(stageId)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s ,本副本只能挑战一次", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        Chapter chapter = getChapter(_getChapterIdByStageId(stageId));
        NewStage stage = getNewStage(stageId);
        if (!NewPveCtrlUtils.starCanMop(chapter, stageId)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s 星数不够,无法扫荡", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!NewPveCtrlUtils.isAttackTimesEnoughByTimes(stage.getDailyAttackTimes(), stageId, mopUpTimes)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "剩余挑战次数不足,stageId=%s", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        // n次添加物品 n次消耗,提前校验是否满足添加和消耗,都满足后再保存扫荡信息
        // 先计算当前体力可以扫荡多少次，和传入的扫荡次数比较取最小值
        int canMopupTimes = _mathEnergyCanMopupTimes(stageId);
        if (canMopupTimes <= 0) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s 体力不足以扫荡一次", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        mopUpTimes = Math.min(canMopupTimes, mopUpTimes);
        List<IdMaptoCount> protoIdMapToCount = new ArrayList<>();
        IdMaptoCount addIdMapToCount = _getDropByTimes(protoIdMapToCount, stageId, mopUpTimes);
        IdMaptoCount sweepDropList = new IdMaptoCount();
        IdMaptoCount sweepDropIdMapToCount = _getSweepDrop(sweepDropList, stageId, mopUpTimes);
        IdMaptoCount removeIdMaptoCount = new IdMaptoCount();
        removeIdMaptoCount.addAll(_deductionEnergyByTimes(stageId, mopUpTimes));
        LogicCheckUtils.canRemove(itemIoCtrl, removeIdMaptoCount);
        _mopUpByTimes(chapter, stage, mopUpTimes);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Sm_NewPve.Action.RESP_MOPUP_PVE).addItem(addIdMapToCount);
        IdMaptoCount refresh_2 = itemIoExtp.getControlerForUpdate(Sm_NewPve.Action.RESP_MOPUP_PVE).removeItem(removeIdMaptoCount);
        _send(Sm_NewPve.Action.RESP_MOPUP_PVE, NewPveCtrlProtos.create_Sm_NewPve_Stage(stage), ProtoUtils.create_Sm_Common_IdMaptoCountList(protoIdMapToCount),
                ProtoUtils.create_Sm_Common_IdMaptoCount(sweepDropList), new IdMaptoCount().addAll(refresh_1).addAll(refresh_2).addAll(sweepDropIdMapToCount));
        save();
        statisticsAttackTimes(stage, mopUpTimes);
    }

    private boolean _isFunctionOpen(int stageId, int mopUpTimes) {
        int playerLv = getPlayerCtrl().getCurLevel();
        int playerVipLv = getPlayerCtrl().getCurVipLevel();
        switch (mopUpTimes) {
            case MagicNumbers.PVE_THREE_SWEEP:
                return Table_FunctionOpen_Row.isFunctionOpen(FunctionType.MOPUP_ELITE, playerLv, playerVipLv);
            case MagicNumbers.PVE_TEN_SWEEP:
                return Table_FunctionOpen_Row.isFunctionOpen(FunctionType.MOPUP_STAGE_10, playerLv, playerVipLv);
            case MagicNumbers.PVE_FIFTY_SWEEP:
                return Table_FunctionOpen_Row.isFunctionOpen(FunctionType.MOPUP_50, playerLv, playerVipLv);
            default:
                return true;
        }
    }


    @Override
    public void getBox(int stageId) {
        if (!NewPveCtrlUtils.isValidStageId(stageId)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "Pve 未发布不可以重置 stageId=%s", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        NewStage stage = getNewStage(stageId);
        if (!NewPveCtrlUtils.hasChest(stageId)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s  这关没宝箱！", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (stage.isOpenBox()) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s  宝箱已经领取！", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        idMaptoCount.addAll(NewPveCtrlUtils.getChest(stageId));
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Sm_NewPve.Action.RESP_END_PVE).addItem(idMaptoCount);
        stage.setOpenBox(true);
        _send(Sm_NewPve.Action.RESP_GET_BOX, NewPveCtrlProtos.create_Sm_NewPve_Stage(stage), refresh_1);
        save();
    }


    @Override
    public Chapter getChapter(int chapterId) {
        if (!target.getIdToChapter().containsKey(chapterId)) {
            if (_isLockChapter(chapterId)) {
                if (!_canUnLockChapter(chapterId)) {
                    String msg = String.format("ChapterId=%s,章节未满足解锁条件", chapterId);
                    throw new BusinessLogicMismatchConditionException(msg);
                }
            }
            Chapter chapter = new Chapter();
            chapter.setChapterId(chapterId);
            target.getIdToChapter().put(chapterId, chapter);
        }
        return target.getIdToChapter().get(chapterId);
    }

    @Override
    public NewStage getNewStage(int stageId) {
        int chapterId = _getChapterIdByStageId(stageId);
        Chapter chapter = getChapter(chapterId);
        if (!chapter.getIdToStage().containsKey(stageId)) {
            if (_isLockStage(chapter, stageId)) {
                if (!_canUnLockStage(stageId)) {
                    String msg = String.format("stageId=%s,关卡未满足解锁条件", stageId);
                    throw new BusinessLogicMismatchConditionException(msg);
                }
            }
            NewStage stage = new NewStage();
            stage.setStageId(stageId);
            chapter.getIdToStage().put(stageId, stage);
        }
        return chapter.getIdToStage().get(stageId);
    }

/*
============================================================以下为内部方法==============================================================
 */

    private void _send(Sm_NewPve.Action action) {
        SenderFunc.sendInner(this, Sm_NewPve.class, Sm_NewPve.Builder.class, action, (b, br) -> {
        });
    }


    private void _send(Sm_NewPve.Action action, List<Sm_NewPve_Chapter> sm_newPve_chapter_nonStage_List) {
        SenderFunc.sendInner(this, Sm_NewPve.class, Sm_NewPve.Builder.class, action, (b, br) -> {
            b.addAllChapter(sm_newPve_chapter_nonStage_List);
        });
    }


    private void _send(Sm_NewPve.Action action, Sm_NewPve_Chapter sm_newPve_chapter_nonStage, IdMaptoCount idMaptoCount) {
        SenderFunc.sendInner(this, Sm_NewPve.class, Sm_NewPve.Builder.class, action, (b, br) -> {
            b.addChapter(sm_newPve_chapter_nonStage);
            itemIoCtrl.refreshItemAddToResponse(idMaptoCount, br);
        });
    }


    private void _send(Sm_NewPve.Action action, Sm_NewPve_Stage sm_newPve_stage, long flag, IdMaptoCount idMaptoCount, List<IdMaptoCount> idMaptoCounts2) {
        SenderFunc.sendInner(this, Sm_NewPve.class, Sm_NewPve.Builder.class, action, (b, br) -> {
            b.setStage(sm_newPve_stage);
            b.setFlag(flag);
            b.setIdMapToCountList(ProtoUtils.create_Sm_Common_IdMaptoCountList(idMaptoCounts2));
            itemIoCtrl.refreshItemAddToResponse(idMaptoCount, br);
        });
    }

    private void _send(Sm_NewPve.Action action, Sm_NewPve_Stage sm_newPve_stage, Sm_Common_IdMaptoCountList idMapToCountList, Sm_Common_IdMaptoCount sweepDropList, IdMaptoCount idMaptoCount) {
        SenderFunc.sendInner(this, Sm_NewPve.class, Sm_NewPve.Builder.class, action, (b, br) -> {
            b.setStage(sm_newPve_stage);
            b.setSweepDropList(sweepDropList);
            b.setIdMapToCountList(idMapToCountList);
            itemIoCtrl.refreshItemAddToResponse(idMaptoCount.addAll(getFormationHerosIdMaptoCount()), br);
        });
    }


    private void _send(Sm_NewPve.Action action, Sm_NewPve_Stage sm_newPve_stage, IdMaptoCount idMaptoCount) {
        SenderFunc.sendInner(this, Sm_NewPve.class, Sm_NewPve.Builder.class, action, (b, br) -> {
            b.setStage(sm_newPve_stage);
            itemIoCtrl.refreshItemAddToResponse(idMaptoCount, br);
        });
    }


    private List<Chapter> _getAllChapters() {
        List<Chapter> chapterList = new ArrayList<>();
        Map<Integer, Chapter> idToChapters = target.getIdToChapter();
        for (Integer chapterId : idToChapters.keySet()) {
            chapterList.add(idToChapters.get(chapterId));
        }
        return chapterList;
    }


    /**
     * 一次性获取扫荡多次的物品
     *
     * @param protoIdMapToCount
     * @param stageId
     * @param times
     * @return
     */
    private IdMaptoCount _getDropByTimes(List<IdMaptoCount> protoIdMapToCount, int stageId, int times) {
        IdMaptoCount addIdMaptoCount = new IdMaptoCount();
        for (int i = MagicNumbers.DEFAULT_ONE; i <= times; i++) {
            IdMaptoCount drop = NewPveCtrlUtils.getStageDrop(stageId, getPlayerCtrl().getCurLevel());
            IdMaptoCount activityDrop = NewPveCtrlUtils.getActivityDrop(stageId, getPlayerCtrl().getCurLevel());
            IdAndCount exp = NewPveCtrlUtils.getFinishStageRoleExp(stageId);
            IdAndCount money = NewPveCtrlUtils.getFinishStageMoney(stageId);
            protoIdMapToCount.add(drop);
            protoIdMapToCount.add(activityDrop);
            addIdMaptoCount.addAll(drop.add(money).add(exp)).addAll(activityDrop);
        }
        return addIdMaptoCount;
    }

    /**
     * 获取扫荡额外掉落物品
     *
     * @param sweepDropList
     * @param stageId
     * @param times
     * @return
     */
    private IdMaptoCount _getSweepDrop(IdMaptoCount sweepDropList, int stageId, int times) {
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        for (int i = MagicNumbers.DEFAULT_ONE; i <= times; i++) {
            IdMaptoCount drop = NewPveCtrlUtils.getSweepDrop(stageId, getPlayerCtrl().getCurLevel());
            sweepDropList.addAll(drop);
        }
        return idMaptoCount;
    }

    /**
     * 扫荡多少次
     *
     * @param chapter
     * @param stage
     * @param mopUpTimes
     */
    private void _mopUpByTimes(Chapter chapter, NewStage stage, int mopUpTimes) {
        // 增加 统计数据用的挑战次数
        stage.setDailySumAttackTimes(stage.getDailySumAttackTimes() + mopUpTimes);
        for (int i = MagicNumbers.DEFAULT_ONE; i <= mopUpTimes; i++) {
            _mopUp(chapter, stage);
        }
    }

    /**
     * 保存扫荡数据
     *
     * @param chapter
     * @param stage
     */
    private void _mopUp(Chapter chapter, NewStage stage) {
        int star = stage.getMaxStar();
        _saveEndAttack(chapter, stage, star);
    }

    /**
     * 保存开始战斗
     *
     * @param stage
     * @param idMaptoCount
     */
    private void _saveBeginAttack(NewStage stage, long flag, IdMaptoCount idMaptoCount) {
        target.setLastAttackStageId(stage.getStageId());
        target.setLastAttackTime(flag);
        this.idMaptoCount = idMaptoCount;
        stage.setDailySumAttackTimes(stage.getDailySumAttackTimes() + MagicNumbers.DEFAULT_ONE);
    }


    /**
     * 保存结束战斗
     *
     * @param chapter
     * @param stage
     * @param star
     */
    private void _saveEndAttack(Chapter chapter, NewStage stage, int star) {
        stage.setDailyAttackTimes(stage.getDailyAttackTimes() + MagicNumbers.DEFAULT_ONE);
        stage.setSumAttackTimes(stage.getSumAttackTimes() + MagicNumbers.DEFAULT_ONE);
        target.setLastAttackTime(-1);
        target.setLastAttackStageId(-1);
        if (star > stage.getMaxStar()) {
            stage.setMaxStar(star);
            int sumStar = 0;
            for (Integer id : chapter.getIdToStage().keySet()) {
                sumStar += chapter.getIdToStage().get(id).getMaxStar();
            }
            chapter.setChapterSumStar(sumStar);
        }
        if (_isFirstPass(stage)) { // 第一次挑战成功
            stage.setFirstPassTime(System.currentTimeMillis());
            LogHandler.playerPveLog(getPlayerCtrl().getTarget(), stage.getStageId());
        }
        _addHerosExp(stage.getStageId());
    }

    /**
     * 增加英雄经验
     *
     * @param stageId
     */
    private void _addHerosExp(int stageId) {
        List<Integer> heroIds = _getFormationHeros();
        Table_Pve_Row pveRow = NewPveCtrlUtils.getPveRow(stageId);
        int exp = pveRow.getStageCardExp();
        for (Integer heroId : heroIds) {
            herosCtrl.addExpToHero(heroId, exp);
        }
    }


    /**
     * 主阵容武将
     *
     * @return
     */
    private IdMaptoCount getFormationHerosIdMaptoCount() {
        List<Integer> heroIds = _getFormationHeros();
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        for (Integer heroId : heroIds) {
            idMaptoCount.add(new IdAndCount(heroId));
        }
        return idMaptoCount;
    }


    /**
     * 获取主阵容卡牌
     *
     * @return
     */
    private List<Integer> _getFormationHeros() {
        List<Integer> heroIds = new ArrayList<>();
        Formation mainFormation = formationsCtrl.getOneFormation(FormationTypeEnum.F_MAIN);
        for (Entry<HeroPositionEnum, FormationPos> entry : mainFormation.getPosToFormationPos().entrySet()) {
            // 如果武将id小于等于0，说明这个阵容位置上没有武将
            if (entry.getValue().getHeroId() > 0) {
                heroIds.add(entry.getValue().getHeroId());
            }
        }
        return heroIds;
    }

    /**
     * 保存重置
     *
     * @param stage
     */
    private void _saveRestTimes(NewStage stage) {
        stage.setResetTimes(stage.getResetTimes() + MagicNumbers.DEFAULT_ONE);
        stage.setDailyAttackTimes(0);
    }

    /**
     * 是否是第一次通关
     *
     * @param stage
     * @return
     */
    private boolean _isFirstPass(NewStage stage) {
        return stage.getFirstPassTime() == 0;
    }

    /**
     * 是否是合法的战斗
     *
     * @param stageId
     * @param flag
     * @return
     */
    private boolean _isLegalBattle(int stageId, long flag) {
        return target.getLastAttackStageId() == stageId && target.getLastAttackTime() == flag;
    }

    /**
     * 获得扣除的结束战斗体力
     *
     * @param stageId 是否是刚进入Pve
     */
    private IdAndCount _getEndDeductionEnergy(int stageId) {
        int needEnergy = NewPveCtrlUtils.getNeedEnergy(stageId);
        MapTypeEnum type = NewPveCtrlUtils.getStageType(stageId);
        int afterAttackNeedEnergy = needEnergy - NewPveCtrlUtils.getBeginDeductionEnergyByStage(type);
        return new IdAndCount(ResourceTypeEnum.RES_ENERGY.getNumber(), afterAttackNeedEnergy);
    }

    private int _mathEnergyCanMopupTimes(int stageId) {
        long nowEnergy = resourcePointCtrl.getResourcePoint(ResourceTypeEnum.RES_ENERGY);
        int oneTimesNeed = NewPveCtrlUtils.getNeedEnergy(stageId);
        return (int) (nowEnergy / oneTimesNeed);
    }

    /**
     * 开始战斗时扣除的体力
     *
     * @param stageId
     * @return
     */
    private IdAndCount _getBeginDeductionEnergy(int stageId) {
        MapTypeEnum type = NewPveCtrlUtils.getStageType(stageId);
        return new IdAndCount(ResourceTypeEnum.RES_ENERGY.getNumber(), NewPveCtrlUtils.getBeginDeductionEnergyByStage(type));
    }


    /**
     * 获得N次扣除的体力值
     *
     * @param stageId
     * @param times
     * @return
     */
    private IdMaptoCount _deductionEnergyByTimes(int stageId, int times) {
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        for (int i = 0; i < times; i++) {
            idMaptoCount.add(new IdAndCount(ResourceTypeEnum.RES_ENERGY.getNumber(), NewPveCtrlUtils.getNeedEnergy(stageId)));
        }
        return idMaptoCount;
    }


    /**
     * 是否可以解锁关卡
     *
     * @param stageId
     * @return
     */
    private boolean _canUnLockStage(int stageId) {
        Table_Pve_Row pveRow = NewPveCtrlUtils.getPveRow(stageId);
        List<Integer> beforStageIds = pveRow.getBeforeStage().getAll();
        return _isSuccessStage(beforStageIds);
    }

    private boolean _isSuccessStage(List<Integer> beforeStageIdList) {
        for (Integer beforeStageId : beforeStageIdList) {
            Table_Pve_Row pveRow = NewPveCtrlUtils.getPveRow(beforeStageId);
            if (!target.getIdToChapter().containsKey(pveRow.getMapId())) {
                return false;
            }
            Chapter chapter = target.getIdToChapter().get(pveRow.getMapId());
            //前置关卡不是0，地图包里也没有前置关卡，说明前置关卡没解锁
            if (beforeStageId != 0 && !chapter.getIdToStage().containsKey(beforeStageId)) {
                return false;
            }
            //没有前置关卡的地图，说明前置关卡未解锁
        }
        return true;
    }

    /**
     * 检查关卡的前置关卡是否已经通关
     *
     * @param chapter
     * @param stageId
     * @return
     */
    private boolean _isLockStage(Chapter chapter, int stageId) {
        Table_Pve_Row pveRow = NewPveCtrlUtils.getPveRow(stageId);
        List<Integer> beforStageIds = pveRow.getBeforeStage().getAll();
        for (Integer beforStageId : beforStageIds) {
            if (beforStageId != 0 && !chapter.getIdToStage().containsKey(beforStageId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查章节的前置章节是否已经通关
     *
     * @param chapterId
     * @return
     */

    private boolean _isLockChapter(int chapterId) {
        Table_Maps_Row mapsRow = NewPveCtrlUtils.getMapRow(chapterId);
        int beforMapId = mapsRow.getBeforeMap();
        if (beforMapId == 0) {
            return false;
        }
        return !target.getIdToChapter().containsKey(beforMapId);
    }


    /**
     * 是否可以解锁章节
     *
     * @param chapterId
     * @return
     */
    private boolean _canUnLockChapter(int chapterId) {
        Table_Maps_Row mapsRow = NewPveCtrlUtils.getMapRow(chapterId);
        int beforMapId = mapsRow.getBeforeMap();
        return beforMapId == 0 || target.getIdToChapter().containsKey(beforMapId);
    }

    /**
     * 用关卡ID获取章节ID
     *
     * @param stageId
     * @return
     */

    private int _getChapterIdByStageId(int stageId) {
        Table_Pve_Row pveRow = NewPveCtrlUtils.getPveRow(stageId);
        return pveRow.getMapId();
    }


    // ======================== 针对PVE模块的数据采集 start start start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /**
     * 统计 精英/普通 每日攻打次数
     *
     * @param stage
     * @param times 不论成功失败
     */
    private void statisticsAttackTimes(NewStage stage, int times) {
        MapTypeEnum type = NewPveCtrlUtils.getStageType(stage.getStageId());
        Pr_NotifyMsg notifyMsg = null;
        if (type == MapTypeEnum.MAP_ELITE) {
            notifyMsg = new Pr_NotifyMsg(PrivateNotifyTypeEnum.NewPve_DaliyAttackEliteNum, times);
        } else if (type == MapTypeEnum.MAP_SIMPLE) {
            notifyMsg = new Pr_NotifyMsg(PrivateNotifyTypeEnum.NewPve_DaliyAttackNormalTimes, times);
        }
        if (notifyMsg != null) {
            sendPrivateMsg(notifyMsg);
        }
    }

    /**
     * 统计总星数（目前统计 主线、精英）
     */
    private void statisticsSumStar() {
        int sumStar = 0;
        for (Chapter chapter : target.getIdToChapter().values()) {
            Table_Maps_Row mapsRow = NewPveCtrlUtils.getMapRow(chapter.getChapterId());
            MapTypeEnum mapType = MapTypeEnum.valueOf(mapsRow.getMapType());
            if (mapType == MapTypeEnum.MAP_ELITE || mapType == MapTypeEnum.MAP_SIMPLE) {
                sumStar += chapter.getChapterSumStar();
            }
        }
        RedisRankUtils.insertRank(sumStar, getPlayerCtrl().getPlayerId(), getPlayerCtrl().getOuterRealmId(), CommonRankTypeEnum.RK_PVE_STAR);
    }

    /**
     * 完成通过的关卡
     */
    private void statisticsCompleteStage(int stageId) {
        Pr_NotifyMsg notifyMsg2 = new Pr_NotifyMsg(PrivateNotifyTypeEnum.NewPve_HasComplet_Stage, stageId, MagicNumbers.DEFAULT_ONE);
        sendPrivateMsg(notifyMsg2);
    }


    // ======================== 针对PVE模块的数据采集 end end end <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

}
