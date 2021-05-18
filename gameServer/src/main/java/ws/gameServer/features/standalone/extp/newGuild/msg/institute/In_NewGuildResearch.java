package ws.gameServer.features.standalone.extp.newGuild.msg.institute;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.protos.EnumsProtos.GuildInstituteTypeEnum;
import ws.protos.EnumsProtos.GuildResearchProjectTypeEnum;
import ws.protos.EnumsProtos.GuildResearchTypeEnum;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 5/16/17.
 */
public class In_NewGuildResearch {
    public static class Request extends AbstractInnerMsg {

        private static final long serialVersionUID = -8367825549790465093L;

        private SimplePlayer simplePlayer;
        private GuildInstituteTypeEnum instituteType;
        private GuildResearchProjectTypeEnum researchProjectType;
        private GuildResearchTypeEnum researchType;

        public Request(SimplePlayer simplePlayer, GuildInstituteTypeEnum instituteType, GuildResearchProjectTypeEnum researchProjectType, GuildResearchTypeEnum researchType) {
            this.simplePlayer = simplePlayer;
            this.instituteType = instituteType;
            this.researchProjectType = researchProjectType;
            this.researchType = researchType;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }

        public GuildResearchProjectTypeEnum getResearchProjectType() {
            return researchProjectType;
        }

        public GuildResearchTypeEnum getResearchType() {
            return researchType;
        }

        public GuildInstituteTypeEnum getInstituteType() {
            return instituteType;
        }
    }

    public static class Response extends AbstractInnerMsg {

        private static final long serialVersionUID = 5053959930707086900L;
        private Request request;
        private NewGuild guild;


        public Response(Request request, NewGuild guild) {
            this.request = request;
            this.guild = guild;
        }

        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;
        }

        public Request getRequest() {
            return request;
        }

        public NewGuild getGuild() {
            return guild;
        }
    }
}
