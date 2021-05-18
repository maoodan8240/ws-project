package ws.gameServer.features.standalone.extp.newGuild.msg.base;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.protos.CommonProtos.Sm_Common_Round;
import ws.protos.EnumsProtos.GuildJoinTypeEnum;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 5/10/17.
 */
public class In_NewGuildChange {

    public static class Request extends AbstractInnerMsg {

        private static final long serialVersionUID = -2375022106264343010L;

        private String guildId;
        private SimplePlayer simplePlayer;
        private GuildJoinTypeEnum joinTypeEnum;
        private Sm_Common_Round round;


        public Request(String guildId, SimplePlayer simplePlayer, GuildJoinTypeEnum joinTypeEnum, Sm_Common_Round round) {
            this.guildId = guildId;
            this.simplePlayer = simplePlayer;
            this.joinTypeEnum = joinTypeEnum;
            this.round = round;
        }

        public String getGuildId() {
            return guildId;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }

        public GuildJoinTypeEnum getJoinTypeEnum() {
            return joinTypeEnum;
        }

        public Sm_Common_Round getRound() {
            return round;
        }
    }

    public static class Response extends AbstractInnerMsg {

        private static final long serialVersionUID = -1410868510701003857L;


        private Request request;
        private NewGuild guild;


        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;
        }

        public Response(Request request, NewGuild guild) {
            this.request = request;
            this.guild = guild;
        }

        public Request getRequest() {
            return request;
        }

        public NewGuild getGuild() {
            return guild;
        }
    }
}
