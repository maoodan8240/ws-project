package ws.gameServer.features.standalone.extp.newGuild.msg.train;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.protos.NewGuildTrainProtos.Sm_NewGuildTrain;
import ws.protos.NewGuildTrainProtos.Sm_NewGuildTrain.Action;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenterPlayer;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildTrainer;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 5/23/17.
 */
public class In_NewGuildTrainAccelerate {
    public static class Request extends AbstractInnerMsg {

        private static final long serialVersionUID = 1708296224324821527L;
        private SimplePlayer simplePlayer;
        private String playerId;
        private int index;
        private Sm_NewGuildTrain.Action action;

        public Request(SimplePlayer simplePlayer, String playerId, int index, Sm_NewGuildTrain.Action action) {
            this.simplePlayer = simplePlayer;
            this.playerId = playerId;
            this.index = index;
            this.action = action;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }

        public String getPlayerId() {
            return playerId;
        }

        public int getIndex() {
            return index;
        }

        public Action getAction() {
            return action;
        }
    }

    public static class Response extends AbstractInnerMsg {

        private static final long serialVersionUID = 8958886322836943651L;
        private Request request;
        private NewGuildCenterPlayer beAccelerateGuildCenterPlayer;
        private NewGuildTrainer beforeTrainer;
        private NewGuildCenterPlayer guildCenterPlayer;

        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;
        }

        public Response(Request request, NewGuildCenterPlayer beAccelerateGuildCenterPlayer, NewGuildTrainer beforeTrainer, NewGuildCenterPlayer guildCenterPlayer) {
            this.request = request;
            this.beAccelerateGuildCenterPlayer = beAccelerateGuildCenterPlayer;
            this.beforeTrainer = beforeTrainer;
            this.guildCenterPlayer = guildCenterPlayer;
        }

        public Request getRequest() {
            return request;
        }

        public NewGuildCenterPlayer getBeAccelerateGuildCenterPlayer() {
            return beAccelerateGuildCenterPlayer;
        }

        public NewGuildTrainer getBeforeTrainer() {
            return beforeTrainer;
        }

        public NewGuildCenterPlayer getGuildCenterPlayer() {
            return guildCenterPlayer;
        }
    }
}
