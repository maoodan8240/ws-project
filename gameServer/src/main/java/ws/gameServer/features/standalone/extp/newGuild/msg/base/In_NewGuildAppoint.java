package ws.gameServer.features.standalone.extp.newGuild.msg.base;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.protos.EnumsProtos.GuildJobEnum;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenterPlayer;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 5/11/17.
 */
public class In_NewGuildAppoint {
    public static class Request extends AbstractInnerMsg {

        private static final long serialVersionUID = -5016479832574448867L;

        private SimplePlayer simplePlayer;
        private SimplePlayer appointSimplePlayer;
        private GuildJobEnum job;

        public Request(SimplePlayer simplePlayer, SimplePlayer appointSimplePlayer, GuildJobEnum job) {
            this.simplePlayer = simplePlayer;
            this.appointSimplePlayer = appointSimplePlayer;
            this.job = job;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }

        public SimplePlayer getAppointSimplePlayer() {
            return appointSimplePlayer;
        }

        public GuildJobEnum getJob() {
            return job;
        }
    }

    public static class Response extends AbstractInnerMsg {

        private static final long serialVersionUID = 4365814529571289609L;
        private Request request;
        private NewGuildCenterPlayer guildCenterPlayer;
        private NewGuild guild;

        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;
        }


        public Response(Request request, NewGuildCenterPlayer guildCenterPlayer, NewGuild guild) {
            this.request = request;
            this.guildCenterPlayer = guildCenterPlayer;
            this.guild = guild;
        }

        public Request getRequest() {
            return request;
        }

        public NewGuildCenterPlayer getGuildCenterPlayer() {
            return guildCenterPlayer;
        }

        public NewGuild getGuild() {
            return guild;
        }
    }
}
