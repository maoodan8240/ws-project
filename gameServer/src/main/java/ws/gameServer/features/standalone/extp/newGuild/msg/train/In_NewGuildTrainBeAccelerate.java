package ws.gameServer.features.standalone.extp.newGuild.msg.train;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.relationship.topLevelPojos.heros.Heros;

import java.util.List;

/**
 * Created by lee on 8/21/17.
 */
public class In_NewGuildTrainBeAccelerate {

    public static class Request extends AbstractInnerMsg {
        private int heroId;
        private int outRealmId;
        private List<String> playerNames;


        public Request(int heroId, List<String> playerNames, int outRealmId) {
            this.heroId = heroId;
            this.playerNames = playerNames;
            this.outRealmId = outRealmId;
        }

        public int getHeroId() {
            return heroId;
        }

        public List<String> getPlayerNames() {
            return playerNames;
        }

        public int getOutRealmId() {
            return outRealmId;
        }
    }

    public static class Response extends AbstractInnerMsg {

        private Heros heros;

        public Response(Heros heros) {
            this.heros = heros;
        }

        public Heros getHeros() {
            return heros;
        }
    }
}
