package ws.gameServer.features.standalone.extp.newGuild.msg.train;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildTrainer;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 5/22/17.
 */
public class In_NewGuildTrainUnlock {
    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = 2371764126157093755L;
        private SimplePlayer simplePlayer;
        private String guildId;
        private int index;

        public Request(SimplePlayer simplePlayer, String guildId, int index) {
            this.simplePlayer = simplePlayer;
            this.guildId = guildId;
            this.index = index;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }

        public String getGuildId() {
            return guildId;
        }

        public int getIndex() {
            return index;
        }
    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = -8606472165955521953L;

        private Request request;
        private NewGuildTrainer trainer;

        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;
        }

        public Response(Request request, NewGuildTrainer trainer) {
            this.request = request;
            this.trainer = trainer;
        }

        public Request getRequest() {
            return request;
        }

        public NewGuildTrainer getTrainer() {
            return trainer;
        }
    }
}
