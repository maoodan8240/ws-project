package ws.relationship.topLevelPojos.newGuildCenter;

import ws.protos.EnumsProtos.GuildResearchProjectTypeEnum;
import ws.relationship.base.WsCloneable;
import ws.relationship.topLevelPojos.common.LevelUpObj;
import ws.relationship.utils.CloneUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lee on 5/15/17.
 */
public class NewGuildInstitute implements Serializable, WsCloneable {
    private static final long serialVersionUID = 4422016002172961039L;

    private Map<GuildResearchProjectTypeEnum, LevelUpObj> researchTypeAndProject = new HashMap<>(); //研究类型对应研究项目

    public Map<GuildResearchProjectTypeEnum, LevelUpObj> getResearchTypeAndProject() {
        return researchTypeAndProject;
    }

    public void setResearchTypeAndProject(Map<GuildResearchProjectTypeEnum, LevelUpObj> researchTypeAndProject) {
        this.researchTypeAndProject = researchTypeAndProject;
    }

    public NewGuildInstitute() {
    }

    public NewGuildInstitute(Map<GuildResearchProjectTypeEnum, LevelUpObj> researchTypeAndProject) {
        this.researchTypeAndProject = researchTypeAndProject;
    }

    @Override
    public NewGuildInstitute clone() {
        return new NewGuildInstitute(CloneUtils.cloneHashMap(researchTypeAndProject));
    }
}
