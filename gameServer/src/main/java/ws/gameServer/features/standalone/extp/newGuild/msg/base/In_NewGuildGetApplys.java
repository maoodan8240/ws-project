package ws.gameServer.features.standalone.extp.newGuild.msg.base;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildApplyInfo;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 5/9/17.
 */
public class In_NewGuildGetApplys {

    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = -6647877078503842986L;
        private SimplePlayer simplePlayer;
        private String guildId;

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }

        public String getGuildId() {
            return guildId;
        }

        public Request(String guildId, SimplePlayer simplePlayer) {
            this.simplePlayer = simplePlayer;
            this.guildId = guildId;
        }
    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = 2858702104127848455L;
        private Request request;
        private List<NewGuildApplyInfo> applyList = new ArrayList<>();

        public Request getRequest() {
            return request;
        }

        public List<NewGuildApplyInfo> getApplyList() {
            return applyList;
        }

        public Response(Request request, List<NewGuildApplyInfo> applyList) {
            this.request = request;
            this.applyList = applyList;
        }

        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;
        }
    }
}
