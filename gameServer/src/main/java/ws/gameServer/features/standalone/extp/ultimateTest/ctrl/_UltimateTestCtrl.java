package ws.gameServer.features.standalone.extp.ultimateTest.ctrl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.dataCenter.enums.PrivateNotifyTypeEnum;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_NotifyMsg;
import ws.gameServer.features.standalone.extp.heros.HerosExtp;
import ws.gameServer.features.standalone.extp.heros.ctrl.HerosCtrl;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.ultimateTest.utils.UltimateTestCtrlProtos;
import ws.gameServer.features.standalone.extp.ultimateTest.utils.UltimateTestCtrlUtils;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.BattleEnumsProtos.SpecialBuffEnum;
import ws.protos.EnumsProtos.CommonRankTypeEnum;
import ws.protos.EnumsProtos.HardTypeEnum;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.protos.EnumsProtos.UltimateTestBuffIndexTypeEnum;
import ws.protos.UltimateTestProtos.Sm_UltimateTest;
import ws.protos.UltimateTestProtos.Sm_UltimateTest.Action;
import ws.protos.UltimateTestProtos.Sm_UltimateTest_Hero_Info;
import ws.protos.UltimateTestProtos.Sm_UltimateTest_Use_Buff;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_Consume_Row;
import ws.relationship.table.tableRows.Table_New_Buff_Row;
import ws.relationship.table.tableRows.Table_New_Skill_Row;
import ws.relationship.table.tableRows.Table_PointReward_Row;
import ws.relationship.table.tableRows.Table_Vip_Row;
import ws.relationship.topLevelPojos.ultimateTest.UltimateTest;
import ws.relationship.topLevelPojos.ultimateTest.UltimateTestHero;
import ws.relationship.topLevelPojos.ultimateTest.UltimatetestBuff;
import ws.relationship.topLevelPojos.ultimateTest.UltimatetestEnemy;
import ws.relationship.utils.ProtoUtils;
import ws.relationship.utils.RedisRankUtils;
import ws.relationship.utils.RelationshipCommonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class _UltimateTestCtrl extends AbstractPlayerExteControler<UltimateTest> implements UltimateTestCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_UltimateTestCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;
    private HerosCtrl herosCtrl;


    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
        herosCtrl = getPlayerCtrl().getExtension(HerosExtp.class).getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
        _reset();
    }

    @Override
    public void _initAfterChanged() throws Exception {

    }

    @Override
    public void sync() {
        SenderFunc.sendInner(this, Sm_UltimateTest.class, Sm_UltimateTest.Builder.class, Sm_UltimateTest.Action.RESP_SYNC, (b, br) -> {
            b.setIcon(getIcon());
            b.setStageLevel(target.getStageLevel());
            b.setInfo(UltimateTestCtrlProtos.create_Sm_UltimateTest_Info(target));
            if (UltimateTestCtrlUtils.isAttackStage(target.getStageLevel())) {
                _tryRandomEnemy();
                b.addAllEnemies(UltimateTestCtrlProtos.create_Sm_UltimateTest_Enemy_List(target.getHardLevelToEnemies()));
            } else if (UltimateTestCtrlUtils.isBuffStage(target.getStageLevel())) {
                _tryRandomBuff();
                b.addAllBuffInfos(UltimateTestCtrlProtos.create_Sm_UltimateTest_Buff_Info_List(target.getBuffIndexAndBuff()));
            }
        });
        save();
    }


    @Override
    public void getEnemy() {
        int stageLevel = _getStageLevel();
        if (!UltimateTestCtrlUtils.isAttackStage(stageLevel)) {
            String msg = String.format("不是战斗关卡stageLevel=%s", stageLevel);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        LOGGER.debug("当前层数 stageLevel={},before random", target.getStageLevel());
        for (Entry<HardTypeEnum, UltimatetestEnemy> entry : target.getHardLevelToEnemies().entrySet()) {
            LOGGER.debug(entry.getValue().toString());
        }
        _tryRandomEnemy();
        LOGGER.debug("当前层数 stageLevel={},after getEnemy", target.getStageLevel());
        for (Entry<HardTypeEnum, UltimatetestEnemy> entry : target.getHardLevelToEnemies().entrySet()) {
            LOGGER.debug(entry.getValue().toString());
        }
        Map<HardTypeEnum, UltimatetestEnemy> hardLevelToEnemies = target.getHardLevelToEnemies();
        SenderFunc.sendInner(this, Sm_UltimateTest.class, Sm_UltimateTest.Builder.class, Sm_UltimateTest.Action.RESP_GET_ENEMY, (b, br) -> {
            b.addAllEnemies(UltimateTestCtrlProtos.create_Sm_UltimateTest_Enemy_List(hardLevelToEnemies));
        });

    }

    @Override
    public void beginAttack(HardTypeEnum hardLevel) {
        int stageLevel = _getStageLevel();
        LogicCheckUtils.validateParam(HardTypeEnum.class, hardLevel);
        if (!UltimateTestCtrlUtils.isAttackStage(stageLevel)) {
            String msg = String.format("不是战斗关卡stageLevel=%s", stageLevel);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!_hasEnemy(hardLevel)) {
            String msg = String.format("没有敌人,当前stageLevel=%s,传入hardLevel=%s", stageLevel, hardLevel);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        _saveBegin(hardLevel);

        int bestStar = _getBestResult(stageLevel, hardLevel);
        SenderFunc.sendInner(this, Sm_UltimateTest.class, Sm_UltimateTest.Builder.class, Sm_UltimateTest.Action.RESP_BEGIN_ATTACK, (b, br) -> {
            b.setFlag(target.getFlag());
            b.setStageLevel(stageLevel);
            b.setBestStar(bestStar);
        });
        save();
        LOGGER.debug("当前层数 stageLevel={},after beginAttack", target.getStageLevel());
        for (Entry<HardTypeEnum, UltimatetestEnemy> entry : target.getHardLevelToEnemies().entrySet()) {
            LOGGER.debug(entry.getValue().toString());
        }
    }

    @Override
    public void endAttack(long flag, int stageLevel, List<Sm_UltimateTest_Hero_Info> heroInfos, int star, boolean isWin, HardTypeEnum hardLevel) {
        if (!_isLegalArg(flag, stageLevel, hardLevel)) {
            String msg = String.format("结束战斗参数不合法，flag=%s,stageId=%s,当前库flag=%s,stageId=%s", flag, stageLevel, target.getFlag(), target.getStageLevel());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        List<UltimateTestHero> heroes = protoUltimateTestHeroParsToPojo(heroInfos);
        if (!isWin) {
            SenderFunc.sendInner(this, Sm_UltimateTest.class, Sm_UltimateTest.Builder.class, Sm_UltimateTest.Action.RESP_END_ATTACK, (b, br) -> {
            });
            _saveHeroInfo(heroes, isWin);
            save();
            return;
        }
        int addScore = _getScoreByHardLevelandStageLevel(target.getStageLevel(), target.getHardLevel());
        int addBuffstar = _mathBuffStar(star, target.getStageLevel(), target.getHardLevel());
        _saveHeroInfo(heroes, isWin);
        _saveEnd(addBuffstar, addScore * star);
        _saveBestResult(stageLevel, hardLevel, star);
        _insertRank();
        SenderFunc.sendInner(this, Sm_UltimateTest.class, Sm_UltimateTest.Builder.class, Sm_UltimateTest.Action.RESP_END_ATTACK, (b, br) -> {
            b.setResult(UltimateTestCtrlProtos.create_Sm_UltimateTest_FightResult(addScore, addBuffstar));
            b.setInfo(UltimateTestCtrlProtos.create_Sm_UltimateTest_Info(target));
        });
        save();
    }


    @Override
    public void buyBuff(UltimateTestBuffIndexTypeEnum buffIndex) {
        if (!_hasBuffType(buffIndex)) {
            String msg = String.format("没有这个buffType,buffType=%s,当前层可以购买buff=%s", buffIndex, target.getBuffIndexAndBuff());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        int buffPrice = _getBuffPirce(buffIndex);
        if (!_canRemoveBuffStar(buffPrice)) {
            String msg = String.format("buffStar不足以购买,buffType=%s,needBuffStar=%s,当前拥有buffStar=%s", buffIndex, buffPrice, target.getBuffStar());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        int buffId = target.getBuffIndexAndBuff().get(buffIndex).getBuffId();
        _removeBuffStar(buffPrice);
        _removeBuffFromCache(buffIndex);
        _saveBuff(buffId);
        SenderFunc.sendInner(this, Sm_UltimateTest.class, Sm_UltimateTest.Builder.class, Sm_UltimateTest.Action.RESP_BUFF, (b, br) -> {
            b.setInfo(UltimateTestCtrlProtos.create_Sm_UltimateTest_Info(target));
        });
        save();
    }


    @Override
    public void getReward() {
        if (!UltimateTestCtrlUtils.isGetRewardStage(target.getStageLevel())) {
            String msg = String.format("不是领奖关卡,当前stageLevel=%s", target.getStageLevel());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdMaptoCount addResource = UltimateTestCtrlUtils.getReward(target.getStageLevel(), getPlayerCtrl().getCurLevel());
        List<IdMaptoCount> idMaptoCountList = new ArrayList<>();
        idMaptoCountList.add(addResource);
        setStageLevel(target.getStageLevel() + MagicNumbers.DEFAULT_ONE);
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(Sm_UltimateTest.Action.RESP_GET_REWARD).addItem(addResource);
        SenderFunc.sendInner(this, Sm_UltimateTest.class, Sm_UltimateTest.Builder.class, Sm_UltimateTest.Action.RESP_GET_REWARD, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
            b.setIdMapToCountList(ProtoUtils.create_Sm_Common_IdMaptoCountList(idMaptoCountList));
        });
        save();
    }

    @Override
    public void getBuffInfo() {
        int stageLevel = _getStageLevel();
        if (!UltimateTestCtrlUtils.isBuffStage(stageLevel)) {
            String msg = String.format("不是buff关卡,当前stageLevel=%s", target.getStageLevel());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        _tryRandomBuff();
        setStageLevel(target.getStageLevel() + MagicNumbers.DEFAULT_ONE);
        Map<UltimateTestBuffIndexTypeEnum, UltimatetestBuff> buffIndexAndBuff = target.getBuffIndexAndBuff();
        SenderFunc.sendInner(this, Sm_UltimateTest.class, Sm_UltimateTest.Builder.class, Sm_UltimateTest.Action.RESP_GET_BUFF, (b, br) -> {
            b.addAllBuffInfos(UltimateTestCtrlProtos.create_Sm_UltimateTest_Buff_Info_List(buffIndexAndBuff));
        });
        save();
    }

    @Override
    public void useBuff(List<Sm_UltimateTest_Use_Buff> useBuffs, UltimateTestBuffIndexTypeEnum buffIndex) {
        if (!_hasBuffType(buffIndex)) {
            String msg = String.format("没有这个buffType,buffType=%s,当前层可以购买buff=%s", buffIndex, target.getBuffIndexAndBuff());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        int buffPrice = _getBuffPirce(buffIndex);
        if (!_canRemoveBuffStar(buffPrice)) {
            String msg = String.format("buffStar不足以购买,buffType=%s,needBuffStar=%s,当前拥有buffStar=%s", buffIndex, buffPrice, target.getBuffStar());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        int buffId = target.getBuffIndexAndBuff().get(buffIndex).getBuffId();
        _removeBuffStar(buffPrice);
        _removeBuffFromCache(buffIndex);
        _useBuff(useBuffs, buffId);
        SenderFunc.sendInner(this, Sm_UltimateTest.class, Sm_UltimateTest.Builder.class, Sm_UltimateTest.Action.RESP_USE_BUFF, (b, br) -> {
            b.setInfo(UltimateTestCtrlProtos.create_Sm_UltimateTest_Info(target));
        });
        save();
    }


    /**
     * 复活格斗家
     *
     * @param useBuffs
     */
    private void _ResurGence(List<Sm_UltimateTest_Use_Buff> useBuffs) {
        Sm_UltimateTest_Use_Buff info = useBuffs.get(MagicNumbers.DEFAULT_ZERO);
        _checkHeros(info.getHeroId());
        UltimateTestHero hero = target.getIdToHeros().get(info.getHeroId());
        if (hero.getHp() > MagicNumbers.DEFAULT_ZERO) {
            LOGGER.debug("复活buff,格斗家血量不为0,不需要复活");
            return;
        }
        LOGGER.debug("复活buff,复活了1个格斗家 id:={}", hero.getHeroId());
        hero.setHp(info.getHp());
    }


    /**
     * 增加怒气
     *
     * @param useBuffs
     */
    private void _addAnger(List<Sm_UltimateTest_Use_Buff> useBuffs) {
        for (Sm_UltimateTest_Use_Buff info : useBuffs) {
            _checkHeros(info.getHeroId());
            UltimateTestHero hero = target.getIdToHeros().get(info.getHeroId());
            if (hero.getHp() > MagicNumbers.DEFAULT_ZERO) {
                hero.setAnger(hero.getAnger() + info.getAnger());
            }
        }
    }

    /**
     * 增加生命
     *
     * @param useBuffs
     */
    private void _addHp(List<Sm_UltimateTest_Use_Buff> useBuffs) {
        for (Sm_UltimateTest_Use_Buff info : useBuffs) {
            _checkHeros(info.getHeroId());
            UltimateTestHero hero = target.getIdToHeros().get(info.getHeroId());
            if (hero.getHp() > MagicNumbers.DEFAULT_ZERO) {
                hero.setAnger(hero.getHp() + info.getHp());
            }
        }
    }

    private void _useBuff(List<Sm_UltimateTest_Use_Buff> useBuffs, int buffId) {
        Table_New_Skill_Row skillRow = RootTc.get(Table_New_Skill_Row.class).get(buffId);
        int buffTypeId = skillRow.getbUFFId().get(MagicNumbers.DEFAULT_ZERO);
        Table_New_Buff_Row buffRow = RootTc.get(Table_New_Buff_Row.class).get(buffTypeId);
        SpecialBuffEnum buffType = SpecialBuffEnum.valueOf(buffRow.getId());
        if (buffType == SpecialBuffEnum.RESURGENCE50) {//复活
            if (useBuffs.size() != MagicNumbers.DEFAULT_ZERO) {
                _ResurGence(useBuffs);
            } else {
                LOGGER.debug("复活buff,格斗家列表为空,没有选择复活的格斗家");
            }
        } else if (buffRow.getbUFFProperty() == HeroAttrTypeEnum.Anger) { //加怒气
            _addAnger(useBuffs);
        } else if (buffRow.getbUFFProperty() == HeroAttrTypeEnum.HPPlus || buffRow.getbUFFProperty() == HeroAttrTypeEnum.HP) { // 加生命
            _addHp(useBuffs);
        }
    }


    /**
     * 检查是否拥有格斗家
     *
     * @param heroId
     */
    private void _checkHeros(int heroId) {
        LogicCheckUtils.canRemove(itemIoCtrl, new IdAndCount(heroId));
    }

    @Override
    public void getSpeReward(int rewardScore) {
        LogicCheckUtils.validateParam(Integer.class, rewardScore);
        if (target.getHistoryTestScore() + target.getTestScore() < rewardScore) {
            String msg = String.format("当前积分不足以领奖,当前stageId=%s,Testscore=%s,hitstoryScore=%s,尝试领取的积分=%s", target.getStageLevel(), target.getTestScore(), target.getHistoryTestScore(), rewardScore);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        Table_PointReward_Row rewardRow = Table_PointReward_Row.getUltimateTestRow(rewardScore);
        if (UltimateTestCtrlUtils.hasGetReward(target.getRewardScores(), rewardRow.getNeedPoints())) {
            String msg = String.format("已经领取过这个奖励了,当前stageId=%s，rewardScore=%s", target.getStageLevel(), rewardRow.getNeedPoints());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdMaptoCount toAdd = rewardRow.getRewards(getPlayerCtrl().getCurLevel());
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(Sm_UltimateTest.Action.RESP_GET_SPE_REWARD).addItem(toAdd);
        _saveSpeReward(rewardScore);
        SenderFunc.sendInner(this, Sm_UltimateTest.class, Sm_UltimateTest.Builder.class, Sm_UltimateTest.Action.RESP_GET_SPE_REWARD, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
            b.addAllRewardScores(target.getRewardScores());
        });
    }


    @Override
    public void speReward() {
        SenderFunc.sendInner(this, Sm_UltimateTest.class, Sm_UltimateTest.Builder.class, Sm_UltimateTest.Action.RESP_SPE_REWARD, (b, br) -> {
            b.setScore(target.getTestScore());
            b.setHistoryScore(target.getHistoryTestScore());
            b.addAllRewardScores(target.getRewardScores());
        });
    }

    @Override
    public void openBox(int stageLevel) {
        LogicCheckUtils.validateParam(Integer.class, stageLevel);
        LogicCheckUtils.canRemove(itemIoCtrl, _deductionResource(stageLevel));
        if (!UltimateTestCtrlUtils.hasBoxStage(stageLevel)) {
            String msg = String.format("没有这个宝箱,当前stageLevel=%s", stageLevel);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!_isTimesCanOpen(stageLevel)) {
            String msg = String.format("今日本层已经开过20次了", stageLevel);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdMaptoCount removeRefresh = itemIoExtp.getControlerForUpdate(Sm_UltimateTest.Action.RESP_OPEN_BOX).removeItem(_deductionResource(stageLevel));
        IdMaptoCount addResource = UltimateTestCtrlUtils.getBoxReward(stageLevel, getPlayerCtrl().getCurLevel());
        List<IdMaptoCount> idMaptoCountList = new ArrayList<>();
        idMaptoCountList.add(addResource);
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(Sm_UltimateTest.Action.RESP_OPEN_BOX).addItem(addResource);
        _saveOpenBox(stageLevel);
        SenderFunc.sendInner(this, Sm_UltimateTest.class, Sm_UltimateTest.Builder.class, Sm_UltimateTest.Action.RESP_OPEN_BOX, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(removeRefresh).addAll(refresh), br);
            b.setIdMapToCountList(ProtoUtils.create_Sm_Common_IdMaptoCountList(idMaptoCountList));
            b.setStageLevel(target.getStageLevel());
        });
        save();

    }

    private boolean _isTimesCanOpen(int stageLevel) {
        return target.getStageLevelToTimes().get(stageLevel) < MagicNumbers.ULTIMATE_TEST_OPEN_BOX_MAX_TIMES;
    }


    @Override
    public void openAllBox(int times) {
        List<Integer> allRewardStageIds = UltimateTestCtrlUtils.getAllRewardStageIds(target.getStageLevel());
        IdMaptoCount needResource = _deductionResourceByOpenAllBoxByTimes(allRewardStageIds, times);
        LogicCheckUtils.canRemove(itemIoCtrl, needResource);
        IdMaptoCount removeRefresh = itemIoExtp.getControlerForUpdate(Sm_UltimateTest.Action.RESP_OPEN_ALL_BOX).removeItem(needResource);
        IdMaptoCount addRefresh = new IdMaptoCount();
        List<IdMaptoCount> idMaptoCountList = new ArrayList<>();
        for (int i = 0; i <= times; i++) {
            idMaptoCountList.addAll(_openAllBox(allRewardStageIds, addRefresh));
        }
        _saveOpenAllBoxByTimes(allRewardStageIds, times);
        SenderFunc.sendInner(this, Sm_UltimateTest.class, Sm_UltimateTest.Builder.class, Sm_UltimateTest.Action.RESP_OPEN_ALL_BOX, (b, br) -> {
            b.setIdMapToCountList(ProtoUtils.create_Sm_Common_IdMaptoCountList(idMaptoCountList));
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(addRefresh).addAll(removeRefresh), br);
        });
    }


    @Override
    public void goToLevel() {
        if (!_isLevelGoTo()) {
            String msg = String.format("当前楼层不是第一层无法跳转, stageLevel=%s!", target.getStageLevel());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        int lvGotoLevel = _tryGetLvGotoLevel();
        int vipLvGotoLevel = _tryGetVipLvGotoLevel();
        _saveGoToLevel(Math.max(lvGotoLevel, vipLvGotoLevel));
        List<Integer> allRewardStageIds = UltimateTestCtrlUtils.getAllRewardStageIds(target.getStageLevel());
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        List<IdMaptoCount> idMaptoCountList = UltimateTestCtrlUtils.getAllReward(allRewardStageIds, idMaptoCount, getPlayerCtrl().getCurLevel());
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(Sm_UltimateTest.Action.RESP_GO_TO_LEVEL).addItem(idMaptoCount);
        SenderFunc.sendInner(this, Sm_UltimateTest.class, Sm_UltimateTest.Builder.class, Sm_UltimateTest.Action.RESP_GO_TO_LEVEL, (b, br) -> {
            b.setIdMapToCountList(ProtoUtils.create_Sm_Common_IdMaptoCountList(idMaptoCountList));
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
            b.setInfo(UltimateTestCtrlProtos.create_Sm_UltimateTest_Info(target));
        });
        save();
    }

    @Override
    public void changeIcon(int icon) {
        LogicCheckUtils.validateParam(Integer.class, icon);
        if (!herosCtrl.containsHeroByTpId(icon)) {
            String msg = String.format("尚未获得该武将，无法用其形象, icon=%s!", icon);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        target.setIcon(icon);
        SenderFunc.sendInner(this, Sm_UltimateTest.class, Sm_UltimateTest.Builder.class, Action.RESP_CHANGE_ICON, (b, br) -> {
            b.setIcon(icon);
        });
        save();
    }

    /*
============================================================以下为内部方法==============================================================
 */


    /**
     * 保存格斗家信息
     *
     * @param heroes
     * @param isWin
     */
    private void _saveHeroInfo(List<UltimateTestHero> heroes, boolean isWin) {
        for (UltimateTestHero hero : heroes) {
            target.getIdToHeros().put(hero.getHeroId(), hero);
        }
        if (isWin) {
            List<UltimateTestHero> pojoHeroes = new ArrayList<>(target.getIdToHeros().values());
            sortByBattleValue(pojoHeroes);
            addAnger(pojoHeroes);
        }
    }

    /**
     * 给格斗家增加怒气
     *
     * @param pojoHeroes
     */
    private void addAnger(List<UltimateTestHero> pojoHeroes) {
        for (int i = 0; i < MagicNumbers.ULTIMATE_WIN_HERO_COUNT && i < pojoHeroes.size(); i++) {
            UltimateTestHero hero = pojoHeroes.get(i);
            hero.setAnger(hero.getAnger() + MagicNumbers.ULTIMATE_WIN_ANGER);
        }
    }

    /**
     * 按照战力排序
     *
     * @param pojoHeroes
     */
    private void sortByBattleValue(List<UltimateTestHero> pojoHeroes) {
        Collections.sort(pojoHeroes, (class1, class2) -> {
            Long battleValue1 = class1.getBattleValue();
            Long battleValue2 = class2.getBattleValue();
            return battleValue1.compareTo(battleValue2);
        });
    }

    /**
     * proto转试炼塔hero
     *
     * @param heroInfos
     * @return
     */
    private List<UltimateTestHero> protoUltimateTestHeroParsToPojo(List<Sm_UltimateTest_Hero_Info> heroInfos) {
        List<UltimateTestHero> heroes = new ArrayList<>();
        for (Sm_UltimateTest_Hero_Info heroInfo : heroInfos) {
            UltimateTestHero hero = new UltimateTestHero(heroInfo.getHeroId(), heroInfo.getAnger(), heroInfo.getHp());
            long battleValue = herosCtrl.getAttrsContainer().getHeroBattleValue(hero.getHeroId());
            hero.setBattleValue(battleValue);
            heroes.add(hero);
        }
        return heroes;
    }


    private void _removeBuffFromCache(UltimateTestBuffIndexTypeEnum buffIndex) {
        target.getBuffIndexAndBuff().remove(buffIndex);
    }


    /**
     * 尝试获取玩家VIP等级可以跳转的最高层
     *
     * @return
     */
    private int _tryGetVipLvGotoLevel() {
        if (_isPlayerVipLvCanGoto()) {
            return AllServerConfig.UltimateTest_MopUp_VipLevel.getConfig();
        }
        return 0;
    }


    /**
     * 尝试获取玩家等级可以跳转的最高层
     *
     * @return
     */
    private int _tryGetLvGotoLevel() {

        return _getPlayerLvCanGotoLevel();
    }

    /**
     * 获取玩家等级可以跳转的楼层
     *
     * @return
     */
    private int _getPlayerLvCanGotoLevel() {
        int gotoLevel = 0;
        TupleListCell<Integer> playerLvToLevel = AllServerConfig.UltimateTest_MopUp_PlayerLvToLevel.getConfig();
        for (TupleCell<Integer> tupleCell : playerLvToLevel.getAll()) {
            if (getPlayerCtrl().getCurLevel() > tupleCell.get(TupleCell.FIRST)) {
                gotoLevel = tupleCell.get(TupleCell.SECOND);
            }
        }
        int yestodayHighLevel = target.getHistoryHighLevel() - MagicNumbers.ULTIMATE_MATH_YESTODAY_HIGHT_LEVEL;
        return yestodayHighLevel <= gotoLevel ? gotoLevel : yestodayHighLevel;
    }


    /**
     * 玩家VIP等级是否可以跳转
     *
     * @return
     */
    private boolean _isPlayerVipLvCanGoto() {
        List<Table_Vip_Row> vipRowList = RootTc.get(Table_Vip_Row.class).values();
        List<Integer> canGotoVipLvList = _getCanGotoVipLvList(vipRowList);
        for (Integer vipLv : canGotoVipLvList) {
            if (getPlayerCtrl().getTarget().getPayment().getVipLevel() >= vipLv) {
                int vipGotoLevel = AllServerConfig.UltimateTest_MopUp_VipLevel.getConfig();
                return target.getHistoryHighLevel() >= vipGotoLevel;
            }
        }
        return false;
    }

    /**
     * 获取可以跳转楼层的vip等级
     *
     * @param vipRowList
     * @return
     */
    private List<Integer> _getCanGotoVipLvList(List<Table_Vip_Row> vipRowList) {
        List<Integer> canGotoVipLvList = new ArrayList<>();
        for (Table_Vip_Row row : vipRowList) {
            if (row.getFinalSweepFloor() != 0) {
                canGotoVipLvList.add(row.getvIPLv());
            }
        }
        return canGotoVipLvList;
    }

    /**
     * 尝试随机敌人
     */
    private void _tryRandomEnemy() {
        if (!UltimateTestCtrlUtils.hasEnemy(target.getHardLevelToEnemies())) {
            int monsterMaxGrade = herosCtrl.maxColorOfAllHeros().getNumber();
            LOGGER.debug("准备随机敌人，当前关卡={}", target.getStageLevel());
            List<UltimatetestEnemy> enemies = randomEnemies(target.getStageLevel(), monsterMaxGrade);
            LOGGER.debug("随机敌人完毕，准备缓存，敌人列表={}", ToStringBuilder.reflectionToString(enemies, ToStringStyle.MULTI_LINE_STYLE));
            _cacheEnemies(enemies);
            save();
        }
    }

    /**
     * 尝试随机buff层信息
     */
    private void _tryRandomBuff() {
        Map<UltimateTestBuffIndexTypeEnum, UltimatetestBuff> buffTypeAndBuff = UltimateTestCtrlUtils.randomBuff(target.getStageLevel());
        _saveBuffTypeAndBuff(buffTypeAndBuff);
    }

    /**
     * 缓存敌人
     *
     * @param enemies
     */
    private void _cacheEnemies(List<UltimatetestEnemy> enemies) {
        target.getHardLevelToEnemies().clear();
        for (UltimatetestEnemy enemy : enemies) {
            target.getHardLevelToEnemies().put(enemy.getHardTypeEnum(), enemy);
        }
    }


    /**
     * 随机敌人
     *
     * @param stageLevel
     * @return
     */
    private List<UltimatetestEnemy> randomEnemies(int stageLevel, int monsterMaxGrade) {
        List<UltimatetestEnemy> enemies = new ArrayList<>();
        for (HardTypeEnum hardTypeEnum : HardTypeEnum.values()) {
            if (hardTypeEnum != HardTypeEnum.NULL) {
                enemies.add(UltimateTestCtrlUtils.createEnemy(stageLevel, hardTypeEnum, monsterMaxGrade));
            }
        }
        return enemies;
    }

    private int _getBuffPirce(UltimateTestBuffIndexTypeEnum buffIndex) {
        return target.getBuffIndexAndBuff().get(buffIndex).getConsum();
    }

    private boolean _hasBuffType(UltimateTestBuffIndexTypeEnum buffIndex) {
        return target.getBuffIndexAndBuff().containsKey(buffIndex);
    }

    private void _saveBuffTypeAndBuff(Map<UltimateTestBuffIndexTypeEnum, UltimatetestBuff> buffTypeAndBuff) {
        target.getBuffIndexAndBuff().clear();
        for (Entry<UltimateTestBuffIndexTypeEnum, UltimatetestBuff> entry : buffTypeAndBuff.entrySet()) {
            target.getBuffIndexAndBuff().put(entry.getKey(), entry.getValue());
        }
    }

    private void _saveOpenBox(int stageLevel) {
        int times = _getOpenBoxTimes(stageLevel);
        target.getStageLevelToTimes().put(stageLevel, times + 1);
    }


    private void _saveOpenAllBoxByTimes(List<Integer> allRewardStageIds, int times) {
        for (Integer stageLevel : allRewardStageIds) {
            int hasOpenTimes = _getOpenBoxTimes(stageLevel);
            target.getStageLevelToTimes().put(stageLevel, hasOpenTimes + times);
        }
    }

    private List<IdMaptoCount> _openAllBox(List<Integer> allRewardStageIds, IdMaptoCount addRefresh) {
        List<IdMaptoCount> idMaptoCountList = new ArrayList<>();
        for (Integer stageLevel : allRewardStageIds) {
            IdMaptoCount dropIdMaptoCount = UltimateTestCtrlUtils.getBoxReward(stageLevel, getPlayerCtrl().getCurLevel());
            _addRefresh(addRefresh, dropIdMaptoCount, Sm_UltimateTest.Action.RESP_OPEN_ALL_BOX);
            idMaptoCountList.add(dropIdMaptoCount);
        }
        return idMaptoCountList;
    }

    private void _addRefresh(IdMaptoCount addRefresh, IdMaptoCount idMaptoCount, Action action) {
        addRefresh.addAll(itemIoExtp.getControlerForUpdate(action).addItem(idMaptoCount));
    }


    /**
     * 获取跳到当前关卡应该获取的所有的buffStar
     *
     * @param allAttackStageIds
     * @return
     */
    private int getBuffStarByAllAttackStageIds(List<Integer> allAttackStageIds) {
        int buffStar = 0;
        for (Integer stageId : allAttackStageIds) {
            buffStar += _mathBuffStar(MagicNumbers.ULTIMATE_TEST_MAX_STAR, stageId, HardTypeEnum.HARD);
        }
        return buffStar;
    }

    /**
     * 获取所有战斗关卡所得分数
     *
     * @param allAttackStageIds
     * @return
     */
    private int getScoreByAllAttackStageIds(List<Integer> allAttackStageIds) {
        int score = 0;
        for (Integer stageLevel : allAttackStageIds) {
            int baseScore = _getScoreByHardLevelandStageLevel(stageLevel, HardTypeEnum.HARD) * MagicNumbers.ULTIMATE_TEST_HARD_STAR;
            score += baseScore;
        }
        return score;
    }

    /**
     * 获取基础分数
     *
     * @param stageLevel
     * @param hardLevel
     * @return
     */
    private int _getScoreByHardLevelandStageLevel(int stageLevel, HardTypeEnum hardLevel) {
        TupleCell<Integer> tuple = AllServerConfig.UltimateTest_Base_Score.getConfig();
        int playerLevel = getPlayerCtrl().getCurLevel();
        int score = tuple.get(TupleCell.FIRST) + (playerLevel * stageLevel / tuple.get(TupleCell.SECOND));
        switch (hardLevel) {
            case EASY:
                return score * MagicNumbers.ULTIMATE_TEST_EASY_MULTI;
            case NORMAL:
                return (int) (score * MagicNumbers.ULTIMATE_TEST_NORMAL_MULTI);
            case HARD:
                return score * MagicNumbers.ULTIMATE_TEST_HARD_MULTI;
        }
        String msg = String.format("爬塔难度传入错误 stageLevel=%s,hardLevel=%s", stageLevel, hardLevel);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    private void _saveGoToLevel(int highLevel) {
        setStageLevel(highLevel);
        List<Integer> allAttackStageIds = UltimateTestCtrlUtils.getAllAttackStageLevels(target.getStageLevel());
        // 计算buffStar
        target.setBuffStar(getBuffStarByAllAttackStageIds(allAttackStageIds));
        // 当前积分计算
        target.setTestScore(getScoreByAllAttackStageIds(allAttackStageIds));
    }

    /**
     * 是否可以跳转
     *
     * @return
     */
    private boolean _isLevelGoTo() {
        //当前层数不是第一层，无法跳层
        if (target.getStageLevel() != MagicNumbers.DEFAULT_ONE) {
            return false;
        }
        //最高层数比当前层数低或者一样高，无法往底层跳
        return target.getHistoryTestScore() < target.getStageLevel();
    }


    private void _insertRank() {
        RedisRankUtils.insertRank(target.getTestScore(), getPlayerCtrl().getPlayerId(), getPlayerCtrl().getSimplePlayerAfterUpdate().getOutRealmId(), CommonRankTypeEnum.RK_ULTIMATE_TEST_POINT);
    }

    private void _reset() {
        target.setHistoryTestScore((int) (target.getHistoryTestScore() * MagicNumbers.ULTIMATE_PERCENTAGE_SCORE + target.getTestScore()));
        target.setBuffStar(MagicNumbers.DEFAULT_ZERO);
        target.setFlag(MagicNumbers.DEFAULT_ZERO);
        target.setTestScore(MagicNumbers.DEFAULT_ZERO);
        target.setStageLevel(MagicNumbers.DEFAULT_ONE);
        target.getHeroBuffIds().clear();
        target.getIdToHeros().clear();
        target.getHardLevelToEnemies().clear();
        target.getStageLevelToTimes().clear();
    }

    private void _saveSpeReward(int rewardScore) {
        target.getRewardScores().add(rewardScore);
    }

    private void _saveBuff(int buffId) {
        target.getHeroBuffIds().add(buffId);
    }

    private void _removeBuffStar(int buffPirce) {
        target.setBuffStar(target.getBuffStar() - buffPirce);
    }

    private boolean _canRemoveBuffStar(int buffPirce) {
        return target.getBuffStar() >= buffPirce;
    }


    private IdMaptoCount _deductionResourceByOpenAllBoxByTimes(List<Integer> allRewardStageIds, int times) {
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        for (int i = 0; i < times; i++) {
            idMaptoCount.addAll(_deductionResourceByOpenAllBox(allRewardStageIds));
        }
        return idMaptoCount;
    }

    private IdMaptoCount _deductionResourceByOpenAllBox(List<Integer> allRewardStageIds) {
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        for (Integer stageLevel : allRewardStageIds) {
            idMaptoCount.add(_deductionResource(stageLevel));
        }
        return idMaptoCount;
    }

    private IdAndCount _deductionResource(int stageLevel) {
        int times = _getOpenBoxTimes(stageLevel);
        int nextTimes = times + MagicNumbers.DEFAULT_ONE;
        return Table_Consume_Row.ultimateTestOpenBoxConSume(nextTimes);
    }

    private int _getOpenBoxTimes(int stageLevel) {
        if (!target.getStageLevelToTimes().containsKey(stageLevel)) {
            target.getStageLevelToTimes().put(stageLevel, MagicNumbers.DEFAULT_ZERO);
        }
        return target.getStageLevelToTimes().get(stageLevel);
    }


    private void _saveEnd(int addBuffstar, int addScore) {
        target.setHardLevel(HardTypeEnum.NULL);
        setStageLevel(UltimateTestCtrlUtils.getNextStageLevel(target.getStageLevel()));
        target.setFlag(MagicNumbers.DEFAULT_ZERO);
        target.setBuffStar(target.getBuffStar() + addBuffstar);
        target.setTestScore(target.getTestScore() + addScore);
        target.getHardLevelToEnemies().clear();
        if (target.getHistoryHighLevel() < target.getStageLevel()) {
            target.setHistoryHighLevel(target.getStageLevel());
        }
        int highestUltimateTestLevel = getPlayerCtrl().getSimplePlayerAfterUpdate().getUltimateTestLevel();
        if (target.getStageLevel() > highestUltimateTestLevel) {
            getPlayerCtrl().getSimplePlayerAfterUpdate().setUltimateTestLevel(target.getStageLevel());
        }

    }


    private void _saveBestResult(int stageLevel, HardTypeEnum hardTypeEnum, int star) {
        Map<HardTypeEnum, Integer> hardTypeToStar = RelationshipCommonUtils.getMapByKey(target.getStageLvToBestStar(), stageLevel);
        hardTypeToStar.putIfAbsent(hardTypeEnum, star);
    }

    private Integer _getBestResult(int stageLevel, HardTypeEnum hardTypeEnum) {
        Map<HardTypeEnum, Integer> hardTypeToStar = RelationshipCommonUtils.getMapByKey(target.getStageLvToBestStar(), stageLevel);
        return hardTypeToStar.get(hardTypeEnum) == null ? 0 : hardTypeToStar.get(hardTypeEnum);
    }

    /**
     * 计算获得buffstar
     *
     * @param star
     * @return
     */
    private int _mathBuffStar(int star, int stageLevel, HardTypeEnum hardLevel) {
        int tabBuffstar = UltimateTestCtrlUtils.getStarByHardLevelandStageLevel(stageLevel, hardLevel);
        return tabBuffstar * star;
    }


    /**
     * 结束战斗检查传入参数
     *
     * @param flag
     * @param stageId
     * @param hardLevel
     * @return
     */
    private boolean _isLegalArg(long flag, int stageId, HardTypeEnum hardLevel) {
        if (target.getFlag() != flag) {
            return false;
        }
        LogicCheckUtils.validateParam(HardTypeEnum.class, hardLevel);
        return target.getStageLevel() == stageId;
    }

    /**
     * 获取当前关卡
     *
     * @return
     */
    private int _getStageLevel() {
        if (_isFristTimeAttack()) {
            setStageLevel(UltimateTestCtrlUtils.getFirstStageId());
        }
        return target.getStageLevel();
    }

    private void _saveBegin(HardTypeEnum hardLevel) {
        target.setFlag(System.currentTimeMillis());
        target.setHardLevel(hardLevel);
    }

    /**
     * 是否是第一次挑战
     *
     * @return
     */
    private boolean _isFristTimeAttack() {
        return target.getStageLevel() == MagicNumbers.DEFAULT_ZERO;
    }

    /**
     * 是否有敌方阵容
     *
     * @param hardLevel
     * @return
     */
    private boolean _hasEnemy(HardTypeEnum hardLevel) {
        //阵容为空
        if (target.getHardLevelToEnemies() == null) {
            return false;
        }
        //阵容为空
        if (target.getHardLevelToEnemies().size() == MagicNumbers.DEFAULT_ZERO) {
            return false;
        }
        //阵容包含这个难度
        return target.getHardLevelToEnemies().containsKey(hardLevel);
    }

    /**
     * 获取试炼塔形象Icon
     *
     * @return
     */
    private int getIcon() {
        int icon = target.getIcon();
        if (icon <= 0) {
            icon = AllServerConfig.PlayerInit_Default_Hero_TpId_1.getConfig(); // 默认为初始武将Id
            target.setIcon(icon);//此出可以不存库
        }
        return icon;
    }


    private void setStageLevel(int stageLevel) {
        target.setStageLevel(stageLevel);
        statisticsDaliyAttackTimes();
    }

    private void statisticsDaliyAttackTimes() {
        Pr_NotifyMsg notifyMsg2 = new Pr_NotifyMsg(PrivateNotifyTypeEnum.UltimateTest_CompletFloorLevel, target.getStageLevel());
        sendPrivateMsg(notifyMsg2);
    }
}
