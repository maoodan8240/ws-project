package ws.gameServer.features.standalone.extp.newGuild.msg.redBag;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.protos.EnumsProtos.GuildRedBagTypeEnum;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildRedBag;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 5/26/17.
 */
public class In_NewGuildRedBagGrab {

    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = 4001031457019365633L;
        private SimplePlayer simplePlayer;
        private GuildRedBagTypeEnum redBagTypeEnum;
        private String redBagId;

        public Request(SimplePlayer simplePlayer, GuildRedBagTypeEnum redBagTypeEnum, String redBagId) {
            this.simplePlayer = simplePlayer;
            this.redBagTypeEnum = redBagTypeEnum;
            this.redBagId = redBagId;
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
        private static final long serialVersionUID = -1149088814192546130L;

        private Request request;
        private NewGuildRedBag redBag;


        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;
        }

        public Response(Request request, NewGuildRedBag redBag) {
            this.request = request;
            this.redBag = redBag;
        }

        public Request getRequest() {
            return request;
        }

        public NewGuildRedBag getRedBag() {
            return redBag;
        }
    }
}
