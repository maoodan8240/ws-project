package ws.gameServer.features.standalone.extp.newGuild.msg.train;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenterPlayer;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildTrainer;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

import java.util.List;

/**
 * Created by lee on 5/23/17.
 */
public class In_NewGuildTrainGetTrainerInfo {
    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = -2325505006210982910L;
        private SimplePlayer simplePlayer;

        public Request(SimplePlayer simplePlayer) {
            this.simplePlayer = simplePlayer;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }
    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = 7437322673763879749L;
        private Request request;
        private List<NewGuildTrainer> beforeSettleTrainerList;
        private NewGuildCenterPlayer guildCenterPlayer;

        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;
        }

        public Response(Request request, List<NewGuildTrainer> beforeSettleTrainerList, NewGuildCenterPlayer guildCenterPlayer) {
            this.request = request;
            this.beforeSettleTrainerList = beforeSettleTrainerList;
            this.guildCenterPlayer = guildCenterPlayer;
        }

        public Request getRequest() {
            return request;
        }

        public List<NewGuildTrainer> getBeforeSettleTrainerList() {
            return beforeSettleTrainerList;
        }

        public NewGuildCenterPlayer getGuildCenterPlayer() {
            return guildCenterPlayer;
        }
    }
}
