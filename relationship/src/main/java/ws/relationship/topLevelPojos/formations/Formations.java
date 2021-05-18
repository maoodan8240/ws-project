package ws.relationship.topLevelPojos.formations;

import ws.protos.EnumsProtos.FormationTypeEnum;
import ws.relationship.topLevelPojos.PlayerTopLevelPojo;

import java.util.HashMap;
import java.util.Map;

public class Formations extends PlayerTopLevelPojo {
    private static final long serialVersionUID = -1349776245811415762L;

    private Map<FormationTypeEnum, Formation> typeToFormation = new HashMap<>();


    public Formations() {
    }

    public Formations(String playerId) {
        super(playerId);
    }

    public Map<FormationTypeEnum, Formation> getTypeToFormation() {
        return typeToFormation;
    }

    public void setTypeToFormation(Map<FormationTypeEnum, Formation> typeToFormation) {
        this.typeToFormation = typeToFormation;
    }
}
