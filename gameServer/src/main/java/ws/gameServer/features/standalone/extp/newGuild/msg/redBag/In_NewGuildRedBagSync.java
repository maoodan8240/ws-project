package ws.gameServer.features.standalone.extp.newGuild.msg.redBag;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.protos.EnumsProtos.GuildRedBagTypeEnum;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildSystemRedBag;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

import java.util.Map;

/**
 * Created by lee on 5/24/17.
 */
public class In_NewGuildRedBagSync {
    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = 1294602111281812628L;
        private SimplePlayer simplePlayer;

        public Request(SimplePlayer simplePlayer) {
            this.simplePlayer = simplePlayer;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }
    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = 7638123542591942372L;
        private Request request;
        private Map<GuildRedBagTypeEnum, NewGuildSystemRedBag> typeAndNewGuildRedBag;

        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;
        }

        public Response(Request request, Map<GuildRedBagTypeEnum, NewGuildSystemRedBag> typeAndNewGuildRedBag) {
            this.request = request;
            this.typeAndNewGuildRedBag = typeAndNewGuildRedBag;
        }

        public Request getRequest() {
            return request;
        }

        public Map<GuildRedBagTypeEnum, NewGuildSystemRedBag> getTypeAndNewGuildRedBag() {
            return typeAndNewGuildRedBag;
        }
    }
}
