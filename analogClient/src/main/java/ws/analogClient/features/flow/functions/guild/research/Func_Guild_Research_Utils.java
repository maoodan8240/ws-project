package ws.analogClient.features.flow.functions.guild.research;

import ws.analogClient.features.utils.ClientUtils;
import ws.protos.EnumsProtos.GuildInstituteTypeEnum;
import ws.protos.EnumsProtos.GuildResearchProjectTypeEnum;
import ws.protos.EnumsProtos.GuildResearchTypeEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.NewGuildResearchProtos.Cm_NewGuildResearch;
import ws.protos.NewGuildResearchProtos.Sm_NewGuildResearch;

/**
 * Created by lee on 6/5/17.
 */
public class Func_Guild_Research_Utils {
    public static void sync() {
        Cm_NewGuildResearch.Builder b = Cm_NewGuildResearch.newBuilder();
        b.setAction(Cm_NewGuildResearch.Action.SYNC_RESEARCH);
        Response response = ClientUtils.send(b.build(), Sm_NewGuildResearch.Action.RESP_SYNC_RESEARCH);
        ClientUtils.check(response);
    }

    public static void research(GuildResearchProjectTypeEnum researchProjectType, GuildResearchTypeEnum researchType, GuildInstituteTypeEnum instituteType) {
        Cm_NewGuildResearch.Builder b = Cm_NewGuildResearch.newBuilder();
        b.setAction(Cm_NewGuildResearch.Action.RESEARCH);
        b.setResearchProjectType(researchProjectType);
        b.setResearchType(researchType);
        b.setInstituteType(instituteType);
        Response response = ClientUtils.send(b.build(), Sm_NewGuildResearch.Action.RESP_RESEARCH);
        ClientUtils.check(response);
    }
}
