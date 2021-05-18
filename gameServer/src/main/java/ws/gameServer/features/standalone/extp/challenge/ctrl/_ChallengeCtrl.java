package ws.gameServer.features.standalone.extp.challenge.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.challenge.utils.ChallengeCtrlProtos;
import ws.gameServer.features.standalone.extp.challenge.utils.ChallengeCtrlUtils;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.gameServer.system.logHandler.LogExcep;
import ws.protos.ChallengeProtos.Sm_Challenge;
import ws.protos.EnumsProtos.ChallengeTypeEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_DropLibrary_Row;
import ws.relationship.table.tableRows.Table_Pve_Row;
import ws.relationship.table.tableRows.Table_Vip_Row;
import ws.relationship.topLevelPojos.challenge.Challenge;
import ws.relationship.topLevelPojos.challenge.Stage;
import ws.relationship.utils.ProtoUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class _ChallengeCtrl extends AbstractPlayerExteControler<Challenge> implements ChallengeCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_ChallengeCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;
    private IdMaptoCount idMaptoCount;

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
        _rest();
    }


    @Override
    public void sync() {
        SenderFunc.sendInner(this, Sm_Challenge.class, Sm_Challenge.Builder.class, Sm_Challenge.Action.RESP_SYNC, (b, br) -> {
            b.addAllStageTypeInfo(ChallengeCtrlProtos.create_Sm_Challenge_StageTypeInfo_List(getTarget()));
        });
    }


    @Override
    public void beginAttack(int stageId) {
        LogicCheckUtils.validateParam(Integer.class, stageId);
        if (!ChallengeCtrlUtils.isValidStageId(stageId)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s  未发布不可以挑战！", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        ChallengeTypeEnum stageType = ChallengeCtrlUtils.getChallengeType(stageId);
        if (!ChallengeCtrlUtils.isOpenDay(stageId)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s 关卡未到开启日期！", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!ChallengeCtrlUtils.isPlayerLevelCanAttack(stageId, getPlayerCtrl().getCurLevel())) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s  玩家等级=%s不够=%s，不可以挑战！", stageId, getPlayerCtrl().getCurLevel(),
                    ChallengeCtrlUtils.getStageNeedPlayerLevel(stageId));
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!isOpenStage(stageId, stageType)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s 关卡未解锁！", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!_isCdReady(stageType)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "CD时间未完成,stageId=%s", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        int hasAttackTimes = _getHasAttackTimes(stageId);
        if (!ChallengeCtrlUtils.isAttackTimesEnough(hasAttackTimes, stageId)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s  剩余挑战次数不足！", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        List<IdMaptoCount> protosIdMapToCount = new ArrayList<>();
        this.idMaptoCount = new IdMaptoCount();
        idMaptoCount.addAll(_getDrop(stageId, true, MagicNumbers.PERCENT_NUMBER));
        protosIdMapToCount.add(idMaptoCount);
        _saveBeginAttack(stageId);
        boolean isPass = _isPass(stageId, stageType);
        SenderFunc.sendInner(this, Sm_Challenge.class, Sm_Challenge.Builder.class, Sm_Challenge.Action.RESP_BEGIN, (b, br) -> {
            b.setIdMapToCountList(ProtoUtils.create_Sm_Common_IdMaptoCountList(protosIdMapToCount));
            b.setFlag(target.getLastAttackTime());
            b.setIsPass(isPass);
        });
        save();
    }


    @Override
    public void endAttack(int stageId, long flag, boolean isWin, int percent, int score) {
        LogicCheckUtils.validateParam(Integer.class, stageId);
        if (!ChallengeCtrlUtils.isValidStageId(stageId)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s  未发布不可以挑战！", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        ChallengeTypeEnum stageType = ChallengeCtrlUtils.getChallengeType(stageId);
        if (!_isLegalBattle(stageId, flag)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "请求结束的战斗是非法的,stageId=%s", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }

        IdMaptoCount idMaptoCount = _getDrop(stageId, isWin, percent);
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(Sm_Challenge.Action.RESP_END).addItem(idMaptoCount);
        _save(stageId, isWin, percent, stageType);
        Stage stage = _getStage(stageType);
        SenderFunc.sendInner(this, Sm_Challenge.class, Sm_Challenge.Builder.class, Sm_Challenge.Action.RESP_END, (b, br) -> {
            b.addStageTypeInfo(ChallengeCtrlProtos.create_Sm_Challenge_StageTypeInfo(stageType, stage));
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
        });
        save();
    }


    @Override
    public void mopup(int stageId, int mopupTimes) {
        LogicCheckUtils.validateParam(Integer.class, stageId);
        if (!ChallengeCtrlUtils.isValidStageId(stageId)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s  未发布不可以挑战！", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        ChallengeTypeEnum stageType = ChallengeCtrlUtils.getChallengeType(stageId);
        if (!ChallengeCtrlUtils.isOpenDay(stageId)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s 关卡未到开启日期！", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!ChallengeCtrlUtils.isPlayerLevelCanAttack(stageId, getPlayerCtrl().getCurLevel())) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s  玩家等级=%s不够=%s，不可以挑战！", stageId, getPlayerCtrl().getCurLevel(),
                    ChallengeCtrlUtils.getStageNeedPlayerLevel(stageId));
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!isOpenStage(stageId, stageType)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s 关卡未解锁！", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!_isCdReady(stageType)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "CD时间未完成,stageId=%s", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!ChallengeCtrlUtils.isAttackTimesEnough(_getHasAttackTimes(stageId), stageId)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s  剩余挑战次数不足！", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!_isPass(stageId, stageType)) {
            String msg = String.format(LogExcep.LOGIC_EXCEP + "StageId=%s  该副本没有通关过，不能扫荡！", stageId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        List<IdMaptoCount> protoIdMapToCount = new ArrayList<>();
        if (mopupTimes == MagicNumbers.DEFAULT_ZERO) {
            mopupTimes = MagicNumbers.DEFAULT_ONE;
        }
        IdMaptoCount idMaptoCount = _getDropByTimes(protoIdMapToCount, stageId, mopupTimes);
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(Sm_Challenge.Action.RESP_MOPUP).addItem(idMaptoCount);
        _save(stageId, true, MagicNumbers.PERCENT_NUMBER, stageType);
        Stage stage = _getStage(stageType);
        SenderFunc.sendInner(this, Sm_Challenge.class, Sm_Challenge.Builder.class, Sm_Challenge.Action.RESP_MOPUP, (b, br) -> {
            b.addStageTypeInfo(ChallengeCtrlProtos.create_Sm_Challenge_StageTypeInfo(stageType, stage));
            b.setIdMapToCountList(ProtoUtils.create_Sm_Common_IdMaptoCountList(protoIdMapToCount));
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
        });
        save();
    }




/*
============================================================以下为内部方法==============================================================
 */


    private void _rest() {
        target.setLastAttackStageId(MagicNumbers.DEFAULT_ZERO);
        target.setLastAttackTime(MagicNumbers.DEFAULT_ZERO);
        for (Stage stage : target.getStageTypeToStage().values()) {
            stage.setAttackTimes(MagicNumbers.DEFAULT_ZERO);
            stage.setCdTime(MagicNumbers.DEFAULT_ZERO);
        }
    }


    /**
     * 是否是开启关卡
     *
     * @param stageId
     * @param stageType
     * @return
     */
    private boolean isOpenStage(int stageId, ChallengeTypeEnum stageType) {
        Table_Pve_Row pveRow = RootTc.get(Table_Pve_Row.class).get(stageId);
        List<Integer> beforeStageIds = pveRow.getBeforeStage().getAll();
        target.getStageTypeToStage().putIfAbsent(stageType, new Stage());
        Stage stage = _getStage(stageType);
        Set<Integer> stageIdSet = stage.getOpenStageIdAndWin().keySet();
        for (Integer beforeStageId : beforeStageIds) {
            if (beforeStageId == 0) {
                return true;
            } else if (!stageIdSet.contains(beforeStageId)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 检查CD冷却
     *
     * @param stageType
     * @return
     */
    private boolean _isCdReady(ChallengeTypeEnum stageType) {
        long lastAttackEndTime = target.getStageTypeToStage().get(stageType).getCdTime();
        Table_Vip_Row vipRow = Table_Vip_Row.getVipRow(getPlayerCtrl().getTarget().getPayment().getVipLevel());
        long cd = vipRow.getGoldStageCool();
        return System.currentTimeMillis() >= (lastAttackEndTime + cd);
    }

    /**
     * 是否通关
     *
     * @param stageId
     * @param stageType
     * @return
     */
    private boolean _isPass(int stageId, ChallengeTypeEnum stageType) {
        Stage stage = target.getStageTypeToStage().get(stageType);
        if (!stage.getOpenStageIdAndWin().containsKey(stageId)) {
            return false;
        }
        return stage.getOpenStageIdAndWin().get(stageId);
    }


    private void _saveBeginAttack(int stageId) {
        target.setLastAttackTime(System.currentTimeMillis());
        target.setLastAttackStageId(stageId);
    }

    /**
     * 获取已经挑战次数
     *
     * @param stageId
     * @return
     */
    private int _getHasAttackTimes(int stageId) {
        ChallengeTypeEnum challengeTypeEnum = ChallengeCtrlUtils.getChallengeType(stageId);
        Stage stage = _getStage(challengeTypeEnum);
        return stage.getAttackTimes();
    }


    private Stage _getStage(ChallengeTypeEnum stageType) {
        target.getStageTypeToStage().putIfAbsent(stageType, new Stage());
        return target.getStageTypeToStage().get(stageType);
    }

    /**
     * 根据次数获取掉落
     *
     * @param protoIdMapToCount
     * @param stageId
     * @param mopupTimes
     * @return
     */
    private IdMaptoCount _getDropByTimes(List<IdMaptoCount> protoIdMapToCount, int stageId, int mopupTimes) {
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        for (int i = 0; i < mopupTimes; i++) {
            IdMaptoCount drop = _getDrop(stageId, true, MagicNumbers.PERCENT_NUMBER);
            idMaptoCount.addAll(drop);
            protoIdMapToCount.add(drop);
        }
        return idMaptoCount;
    }


    /**
     * 获取关卡掉落
     *
     * @param stageId
     * @param isWin
     * @param percent
     */
    private IdMaptoCount _getDrop(int stageId, boolean isWin, int percent) {
        int stageTypeId = ChallengeCtrlUtils.getChallengeTypeId(stageId);
        switch (ChallengeTypeEnum.valueOf(stageTypeId)) {
            case CHALLENGE_EXP:
            case CHALLENGE_MONEY:
                return _getDropByPercent(stageId, percent);
            case CHALLENGE_GHOST:
            case CHALLENGE_WOMEN:
                return _getDropByResult(stageId, isWin);
            default:
                throw new IllegalArgumentException();
        }
    }

    private IdMaptoCount _getDropByResult(int stageId, boolean isWin) {
        IdMaptoCount idMapToCount = new IdMaptoCount();
        if (isWin) {
            List<Integer> libraryIds = RootTc.get(Table_Pve_Row.class).get(stageId).getStageDrop().getAll();
            idMapToCount.addAll(Table_DropLibrary_Row.drop(libraryIds, getPlayerCtrl().getCurLevel()));
        }
        return idMapToCount;
    }

    /**
     * 根据百分比获取掉落
     *
     * @param stageId
     * @param percent
     * @return
     */
    private IdMaptoCount _getDropByPercent(int stageId, int percent) {
        List<Integer> libraryIds = RootTc.get(Table_Pve_Row.class).get(stageId).getStageDrop().getAll();
        IdMaptoCount idMapToCount = Table_DropLibrary_Row.drop(libraryIds, getPlayerCtrl().getCurLevel());
        IdMaptoCount result = new IdMaptoCount();
        for (Integer id : idMapToCount.getAllKeys()) {
            //取出每一个id的数量乘百分比
            double f = percent / MagicNumbers.PERCENT_NUMBER;
            double realPrecent = f / MagicNumbers.PERCENT_NUMBER;
            long count = (long) Math.floor(idMapToCount.getCount(id) * realPrecent);
            result.add(new IdAndCount(id, count));
        }
        return result;
    }


    /**
     * 根据百分比保存战斗结果
     *
     * @param stageId
     * @param isWin
     * @param percent
     * @param stageType
     */
    private void _save(int stageId, boolean isWin, int percent, ChallengeTypeEnum stageType) {
        Stage stage = target.getStageTypeToStage().get(stageType);
        switch (stageType) {
            case CHALLENGE_EXP:
            case CHALLENGE_MONEY:
                stage.setCdTime(System.currentTimeMillis());
                if (percent >= MagicNumbers.PERCENT_NUMBER) {
                    stage.getOpenStageIdAndWin().put(stageId, isWin);
                } else {
                    stage.getOpenStageIdAndWin().put(stageId, false);
                }
                stage.setAttackTimes(stage.getAttackTimes() + 1);
                break;
            case CHALLENGE_GHOST:
            case CHALLENGE_WOMEN:
                if (isWin) {
                    stage.setCdTime(System.currentTimeMillis());
                    stage.getOpenStageIdAndWin().put(stageId, isWin);
                    stage.setAttackTimes(stage.getAttackTimes() + 1);
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
        target.getStageTypeToStage().put(stageType, stage);
        _resetLastAttack();
    }


    /**
     * 重置战斗标示
     */
    private void _resetLastAttack() {
        if (target.getLastAttackStageId() != -1) {
            target.setLastAttackStageId(-1);
        }
        if (target.getLastAttackTime() != -1) {
            target.setLastAttackTime(-1);
        }
    }

    /**
     * 是否是合法的结束战斗信息
     *
     * @param stageId
     * @param flag
     * @return
     */
    private boolean _isLegalBattle(int stageId, long flag) {
        return target.getLastAttackTime() == flag && target.getLastAttackStageId() == stageId;
    }
}
