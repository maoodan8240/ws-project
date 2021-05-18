package ws.gameServer.features.standalone.extp.newGuild.msg.train;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenterPlayer;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

import java.util.List;

/**
 * Created by lee on 8/16/17.
 */
public class In_NewGuildTrainGetMember {
    public static class Request extends AbstractInnerMsg {
        private SimplePlayer simplePlayer;

        public Request(SimplePlayer simplePlayer) {
            this.simplePlayer = simplePlayer;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }


    }

    public static class Response extends AbstractInnerMsg {
        private Request request;
        private List<NewGuildCenterPlayer> guildCenterPlayerList;


        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;
        }

        public Response(Request request, List<NewGuildCenterPlayer> guildCenterPlayerList) {
            this.request = request;
            this.guildCenterPlayerList = guildCenterPlayerList;
        }

        public Request getRequest() {
            return request;
        }

        public List<NewGuildCenterPlayer> getGuildCenterPlayerList() {
            return guildCenterPlayerList;
        }
    }
}
