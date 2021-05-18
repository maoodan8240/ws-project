package ws.gameServer.features.standalone.extp.formations.utils;

import ws.protos.EnumsProtos.FormationTypeEnum;
import ws.protos.FormationsProtos.Sm_Formations_OneFormation;
import ws.protos.FormationsProtos.Sm_Formations_PosToHeroId;
import ws.relationship.topLevelPojos.formations.Formation;
import ws.relationship.topLevelPojos.formations.FormationPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FormationsCtrlProtos {

    public static List<Sm_Formations_OneFormation> create_Sm_Formations_OneFormation_List(Map<FormationTypeEnum, Formation> typeToFormation) {
        List<Sm_Formations_OneFormation> bs = new ArrayList<>();
        for (Formation formation : typeToFormation.values()) {
            bs.add(create_Sm_Formations_OneFormation(formation));
        }
        return bs;
    }

    public static Sm_Formations_OneFormation create_Sm_Formations_OneFormation(Formation formation) {
        Sm_Formations_OneFormation.Builder oneFormation = Sm_Formations_OneFormation.newBuilder();
        oneFormation.setType(formation.getType());
        Sm_Formations_PosToHeroId.Builder b = Sm_Formations_PosToHeroId.newBuilder();
        for (FormationPos formationPos : formation.getPosToFormationPos().values()) {
            oneFormation.addPosLis(create_Sm_Formations_PosToHeroId(b, formationPos));
        }
        return oneFormation.build();
    }

    public static Sm_Formations_PosToHeroId create_Sm_Formations_PosToHeroId(Sm_Formations_PosToHeroId.Builder b, FormationPos formationPos) {
        b.clear();
        b.setPos(formationPos.getPos());
        b.setHeroId(formationPos.getHeroId());
        return b.build();
    }
}
