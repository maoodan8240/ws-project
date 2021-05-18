package ws.analogClient.features.flow.functions.pvp;

import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PvpProtos.Cm_Pvp;
import ws.protos.PvpProtos.Sm_Pvp;
import ws.protos.PvpProtos.Sm_Pvp_Enemy;
import ws.analogClient.features.utils.ClientUtils;

import java.util.List;

/**
 * Created by lee on 17-3-9.
 */
public class Func_Pvp_Utils {

    public static int sync() {
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.SYNC);
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_SYNC);
        ClientUtils.check(response);
        return response.getSmPvp().getSelf().getRank();
    }

    /**
     * 挑战
     * @param playerId
     * @param rank
     */
    public static void fight(int rank, String playerId) {
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.CHALLENGE);
        b.setRank(rank);
        b.setPlayerId(playerId);
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_CHALLENGE);
        ClientUtils.check(response);
    }
    

    /**
     * 刷新,返回一个比自己排名低的玩家
     */
    public static Sm_Pvp_Enemy refreshMinOne() {
        int playerRank = sync();
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.REFRESH);
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_REFRESH);
        ClientUtils.check(response);
        List<Sm_Pvp_Enemy> enemyList = response.getSmPvp().getEnemiesList();
        for (Sm_Pvp_Enemy enemy : enemyList) {
            if (enemy.getEnemy().getRank() > playerRank) {
                return enemy;
            }
        }
        return null;
    }


    /**
     * 刷新,返回一个比自己排名高的玩家
     */
    public static Sm_Pvp_Enemy refreshMaxOne() {
        int playerRank = sync();
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.REFRESH);
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_REFRESH);
        ClientUtils.check(response);
        List<Sm_Pvp_Enemy> enemyList = response.getSmPvp().getEnemiesList();
        for (Sm_Pvp_Enemy enemy : enemyList) {
            if (enemy.getEnemy().getRank() < playerRank) {
                return enemy;
            }
        }
        return null;
    }

    /**
     * 清除CD
     */
    public static void clearCd() {
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.CLEAR_CD);
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_CLEAR_CD);
        ClientUtils.check(response);
    }

}
