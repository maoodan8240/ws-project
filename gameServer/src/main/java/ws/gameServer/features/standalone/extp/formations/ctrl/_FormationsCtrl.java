package ws.gameServer.features.standalone.extp.formations.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.formations.utils.FormationsCtrlProtos;
import ws.gameServer.features.standalone.extp.formations.utils.FormationsCtrlUtils;
import ws.gameServer.features.standalone.extp.heros.HerosExtp;
import ws.gameServer.features.standalone.extp.heros.ctrl.HerosCtrl;
import ws.gameServer.features.standalone.extp.itemBag.ItemBagExtp;
import ws.gameServer.features.standalone.extp.itemBag.ctrl.ItemBagCtrl;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.EnumsProtos.FormationTypeEnum;
import ws.protos.EnumsProtos.HeroPositionEnum;
import ws.protos.FormationsProtos.Sm_Formations;
import ws.protos.FormationsProtos.Sm_Formations.Action;
import ws.protos.FormationsProtos.Sm_Formations_OneFormation;
import ws.protos.FormationsProtos.Sm_Formations_PosToHeroId;
import ws.relationship.base.IdAndCount;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.topLevelPojos.formations.Formation;
import ws.relationship.topLevelPojos.formations.FormationPos;
import ws.relationship.topLevelPojos.formations.Formations;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayerMfHero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class _FormationsCtrl extends AbstractPlayerExteControler<Formations> implements FormationsCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_FormationsCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;
    private HerosCtrl herosCtrl;
    private ItemBagCtrl itemBagCtrl;

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
        herosCtrl = getPlayerCtrl().getExtension(HerosExtp.class).getControlerForQuery();
        itemBagCtrl = getPlayerCtrl().getExtension(ItemBagExtp.class).getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {

    }

    @Override
    public void sync() {
        SenderFunc.sendInner(this, Sm_Formations.class, Sm_Formations.Builder.class, Sm_Formations.Action.RESP_SYNC, (b, br) -> {
            b.addAllFormations(FormationsCtrlProtos.create_Sm_Formations_OneFormation_List(target.getTypeToFormation()));
        });
    }

    @Override
    public void deployment(Sm_Formations_OneFormation smFormation) {
        LogicCheckUtils.requireNonNull(smFormation, Sm_Formations_OneFormation.class);
        LogicCheckUtils.requireNonNull(smFormation.getType(), FormationTypeEnum.class);
        if (smFormation.getPosLisList() == null || smFormation.getPosLisList().size() <= 0) {
            String msg = String.format("阵容类型为=%s 不能保存空的阵容！", smFormation.getType());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        check_SameHeroPos(smFormation);
        check_SameHeroId(smFormation);
        Formation formation = getOneFormation(smFormation.getType());
        updateFormationPos(smFormation, formation);
        SenderFunc.sendInner(this, Sm_Formations.class, Sm_Formations.Builder.class, Action.RESP_DEPLOYMENT, (b, br) -> {
            b.addFormations(FormationsCtrlProtos.create_Sm_Formations_OneFormation(formation));
        });
        save();
    }

    @Override
    public boolean isIdInFormation(FormationTypeEnum type, int heroId) {
        if (!(containsFormation(type) && heroId > 0)) {
            return false;
        }
        Formation formation = getOneFormation(type);
        for (FormationPos formationPos : formation.getPosToFormationPos().values()) {
            if (formationPos.getHeroId() == heroId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<HeroPositionEnum, SimplePlayerMfHero> getFormationMfHeros(FormationTypeEnum formationType) {
        Map<HeroPositionEnum, SimplePlayerMfHero> posToMfHero = new HashMap<>();
        Formation formation = getOneFormation(formationType);
        for (FormationPos onePos : formation.getPosToFormationPos().values()) {
            if (onePos.getHeroId() > 0) {
                Hero hero = herosCtrl.getHero(onePos.getHeroId());
                posToMfHero.put(onePos.getPos(), new SimplePlayerMfHero(onePos.getPos(), hero));
            }
        }
        return posToMfHero;
    }

    /**
     * 获取一个阵容，并初始化
     *
     * @param type
     * @return
     */
    public Formation getOneFormation(FormationTypeEnum type) {
        LogicCheckUtils.validateParam(FormationTypeEnum.class, type);
        if (!containsFormation(type)) {
            target.getTypeToFormation().put(type, new Formation(type));
        }
        Formation formation = target.getTypeToFormation().get(type);
        FormationsCtrlUtils.initFormation(formation);
        return formation;
    }

    //=====================================================================================

    public boolean containsFormation(FormationTypeEnum type) {
        return target.getTypeToFormation().containsKey(type);
    }


    /**
     * 更新站位上的武将
     *
     * @param smFormation
     * @param formation
     */
    private void updateFormationPos(Sm_Formations_OneFormation smFormation, Formation formation) {
        formation.getPosToFormationPos().clear();
        FormationsCtrlUtils.initFormation(formation);
        for (Sm_Formations_PosToHeroId posToHeroId : smFormation.getPosLisList()) {
            if (posToHeroId.getHeroId() > 0) {
                formation.getPosToFormationPos().get(posToHeroId.getPos()).setHeroId(posToHeroId.getHeroId());
            }
        }
    }

    /**
     * 不能有相同的武将Id
     *
     * @param smFormation
     */
    private void check_SameHeroId(Sm_Formations_OneFormation smFormation) {
        List<Integer> heroIds = new ArrayList<>();
        for (Sm_Formations_PosToHeroId posToHeroId : smFormation.getPosLisList()) {
            if (posToHeroId.getHeroId() <= 0) {
                String msg = String.format("阵容类型为=%s 武将Id必须>0！", smFormation.getType());
                throw new BusinessLogicMismatchConditionException(msg);
            }
            if (heroIds.contains(posToHeroId.getHeroId())) {
                String msg = String.format("阵容类型为=%s 阵容中包含相同的Id=%s！", smFormation.getType(), posToHeroId.getHeroId());
                throw new BusinessLogicMismatchConditionException(msg);
            }
            LogicCheckUtils.canRemove(itemIoCtrl, new IdAndCount(posToHeroId.getHeroId()));
            heroIds.add(posToHeroId.getHeroId());
        }
        heroIds.clear();
    }


    /**
     * 不能有相同的位置
     *
     * @param smFormation
     */
    private void check_SameHeroPos(Sm_Formations_OneFormation smFormation) {
        List<HeroPositionEnum> heroPoses = new ArrayList<>();
        for (Sm_Formations_PosToHeroId posToHeroId : smFormation.getPosLisList()) {
            if (posToHeroId.getPos() == null) {
                String msg = String.format("阵容类型为=%s 阵容位置不能传空！", smFormation.getType());
                throw new BusinessLogicMismatchConditionException(msg);
            }
            if (heroPoses.contains(posToHeroId.getPos())) {
                String msg = String.format("阵容类型为=%s 阵容中包含相同的Pos=%s！", smFormation.getType(), posToHeroId.getPos());
                throw new BusinessLogicMismatchConditionException(msg);
            }
            heroPoses.add(posToHeroId.getPos());
        }
        heroPoses.clear();
    }
}
