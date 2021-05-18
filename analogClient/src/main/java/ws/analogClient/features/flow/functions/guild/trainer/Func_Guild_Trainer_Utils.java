package ws.analogClient.features.flow.functions.guild.trainer;

import ws.analogClient.features.utils.ClientUtils;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.NewGuildTrainProtos.Cm_NewGuildTrain;
import ws.protos.NewGuildTrainProtos.Sm_NewGuildTrain;
import ws.relationship.utils.ProtoUtils;

/**
 * Created by lee on 8/17/17.
 */
public class Func_Guild_Trainer_Utils {

    public static void sync() {
        Cm_NewGuildTrain.Builder b = Cm_NewGuildTrain.newBuilder();
        b.setAction(Cm_NewGuildTrain.Action.SYNC);
        Response response = ClientUtils.send(b.build(), Sm_NewGuildTrain.Action.RESP_SYNC);
        ClientUtils.check(response);
    }

    public static void unlock(int index) {
        Cm_NewGuildTrain.Builder b = Cm_NewGuildTrain.newBuilder();
        b.setAction(Cm_NewGuildTrain.Action.UNLOCK);
        b.setIndex(index);
        Response response = ClientUtils.send(b.build(), Sm_NewGuildTrain.Action.RESP_UNLOCK);
        ClientUtils.check(response);
    }

    public static void replace(int heroId, int index) {
        Cm_NewGuildTrain.Builder b = Cm_NewGuildTrain.newBuilder();
        b.setAction(Cm_NewGuildTrain.Action.REPLACE);
        b.setIndex(index);
        b.setHeroId(heroId);
        Response response = ClientUtils.send(b.build(), Sm_NewGuildTrain.Action.RESP_REPLACE);
        ClientUtils.check(response);
    }

    public static void accelerate(String playerId, int index) {
        Cm_NewGuildTrain.Builder b = Cm_NewGuildTrain.newBuilder();
        b.setAction(Cm_NewGuildTrain.Action.ACCELERATE);
        b.setIndex(index);
        b.setPlayerId(playerId);
        Response response = ClientUtils.send(b.build(), Sm_NewGuildTrain.Action.RESP_ACCELERATE);
        ClientUtils.check(response);
    }

    public static void getMember(int min, int max) {
        Cm_NewGuildTrain.Builder b = Cm_NewGuildTrain.newBuilder();
        b.setAction(Cm_NewGuildTrain.Action.GET_MEMBER);
        b.setRound(ProtoUtils.createSm_Common_Round(min, max));
        Response response = ClientUtils.send(b.build(), Sm_NewGuildTrain.Action.RESP_GET_MEMBER);
        ClientUtils.check(response);
    }

    public static void stamp(String playerId) {
        Cm_NewGuildTrain.Builder b = Cm_NewGuildTrain.newBuilder();
        b.setAction(Cm_NewGuildTrain.Action.STAMP);
        b.setPlayerId(playerId);
        Response response = ClientUtils.send(b.build(), Sm_NewGuildTrain.Action.RESP_GET_MEMBER);
        ClientUtils.check(response);
    }

    public static void getTrainerInfo(String playerId) {
        Cm_NewGuildTrain.Builder b = Cm_NewGuildTrain.newBuilder();
        b.setAction(Cm_NewGuildTrain.Action.GET_TRAINER_INFO);
        b.setPlayerId(playerId);
        Response response = ClientUtils.send(b.build(), Sm_NewGuildTrain.Action.RESP_GET_TRAINER_INFO);
        ClientUtils.check(response);
    }

    public static void randomAccelerate() {
        Cm_NewGuildTrain.Builder b = Cm_NewGuildTrain.newBuilder();
        b.setAction(Cm_NewGuildTrain.Action.RANDOM_ACCELERATE);
        Response response = ClientUtils.send(b.build(), Sm_NewGuildTrain.Action.RESP_RANDOM_ACCELERATE);
        ClientUtils.check(response);
    }

    public static void settle(int index) {
        Cm_NewGuildTrain.Builder b = Cm_NewGuildTrain.newBuilder();
        b.setAction(Cm_NewGuildTrain.Action.SETTLE);
        b.setIndex(index);
        Response response = ClientUtils.send(b.build(), Sm_NewGuildTrain.Action.RESP_SETTLE);
        ClientUtils.check(response);
    }
}
