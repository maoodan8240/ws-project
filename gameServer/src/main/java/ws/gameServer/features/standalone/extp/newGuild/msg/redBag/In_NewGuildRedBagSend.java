package ws.gameServer.features.standalone.extp.newGuild.msg.redBag;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.protos.EnumsProtos.GuildRedBagTypeEnum;
import ws.protos.EnumsProtos.GuildSendRedBagTypeEnum;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenterPlayer;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

import java.util.List;

/**
 * Created by lee on 5/25/17.
 */
public class In_NewGuildRedBagSend {
    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = 1682929780243556033L;
        private SimplePlayer simplePlayer;
        private GuildRedBagTypeEnum redBagType;
        private GuildSendRedBagTypeEnum sendRedBagType;

        public Request(SimplePlayer simplePlayer, GuildRedBagTypeEnum redBagType, GuildSendRedBagTypeEnum sendRedBagType) {
            this.simplePlayer = simplePlayer;
            this.redBagType = redBagType;
            this.sendRedBagType = sendRedBagType;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }

        public GuildRedBagTypeEnum getRedBagType() {
            return redBagType;
        }

        public GuildSendRedBagTypeEnum getSendRedBagType() {
            return sendRedBagType;
        }
    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = -5763051876710977973L;
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

        public Response(Request request) {
            this.request = request;
        }

        public Request getRequest() {
            return request;
        }

        public List<NewGuildCenterPlayer> getGuildCenterPlayerList() {
            return guildCenterPlayerList;
        }
    }
}
