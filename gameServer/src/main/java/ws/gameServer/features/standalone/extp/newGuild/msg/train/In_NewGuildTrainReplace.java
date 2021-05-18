package ws.gameServer.features.standalone.extp.newGuild.msg.train;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.gameServer.features.standalone.actor.newGuildCenter.utils.TrainerReplaceResult;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 5/22/17.
 */
public class In_NewGuildTrainReplace {
    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = 4131193136342689472L;
        private SimplePlayer simplePlayer;
        private String guildId;
        private int index;
        private int heroId;

        public Request(SimplePlayer simplePlayer, String guildId, int index, int heroId) {
            this.simplePlayer = simplePlayer;
            this.guildId = guildId;
            this.index = index;
            this.heroId = heroId;
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

        public int getHeroId() {
            return heroId;
        }
    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = -7662271476458167494L;

        private Request request;
        private TrainerReplaceResult result;

        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;
        }


        public Response(Request request) {
            this.request = request;
        }


        public Response(Request request, TrainerReplaceResult result) {
            this.request = request;
            this.result = result;
        }


        public Request getRequest() {
            return request;
        }

        public TrainerReplaceResult getResult() {
            return result;
        }
    }
}
