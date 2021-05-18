package ws.gameServer.features.standalone.extp.newGuild.msg.redBag;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.protos.EnumsProtos.GuildRedBagTypeEnum;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

import java.util.Map;

/**
 * Created by lee on 5/25/17.
 */
public class In_NewGuildRedBagRank {
    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = -1930471427740160884L;
        private SimplePlayer simplePlayer;
        private String redBagId;
        private GuildRedBagTypeEnum redBagTypeEnum;

        public Request(SimplePlayer simplePlayer, GuildRedBagTypeEnum redBagTypeEnum) {
            this.simplePlayer = simplePlayer;
            this.redBagTypeEnum = redBagTypeEnum;
        }

        public Request(SimplePlayer simplePlayer, String redBagId, GuildRedBagTypeEnum redBagTypeEnum) {
            this.simplePlayer = simplePlayer;
            this.redBagId = redBagId;
            this.redBagTypeEnum = redBagTypeEnum;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }

        public GuildRedBagTypeEnum getRedBagTypeEnum() {
            return redBagTypeEnum;
        }

        public String getRedBagId() {
            return redBagId;
        }
    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = 2819134552809500758L;
        private Request request;
        private Map<String, Integer> playerNameAndShare;

        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;
        }


        public Response(Request request, Map<String, Integer> playerNameAndShare) {
            this.request = request;
            this.playerNameAndShare = playerNameAndShare;
        }

        public Map<String, Integer> getPlayerNameAndShare() {
            return playerNameAndShare;
        }

        public Request getRequest() {
            return request;
        }
    }
}
