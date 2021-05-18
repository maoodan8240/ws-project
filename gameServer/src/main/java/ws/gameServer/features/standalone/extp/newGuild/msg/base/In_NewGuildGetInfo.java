package ws.gameServer.features.standalone.extp.newGuild.msg.base;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

import java.util.List;

/**
 * Created by lee on 5/9/17.
 */
public class In_NewGuildGetInfo {
    public static class Request extends AbstractInnerMsg {

        private static final long serialVersionUID = 5642848285688644219L;

        private String guildId;
        private SimplePlayer simplePlayer;

        public String getGuildId() {
            return guildId;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }

        public Request(SimplePlayer simplePlayer) {
            this.simplePlayer = simplePlayer;
        }

        public Request(String guildId, SimplePlayer simplePlayer) {
            this.guildId = guildId;
            this.simplePlayer = simplePlayer;
        }
    }

    public static class Response extends AbstractInnerMsg {

        private static final long serialVersionUID = 5880822966130890659L;
        private Request request;
        private List<NewGuild> guildList;

        public Request getRequest() {
            return request;
        }

        public List<NewGuild> getGuildList() {
            return guildList;
        }

        public Response(Request request, List<NewGuild> guildList) {
            this.request = request;
            this.guildList = guildList;
        }

        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;
            this.guildList = guildList;
        }
    }
}
