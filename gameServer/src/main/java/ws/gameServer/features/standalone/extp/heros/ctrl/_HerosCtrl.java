package ws.gameServer.features.standalone.extp.heros.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.dataCenter.enums.PrivateNotifyTypeEnum;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_NotifyMsg;
import ws.gameServer.features.standalone.extp.equipment.utils.EquipmentCtrlUtils;
import ws.gameServer.features.standalone.extp.formations.FormationsExtp;
import ws.gameServer.features.standalone.extp.formations.ctrl.FormationsCtrl;
import ws.gameServer.features.standalone.extp.heros.utils.HerosCtrlOnlineOfflineUtils;
import ws.gameServer.features.standalone.extp.heros.utils.HerosCtrlProtos;
import ws.gameServer.features.standalone.extp.heros.utils.HerosCtrlUtils;
import ws.gameServer.features.standalone.extp.itemBag.ItemBagExtp;
import ws.gameServer.features.standalone.extp.itemBag.ctrl.ItemBagCtrl;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.newBattle.NewBattleHeroContainer;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.EnumsProtos.ChatTypeEnum;
import ws.protos.EnumsProtos.ColorDetailTypeEnum;
import ws.protos.EnumsProtos.CommonRankTypeEnum;
import ws.protos.EnumsProtos.FormationTypeEnum;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.protos.EnumsProtos.SkillPositionEnum;
import ws.protos.EnumsProtos.SpecialItemTemplateIdEnum;
import ws.protos.EnumsProtos.WarSoulPositionEnum;
import ws.protos.HerosProtos.Sm_Heros;
import ws.protos.HerosProtos.Sm_Heros.Action;
import ws.protos.HerosProtos.Sm_Heros_SkillUpgradeInfo;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.MessageHandlerProtos.Response.Builder;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.chatServer.GroupChatMsg;
import ws.relationship.chatServer.SystemChatMsg;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_CardUpgradeQuality_Row;
import ws.relationship.table.tableRows.Table_CardUpgradeStar_Row;
import ws.relationship.table.tableRows.Table_Consume_Row;
import ws.relationship.table.tableRows.Table_Exp_Row;
import ws.relationship.table.tableRows.Table_Item_Row;
import ws.relationship.table.tableRows.Table_New_Card_Row;
import ws.relationship.table.tableRows.Table_SkillUpgradeConsumes_Row;
import ws.relationship.table.tableRows.Table_Vip_Row;
import ws.relationship.topLevelPojos.common.LevelUpObj;
import ws.relationship.topLevelPojos.formations.Formation;
import ws.relationship.topLevelPojos.formations.Formations;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.topLevelPojos.heros.Heros;
import ws.relationship.topLevelPojos.talent.Talent;
import ws.relationship.utils.BroadcastSystemMsgUtils;
import ws.relationship.utils.ProtoUtils;
import ws.relationship.utils.RedisRankUtils;
import ws.relationship.utils.attrs.HeroAttrsContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class _HerosCtrl extends AbstractPlayerExteControler<Heros> implements HerosCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_HerosCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;
    private ItemBagCtrl itemBagCtrl;
    private FormationsCtrl formationsCtrl;

    private HeroAttrsContainer attrsContainer = new HeroAttrsContainer();  // 武将属性缓存

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
        itemBagCtrl = getPlayerCtrl().getExtension(ItemBagExtp.class).getControlerForQuery();
        formationsCtrl = getPlayerCtrl().getExtension(FormationsExtp.class).getControlerForQuery();
    }

    @Override
    public void _initAfterChanged() throws Exception {
        Talent talent = getPlayerCtrl().getTopLevelPojo(Talent.class);
        Formations formations = getPlayerCtrl().getTopLevelPojo(Formations.class);
        Formation formation_Main = formations.getTypeToFormation().get(FormationTypeEnum.F_MAIN);
        attrsContainer = HerosCtrlUtils.createHeroAttrsContainer_FMain_AllHeros(target, talent, formation_Main);
        statisticsMainFormationBattleValue();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
        target.setDyUpHeroLvTs(MagicNumbers.DEFAULT_ZERO);
        target.setDyUpQualityTs(MagicNumbers.DEFAULT_ZERO);
        target.setDyUpSkillTs(MagicNumbers.DEFAULT_ZERO);
        target.setHasBuySkillPointTs(MagicNumbers.DEFAULT_ZERO);
        statisticsHasCollectHeroNum();
        statisticsHasCollectHeroNum();
    }


    @Override
    public void sync() {
        calcuSkillPoint();
        SenderFunc.sendInner(this, Sm_Heros.class, Sm_Heros.Builder.class, Action.RESP_SYNC, (b, br) -> {
            b.setSkillPoint(target.getSkillPoint());
            b.setLastSettleTime(target.getLastSettleTime());
            b.setHasBuySkillPointTimes(target.getHasBuySkillPointTs());
            b.addAllHeros(ProtoUtils.create_Sm_Common_Hero_Lis(target.getIdToHero()));
        });
        save();
    }

    @Override
    public void refreshItem(IdMaptoCount idMaptoCount) {
        Response.Builder br = ProtoUtils.create_Response(Code.Sm_Heros, Sm_Heros.Action.RESP_SYNC_PART);
        Sm_Heros.Builder b = HerosCtrlProtos.create_Sm_Heros_Dynamic(this, idMaptoCount);
        br.setResult(true);
        br.setSmHerosSyncPart(b);
        send(br.build());
    }

    @Override
    public void refreshItemAddToResponse(IdMaptoCount idMaptoCount, Builder br) {
        br.setSmHerosSyncPart(HerosCtrlProtos.create_Sm_Heros_Dynamic(this, idMaptoCount));
    }

    @Override
    public boolean canAddHeros(IdMaptoCount idMaptoCount) {
        return RootTc.has(Table_New_Card_Row.class, idMaptoCount.getAllKeys());
    }

    @Override
    public IdMaptoCount addHeros(IdMaptoCount idMaptoCount) {
        IdMaptoCount refresh_IdMaptoCount = new IdMaptoCount();
        idMaptoCount.getAll().forEach(idAndCount -> {
            Integer heroTemplateId = idAndCount.getId();
            for (int i = 0; i < idAndCount.getCount(); i++) {
                if (containsHeroByTpId(heroTemplateId)) { // 已经有武将了，转为碎片
                    IdMaptoCount toRefresh = addConvertHeroFragments(heroTemplateId);
                    refresh_IdMaptoCount.addAll(toRefresh);
                } else {
                    Hero hero = HerosCtrlUtils.addHero(target, heroTemplateId);
                    refresh_IdMaptoCount.add(new IdAndCount(hero.getId())); // 武将自身
                    statisticsHasCollectHeroNum();
                    attrsContainer.onAddNewHero(hero);
                    List<Hero> doneYokeHeroes = HerosCtrlUtils.addHeroMakeYokeDoneHeros(hero, target.getIdToHero());
                    attrsContainer.onSomeHeroYokeChange(doneYokeHeroes);
                    broadcastSystemMsg_GetHero(hero);

                }
            }
        });
        save();
        return refresh_IdMaptoCount;
    }

    @Override
    public boolean canRemoveHeros(IdMaptoCount idMaptoCount) {
        for (Integer heroId : idMaptoCount.getAllKeys()) {
            if (!containsHeroById(heroId)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public IdMaptoCount removeHeros(IdMaptoCount idMaptoCount) {
        IdMaptoCount refresh_IdMaptoCount = new IdMaptoCount();
        for (Integer heroId : idMaptoCount.getAllKeys()) {
            if (containsHeroById(heroId)) {
                target.getIdToHero().remove(heroId);
                refresh_IdMaptoCount.add(new IdAndCount(heroId));
            }
        }
        save();
        return refresh_IdMaptoCount;
    }

    @Override
    public void upgradeLevel(int heroId, List<IdAndCount> consumeTpIds) {
        if (consumeTpIds.size() <= 0) {
            String msg = String.format("consumeTpIds=%s，升级武将等级消耗材料列表不能为空！", consumeTpIds);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        check_ItemCanUseAsHeroExpItem(consumeTpIds);
        IdAndCount self = new IdAndCount(heroId);
        IdMaptoCount expItems_Consumes = new IdMaptoCount().addAll(consumeTpIds);
        LogicCheckUtils.canRemove(itemIoCtrl, new IdMaptoCount().add(self).addAll(expItems_Consumes));
        Hero hero = getHero(heroId);
        int oldLevel = hero.getLv();
        int playerLevel = getPlayerCtrl().getCurLevel();
        if (oldLevel > playerLevel) {
            String msg = String.format("heroId=%s，当前等级为=%s 玩家角色等级为=%s 不能继续升级！", heroId, oldLevel, playerLevel);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        long expOffered = HerosCtrlUtils.calcuSumItemExp(consumeTpIds, (row) -> row.getCardExp());
        HerosCtrlUtils.upgradeHeroLevel(hero, expOffered, playerLevel);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_UP_LV).removeItem(new IdMaptoCount().addAll(expItems_Consumes));
        SenderFunc.sendInner(this, Sm_Heros.class, Sm_Heros.Builder.class, Action.RESP_UP_LV, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1).add(self), br);
        });
        target.setDyUpHeroLvTs(target.getDyUpHeroLvTs() + (hero.getLv() - oldLevel));
        int newLevel = hero.getLv();
        if (newLevel > oldLevel) { // 更新属性
            attrsContainer.onOneHeroBaseChange(hero);
        }
        save();
        statisticsDyUpHeroLvTs();
    }


    @Override
    public void addExpToHero(int heroId, long expOffered) {
        if (expOffered <= 0) {
            return;
        }
        if (!containsHeroById(heroId)) {
            LOGGER.debug("给武将={},添加经验时，该武将不存在，忽略!", heroId);
            return;
        }
        Hero hero = getHero(heroId);
        int oldLevel = hero.getLv();
        int playerLevel = getPlayerCtrl().getCurLevel();
        if (oldLevel > playerLevel) {
            return;
        }
        HerosCtrlUtils.upgradeHeroLevel(hero, expOffered, playerLevel);
        int newLevel = hero.getLv();
        if (newLevel > oldLevel) { // 更新属性
            attrsContainer.onOneHeroBaseChange(hero);
        }
        save();
    }


    @Override
    public void upgradeQualityLevel(int heroId) {
        IdAndCount self = new IdAndCount(heroId);
        LogicCheckUtils.canRemove(itemIoCtrl, new IdMaptoCount().add(self));
        Hero hero = getHero(heroId);
        int tpId = hero.getTpId();
        int heroLv = hero.getLv();
        ColorDetailTypeEnum oldQualityLv = ColorDetailTypeEnum.valueOf(hero.getQualityLv());
        ColorDetailTypeEnum maxQualityLv = EquipmentCtrlUtils.maxColor();
        if (oldQualityLv.getNumber() >= maxQualityLv.getNumber()) {
            String msg = String.format("heroId=%s，已经达到最大品级=%s 不能继续升品！", heroId, oldQualityLv);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        ColorDetailTypeEnum newQualityLv = EquipmentCtrlUtils.nextColor(oldQualityLv);
        int needHeroLv = Table_CardUpgradeQuality_Row.getUpgradeNeedHeroLv(tpId, newQualityLv);
        if (heroLv < needHeroLv) {
            String msg = String.format("heroId=%s，当前等级为=%s 升品需求等级=%s 不能继续升品！", heroId, heroLv, needHeroLv);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdMaptoCount idMaptoCount_Consumes = Table_CardUpgradeQuality_Row.getUpgradeConsumes(tpId, newQualityLv);
        LogicCheckUtils.canRemove(itemIoCtrl, new IdMaptoCount().addAll(idMaptoCount_Consumes));
        hero.setQualityLv(newQualityLv.getNumber());
        HerosCtrlUtils.unlockSpirit(hero);// 解锁战魂
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_UP_QUALITY_LV).removeItem(new IdMaptoCount().addAll(idMaptoCount_Consumes));
        SenderFunc.sendInner(this, Sm_Heros.class, Sm_Heros.Builder.class, Action.RESP_UP_QUALITY_LV, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1).add(self), br);
        });
        statisticsHeroQuality(oldQualityLv, newQualityLv);
        target.setDyUpQualityTs(target.getDyUpQualityTs() + (newQualityLv.getNumber() - oldQualityLv.getNumber()));
        attrsContainer.onOneHeroBaseChange(hero);
        save();
        statisticsDyUpQualityTs();
    }

    @Override
    public void useUniversalFragments(int heroId, int num) {
        if (num <= 0) {
            String msg = String.format("num=%s，参数非法！", num);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!containsHeroById(heroId)) {
            String msg = String.format("必须拥有heroId=%s 才能使用万能碎片! num=%s ", heroId, num);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdAndCount self = new IdAndCount(heroId);
        IdAndCount consume = new IdAndCount(SpecialItemTemplateIdEnum.SPE_UNIVERSAL_FRAGMENT_VALUE, num);
        LogicCheckUtils.canRemove(itemIoCtrl, new IdMaptoCount().add(self).add(consume));
        Hero hero = getHero(heroId);
        int tpId = hero.getTpId();
        Table_New_Card_Row cardRow = RootTc.get(Table_New_Card_Row.class, tpId);
        IdAndCount add = new IdAndCount(cardRow.getCardPiece(), num);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_USE_UNIVERSAL_FRAG).removeItem(consume);
        IdMaptoCount refresh_2 = itemIoExtp.getControlerForUpdate(Action.RESP_USE_UNIVERSAL_FRAG).addItem(add);
        SenderFunc.sendInner(this, Sm_Heros.class, Sm_Heros.Builder.class, Action.RESP_USE_UNIVERSAL_FRAG, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1).addAll(refresh_2), br);
        });
        save();
    }

    @Override
    public void upgradeStarLevel(int heroId) {
        IdAndCount self = new IdAndCount(heroId);
        LogicCheckUtils.canRemove(itemIoCtrl, new IdMaptoCount().add(self));
        Hero hero = getHero(heroId);
        int tpId = hero.getTpId();
        int oldStarLv = hero.getStarLv();
        int maxStarLv = AllServerConfig.Heros_Max_Star_Lv.getConfig();
        if (oldStarLv >= maxStarLv) {
            String msg = String.format("heroId=%s，已经达到最大星级=%s 不能继续升星！", heroId, oldStarLv);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        int newStarLv = oldStarLv + 1;
        IdMaptoCount idMaptoCount_Consumes = Table_CardUpgradeStar_Row.getUpgradeConsumes(tpId, oldStarLv);
        LogicCheckUtils.canRemove(itemIoCtrl, new IdMaptoCount().addAll(idMaptoCount_Consumes));
        hero.setStarLv(newStarLv);
        HerosCtrlUtils.unlockSkill(hero); // 触发解锁技能
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_UP_STAR_LV).removeItem(idMaptoCount_Consumes);
        SenderFunc.sendInner(this, Sm_Heros.class, Sm_Heros.Builder.class, Action.RESP_UP_STAR_LV, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1).add(self), br);
        });
        attrsContainer.onOneHeroBaseChange(hero);
        save();
        broadcastSystemMsg_StarLvUp(hero);
    }

    @Override
    public void buySkillPoint() {
        // 任何时候都可以购买，有消耗和次数控制
        int curVipLv = getPlayerCtrl().getCurVipLevel();
        int maxBuySkillTs = Table_Vip_Row.getVipRow(curVipLv).getBuySkillPointTimes();
        if (target.getHasBuySkillPointTs() >= maxBuySkillTs) {
            String msg = String.format("今日购买技能点的次数已满，不能继续购买！curVipLv=%s hasBuySkillTs=%s", curVipLv, target.getHasBuySkillPointTs());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        calcuSkillPoint();
        int nextHasBuySkillPointTs = target.getHasBuySkillPointTs() + 1;
        int oneTimeBuyGetSkillPoint = AllServerConfig.Heros_OneTimeBuy_GetSkillPoint.getConfig();
        IdAndCount reduce = Table_Consume_Row.buySkillPointConsume(nextHasBuySkillPointTs);
        LogicCheckUtils.canRemove(itemIoCtrl, reduce);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_BUY_SKILL_POINT).removeItem(reduce);
        target.setSkillPoint(target.getSkillPoint() + oneTimeBuyGetSkillPoint);
        target.setHasBuySkillPointTs(nextHasBuySkillPointTs);
        SenderFunc.sendInner(this, Sm_Heros.class, Sm_Heros.Builder.class, Action.RESP_UP_SKILL_LV, (b, br) -> {
            b.setSkillPoint(target.getSkillPoint());
            b.setLastSettleTime(target.getLastSettleTime());
            b.setHasBuySkillPointTimes(target.getHasBuySkillPointTs());
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1), br);
        });
        save();
    }

    @Override
    public void upgradeSkillLevel(int heroId, List<Sm_Heros_SkillUpgradeInfo> skillUpgradeInfos) {
        calcuSkillPoint();
        IdAndCount self = new IdAndCount(heroId);
        LogicCheckUtils.canRemove(itemIoCtrl, new IdMaptoCount().add(self));
        Hero hero = getHero(heroId);
        check_SkillUpgradeInfo(skillUpgradeInfos, hero);
        int needPoint = check_SkillUpgradeInfo_SkillPoint(skillUpgradeInfos);
        IdMaptoCount consumes = check_SkillUpgradeInfo_Consumes(skillUpgradeInfos, hero);
        target.setSkillPoint(target.getSkillPoint() - needPoint);
        _upgradeSkill(skillUpgradeInfos, hero);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_UP_SKILL_LV).removeItem(consumes);
        SenderFunc.sendInner(this, Sm_Heros.class, Sm_Heros.Builder.class, Action.RESP_UP_SKILL_LV, (b, br) -> {
            b.setSkillPoint(target.getSkillPoint());
            b.setLastSettleTime(target.getLastSettleTime());
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1).add(self), br);
        });
        attrsContainer.onOneHeroSkillChange(hero);
        save();
        statisticsDyUpSkillTs();
    }

    @Override
    public void upgradeWarSoulLevel(int heroId, WarSoulPositionEnum soulPos, List<IdAndCount> consumeTpIds) {
        LogicCheckUtils.requireNonNull(soulPos, WarSoulPositionEnum.class);
        if (consumeTpIds.size() <= 0) {
            String msg = String.format("consumeTpIds=%s，升级战魂消耗材料列表不能为空！", consumeTpIds);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        Hero hero = getHero(heroId);
        IdAndCount self = new IdAndCount(heroId);
        LogicCheckUtils.canRemove(itemIoCtrl, self);
        check_IsWarSoulHasUnlock(hero, soulPos);
        check_ItemCanUseAsWarSoulExpItem(consumeTpIds);
        LevelUpObj oldLvObj = getFightingSpiritTypeLevel(hero, soulPos);
        int maxWarSoulLv = AllServerConfig.Heros_Max_WarSoul_Lv.getConfig();
        if (oldLvObj.getLevel() >= maxWarSoulLv) {
            String msg = String.format("heroId=%s，soulPos=%s lvObj=%s当前已经达到最大等级不可继续升级！", heroId, soulPos, oldLvObj);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdMaptoCount expItems_Consumes = new IdMaptoCount(consumeTpIds);
        LogicCheckUtils.canRemove(itemIoCtrl, expItems_Consumes);
        int consumeRatio = AllServerConfig.Heros_WarSoul_Lv_Up_Consume_Ratio.getConfig();
        long expOffered = HerosCtrlUtils.calcuSumItemExp(consumeTpIds, (row) -> row.getWarSoulExp());
        IdAndCount money_Consumes = new IdAndCount(ResourceTypeEnum.RES_MONEY_VALUE, (int) expOffered * consumeRatio);
        LogicCheckUtils.canRemove(itemIoCtrl, money_Consumes);
        HerosCtrlUtils.upgradeHeroWarSoulLevel(soulPos, oldLvObj, expOffered, maxWarSoulLv);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_UP_SOUL_LV).removeItem(new IdMaptoCount().addAll(expItems_Consumes).add(money_Consumes));
        SenderFunc.sendInner(this, Sm_Heros.class, Sm_Heros.Builder.class, Action.RESP_UP_SOUL_LV, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1).add(self), br);
        });
        LevelUpObj newLvObj = getFightingSpiritTypeLevel(hero, soulPos);
        if (newLvObj.getLevel() > oldLvObj.getLevel()) {
            attrsContainer.onOneHeroWarSoulChange(hero);
        }
        save();
    }

    @Override
    public void vipMoneyUpgradeFightingSpiritLevel(int heroId, WarSoulPositionEnum soulPos) {
        LogicCheckUtils.requireNonNull(soulPos, WarSoulPositionEnum.class);
        Hero hero = getHero(heroId);
        IdAndCount self = new IdAndCount(heroId);
        LogicCheckUtils.canRemove(itemIoCtrl, self);
        check_IsWarSoulHasUnlock(hero, soulPos);
        LevelUpObj oldLvObj = getFightingSpiritTypeLevel(hero, soulPos);
        int maxWarSoulLv = AllServerConfig.Heros_Max_WarSoul_Lv.getConfig();
        if (oldLvObj.getLevel() >= maxWarSoulLv) {
            String msg = String.format("heroId=%s，soulPos=%s lvObj=%s当前已经达到最大等级不可继续升级！", heroId, soulPos, oldLvObj);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        long needExp = Table_Exp_Row.getWarSoulUpgradeNeedExp(soulPos, oldLvObj.getLevel()) - oldLvObj.getOvfExp();
        if (needExp <= 0) {
            String msg = String.format("heroId=%s，soulPos=%s lvObj=%s当前等级已经满经验，不可钻石升级！", heroId, soulPos, oldLvObj);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        TupleCell<Integer> ratio = AllServerConfig.Heros_WarSoul_BuyExpRatio.getConfig();
        long needVipMoney = (needExp * ratio.get(TupleCell.SECOND)) / ratio.get(TupleCell.FIRST);
        IdAndCount vipMoneyConsumes = new IdAndCount(ResourceTypeEnum.RES_VIPMONEY_VALUE, needVipMoney);
        LogicCheckUtils.canRemove(itemIoCtrl, vipMoneyConsumes);
        HerosCtrlUtils.upgradeHeroWarSoulLevel(soulPos, oldLvObj, needExp, maxWarSoulLv);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_VIPMONEY_UP_SOUL_LV).removeItem(new IdMaptoCount().add(vipMoneyConsumes));
        SenderFunc.sendInner(this, Sm_Heros.class, Sm_Heros.Builder.class, Action.RESP_VIPMONEY_UP_SOUL_LV, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1).add(self), br);
        });
        LevelUpObj newLvObj = getFightingSpiritTypeLevel(hero, soulPos);
        if (newLvObj.getLevel() > oldLvObj.getLevel()) {
            attrsContainer.onOneHeroWarSoulChange(hero);
        }
        save();
    }

    @Override
    public boolean containsHeroById(int heroId) {
        return target.getIdToHero().containsKey(heroId);
    }

    @Override
    public boolean containsHeroByTpId(int heroTpId) {
        for (Hero hero : target.getIdToHero().values()) {
            if (hero.getTpId() == heroTpId) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Hero getHero(int heroId) {
        return target.getIdToHero().get(heroId);
    }

    @Override
    public Hero getHeroByTpId(int heroTpId) {
        for (Hero hero : target.getIdToHero().values()) {
            if (hero.getTpId() == heroTpId) {
                return hero;
            }
        }
        return null;
    }

    @Override
    public HeroAttrsContainer getAttrsContainer() {
        return attrsContainer;
    }


    @Override
    public void onBroadcastEachHour(int hour) {
        statisticsMainFormationBattleValue();
    }

    @Override
    public NewBattleHeroContainer onQueryBattleHeroContainerInFormation(FormationTypeEnum type) {
        NewBattleHeroContainer container = HerosCtrlOnlineOfflineUtils.createFormationBattleHeroContainer_Online(type, getPlayerCtrl());
        return container;
    }

    @Override
    public ColorDetailTypeEnum maxColorOfAllHeros() {
        int max = ColorDetailTypeEnum.COLOR_WHITE_VALUE;
        for (Hero hero : target.getIdToHero().values()) {
            max = Math.max(hero.getQualityLv(), max);
        }
        return ColorDetailTypeEnum.valueOf(max);
    }

    /**
     * 获取技能等级
     *
     * @param hero
     * @param skillPos
     * @return
     */
    private int getSkillLevel(Hero hero, SkillPositionEnum skillPos) {
        if (hero.getSkills().containsKey(skillPos)) {
            return hero.getSkills().get(skillPos);
        }
        return MagicNumbers.DefaultValue_Skill_Level;
    }

    /**
     * 获取战魂等级
     *
     * @param hero
     * @param soulPositionEnum
     * @return
     */
    private LevelUpObj getFightingSpiritTypeLevel(Hero hero, WarSoulPositionEnum soulPositionEnum) {
        if (hero.getSouls().containsKey(soulPositionEnum)) {
            return hero.getSouls().get(soulPositionEnum);
        }
        return null;
    }

    /**
     * 向背包添加整卡转换后的碎片
     *
     * @param herotpId
     * @return
     */
    private IdMaptoCount addConvertHeroFragments(int herotpId) {
        Table_New_Card_Row cardRow = RootTc.get(Table_New_Card_Row.class, herotpId);
        return itemIoCtrl.addItem(new IdAndCount(cardRow.getCardPiece(), cardRow.getConvertNumber()));
    }

    /**
     * 结算技能点
     */
    private void calcuSkillPoint() {
        int maxSkillPoint = AllServerConfig.Heros_Max_Skill_Point.getConfig();
        long lastSettleTime = target.getLastSettleTime();
        long curTime = System.currentTimeMillis();
        target.setLastSettleTime(curTime);
        if (target.getSkillPoint() >= maxSkillPoint) {
            return;
        }
        if (lastSettleTime >= curTime) {
            return;
        }
        Table_Vip_Row vipRow = Table_Vip_Row.getVipRow(getPlayerCtrl().getTarget().getPayment().getVipLevel());
        int regainTime = vipRow.getSkillReply() * 1000; // 单位毫秒
        int point = (int) ((System.currentTimeMillis() - lastSettleTime) / regainTime);
        long remainTime = (System.currentTimeMillis() - lastSettleTime) % regainTime;
        target.setSkillPoint(Math.min(target.getSkillPoint() + point, maxSkillPoint));
        target.setLastSettleTime(curTime - remainTime);
    }


    /**
     * 升级技能
     *
     * @param skillUpgradeInfos
     * @param hero
     */
    private void _upgradeSkill(List<Sm_Heros_SkillUpgradeInfo> skillUpgradeInfos, Hero hero) {
        for (Sm_Heros_SkillUpgradeInfo info : skillUpgradeInfos) {
            SkillPositionEnum skillPos = info.getSkillPos();
            int oldSkillLv = getSkillLevel(hero, skillPos);
            int newSkillLv = oldSkillLv + info.getTimes();
            hero.getSkills().put(skillPos, newSkillLv);
            target.setDyUpSkillTs(target.getDyUpSkillTs() + (newSkillLv - oldSkillLv));
        }
    }


    /**
     * 获取技能类型解锁需要的武将星级
     *
     * @param skillPosition
     * @return
     */
    private int skillPosNeedHeroStarLv(SkillPositionEnum skillPosition) {
        TupleListCell<Integer> tupleListCell = AllServerConfig.Heros_Skill_LvUp_NeedStar.getConfig();
        IdMaptoCount idMaptoCount = IdMaptoCount.parse(tupleListCell);
        int star = idMaptoCount.getIntCount(skillPosition.getNumber());
        if (star <= 0) {
            String msg = String.format("无法获得 skillPosition=%s 解锁需要的星级！ 配置为：%s ", skillPosition, tupleListCell);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        return star;
    }


    //================================================== check =========================================

    /**
     * 检查是否是经验道具
     *
     * @param consumeTpIds
     */
    private void check_ItemCanUseAsHeroExpItem(List<IdAndCount> consumeTpIds) {
        for (IdAndCount idAndCount : consumeTpIds) {
            Table_Item_Row row = RootTc.get(Table_Item_Row.class, idAndCount.getId());
            if (row == null) {
                String msg = String.format("所有消耗品=%s 其中id=%s,非道具！", consumeTpIds, idAndCount.getId());
                throw new BusinessLogicMismatchConditionException(msg);
            }
            if (row.getCardExp() <= 0) {
                String msg = String.format("所有消耗品=%s 其中id=%s,非卡牌经验道具！", consumeTpIds, idAndCount.getId());
                throw new BusinessLogicMismatchConditionException(msg);
            }
        }
    }

    /**
     * 检查技能升级参数
     *
     * @param skillUpgradeInfos
     * @param hero
     */
    private void check_SkillUpgradeInfo(List<Sm_Heros_SkillUpgradeInfo> skillUpgradeInfos, Hero hero) {
        for (Sm_Heros_SkillUpgradeInfo info : skillUpgradeInfos) {
            SkillPositionEnum skillPos = info.getSkillPos();
            LogicCheckUtils.requireNonNull(skillPos, SkillPositionEnum.class);
            if (info.getTimes() <= 0) {
                String msg = String.format("升级次数times=%s，必须大于0！ skillUpgradeInfos=%s", info.getTimes(), skillUpgradeInfos);
                throw new BusinessLogicMismatchConditionException(msg);
            }
            int oldSkillLv = getSkillLevel(hero, skillPos);
            int heroLv = hero.getLv();
            int starLv = hero.getStarLv();
            if (oldSkillLv >= heroLv) {
                String msg = String.format("heroId=%s，skillPos=%s, 已经达到英雄的等级=%s，不能再升级技能！", hero.getTpId(), skillPos, oldSkillLv);
                throw new BusinessLogicMismatchConditionException(msg);
            }
            if ((oldSkillLv + info.getTimes()) > heroLv) {
                String msg = String.format("heroId=%s，skillPos=%s, heroLv=%s, oldSkillLv=%s, times=%s ,(oldSkillLv + times 不能大于heroLv)！", hero.getTpId(), skillPos, heroLv, oldSkillLv, info.getTimes());
                throw new BusinessLogicMismatchConditionException(msg);
            }
            int needStarLv = skillPosNeedHeroStarLv(skillPos);
            if (needStarLv > starLv) {
                String msg = String.format("heroId=%s，skillPos=%s, 需求星级=%s，当前星级=%s，不能升级技能！", hero.getTpId(), skillPos, needStarLv, starLv);
                throw new BusinessLogicMismatchConditionException(msg);
            }
        }
    }


    /**
     * 检查技能升级参数需要的技能点数
     *
     * @param skillUpgradeInfos
     */
    private int check_SkillUpgradeInfo_SkillPoint(List<Sm_Heros_SkillUpgradeInfo> skillUpgradeInfos) {
        int sumTimes = 0;
        for (Sm_Heros_SkillUpgradeInfo info : skillUpgradeInfos) {
            sumTimes += info.getTimes();
        }
        if (sumTimes > target.getSkillPoint()) {
            String msg = String.format("升级技能所需要的技能点数=%s，当前总的技能点为=%s！", sumTimes, target.getSkillPoint());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        return sumTimes;
    }


    /**
     * 检查技能升级参数需要的消耗是否足够
     *
     * @param skillUpgradeInfos
     * @param hero
     */
    private IdMaptoCount check_SkillUpgradeInfo_Consumes(List<Sm_Heros_SkillUpgradeInfo> skillUpgradeInfos, Hero hero) {
        IdMaptoCount sumConsumes = new IdMaptoCount();
        for (Sm_Heros_SkillUpgradeInfo info : skillUpgradeInfos) {
            SkillPositionEnum skillPos = info.getSkillPos();
            int oldSkillLv = getSkillLevel(hero, skillPos);
            for (int i = 0; i < info.getTimes(); i++) {
                IdAndCount idAndCount = Table_SkillUpgradeConsumes_Row.getConsumes(hero.getTpId(), skillPos, oldSkillLv);
                sumConsumes.add(idAndCount);
                oldSkillLv++;
            }
        }
        LogicCheckUtils.canRemove(itemIoCtrl, sumConsumes);
        return sumConsumes;
    }


    /**
     * 检查战魂升级参数
     *
     * @param consumeTpIds
     */
    private void check_ItemCanUseAsWarSoulExpItem(List<IdAndCount> consumeTpIds) {
        for (IdAndCount idAndCount : consumeTpIds) {
            Table_Item_Row row = RootTc.get(Table_Item_Row.class, idAndCount.getId());
            if (row == null) {
                String msg = String.format("所有消耗品=%s 其中id=%s,非道具！", consumeTpIds, idAndCount.getId());
                throw new BusinessLogicMismatchConditionException(msg);
            }
            if (!row.getIsWarSoulExp()) {
                String msg = String.format("所有消耗品=%s 其中id=%s,非战魂经验道具！", consumeTpIds, idAndCount.getId());
                throw new BusinessLogicMismatchConditionException(msg);
            }
        }
    }

    /**
     * 检测战魂是否已经开启了
     *
     * @param hero
     * @param soulPosition
     */
    private void check_IsWarSoulHasUnlock(Hero hero, WarSoulPositionEnum soulPosition) {
        TupleListCell<Integer> needColor = AllServerConfig.Heros_Spirit_Unlock_Need_Color.getConfig();
        List<TupleCell<Integer>> all = needColor.getAll();
        for (int i = all.size() - 1; i >= 0; i--) {
            TupleCell<Integer> color = all.get(i);
            if (hero.getQualityLv() >= color.get(TupleCell.FIRST)) { // 当前可以开启的最大战魂
                if (soulPosition.getNumber() > color.get(TupleCell.SECOND)) {
                    String msg = String.format("武将当前的品质=%s 可以解锁的最大战魂位置=%s, 已经解锁的战魂=%s, 当前请求的战魂=%s过大！", hero.getQualityLv(), color.get(TupleCell.SECOND), hero.getSouls(), soulPosition);
                    throw new BusinessLogicMismatchConditionException(msg);
                }
                return;
            }
        }
    }


    // ======================== 针对武将模块的数据采集 start start start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    /**
     * 统计各个品质武将的数量
     */
    private void statisticsHeroQuality(ColorDetailTypeEnum oldQualityLv, ColorDetailTypeEnum newQualityLv) {
        if (newQualityLv.getNumber() <= oldQualityLv.getNumber()) {
            return;
        }
        // 变化的品质为，[oldQualityLv+1,newQualityLv]
        Map<PrivateNotifyTypeEnum, Integer> typeToCount = new HashMap<>();
        for (int number = oldQualityLv.getNumber() + 1; number <= newQualityLv.getNumber(); number++) {
            // num 对应的PrivateNotifyTypeEnum，201-1+num
            PrivateNotifyTypeEnum notifyTypeEnum = PrivateNotifyTypeEnum.parse(PrivateNotifyTypeEnum.Heros_WHITE_QualityNum.getCode() - 1 + number);
            int count = 0;
            for (Hero hero : target.getIdToHero().values()) {
                if (hero.getQualityLv() >= number) {
                    count++;
                }
            }
            typeToCount.put(notifyTypeEnum, count);
        }
        typeToCount.forEach((type, count) -> {
            Pr_NotifyMsg notifyMsg = new Pr_NotifyMsg(type, count);
            sendPrivateMsg(notifyMsg);
        });
    }

    private void statisticsHasCollectHeroNum() {
        int count = target.getIdToHero().size();
        RedisRankUtils.insertRank(count, getPlayerCtrl().getPlayerId(), getPlayerCtrl().getOuterRealmId(), CommonRankTypeEnum.RK_HERO_COUNT);
        Pr_NotifyMsg notifyMsg2 = new Pr_NotifyMsg(PrivateNotifyTypeEnum.Heros_HasCollectHeroNum, count);
        sendPrivateMsg(notifyMsg2);
    }

    private void statisticsDyUpQualityTs() {
        Pr_NotifyMsg notifyMsg2 = new Pr_NotifyMsg(PrivateNotifyTypeEnum.Heros_Daliy_UpQualiy_Times, target.getDyUpQualityTs());
        sendPrivateMsg(notifyMsg2);
    }

    private void statisticsDyUpSkillTs() {
        Pr_NotifyMsg notifyMsg2 = new Pr_NotifyMsg(PrivateNotifyTypeEnum.Heros_Daliy_UpSkill_Times, target.getDyUpSkillTs());
        sendPrivateMsg(notifyMsg2);
    }


    private void statisticsDyUpHeroLvTs() {
        Pr_NotifyMsg notifyMsg2 = new Pr_NotifyMsg(PrivateNotifyTypeEnum.Heros_Daliy_UpLv_Times, target.getDyUpHeroLvTs());
        sendPrivateMsg(notifyMsg2);
    }

    /**
     * 统计武将数量
     */
    private void statisticsMainFormationBattleValue() {
        long value = attrsContainer.getFormationSumBattleValue_Formation_Main();
        RedisRankUtils.insertRank(value, getPlayerCtrl().getPlayerId(), getPlayerCtrl().getOuterRealmId(), CommonRankTypeEnum.RK_BATTLE_VALUE);
    }


    private void broadcastSystemMsg_StarLvUp(Hero hero) {
        SystemChatMsg systemChatMsg = new SystemChatMsg(MagicNumbers.SYSTEM_BROADCAST_ID_1,
                getPlayerCtrl().getTarget().getBase().getName(),
                String.valueOf(hero.getTpId()),
                String.valueOf(hero.getStarLv()),
                String.valueOf(hero.getQualityLv())
        );
        GroupChatMsg groupChatMsg = new GroupChatMsg(ChatTypeEnum.CHAT_SYSTEM, systemChatMsg);
        BroadcastSystemMsgUtils.broad(getPlayerCtrl().getContext(), getPlayerCtrl().getTarget().getAccount().getInnerRealmId(), groupChatMsg);
    }

    private void broadcastSystemMsg_GetHero(Hero hero) {
        SystemChatMsg systemChatMsg = new SystemChatMsg(MagicNumbers.SYSTEM_BROADCAST_ID_2,
                getPlayerCtrl().getTarget().getBase().getName(),
                String.valueOf(hero.getStarLv()),
                String.valueOf(hero.getTpId()),
                String.valueOf(hero.getQualityLv())
        );
        GroupChatMsg groupChatMsg = new GroupChatMsg(ChatTypeEnum.CHAT_SYSTEM, systemChatMsg);

        BroadcastSystemMsgUtils.broad(getPlayerCtrl().getContext(), getPlayerCtrl().getTarget().getAccount().getInnerRealmId(), groupChatMsg);
    }

    // ======================== 针对武将模块的数据采集 end end end <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
}
