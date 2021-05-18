package ws.analogClient.features.flow.functions.guild.base;

import org.bson.types.ObjectId;
import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.EnumsProtos.GuildJobEnum;
import ws.protos.EnumsProtos.GuildJoinTypeEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.NewGuildProtos.Cm_NewGuild;
import ws.protos.NewGuildProtos.Sm_NewGuild;
import ws.relationship.utils.ProtoUtils;

/**
 * Created by lee on 17-3-23.
 */
public class Func_Guild_Utils {

    public static void createGuild() {
        Func_Guild_Utils.create("社团" + ObjectId.get().toString(), 101);
    }

    public static void create(String guildName, int iconId) {
        Func_Gm.setLv(25);
        Func_Gm.addResource("2:400");
        Cm_NewGuild.Builder b = Cm_NewGuild.newBuilder();
        b.setAction(Cm_NewGuild.Action.CREATE);
        b.setGuildName(guildName);
        b.setGuildIcon(iconId);
        Response response = ClientUtils.send(b.build(), Sm_NewGuild.Action.RESP_CREATE);
        ClientUtils.check(response);
    }

    public static void join(String guildId) {
        Func_Gm.setLv(24);
        Cm_NewGuild.Builder b = Cm_NewGuild.newBuilder();
        b.setAction(Cm_NewGuild.Action.JOIN);
        b.setGuildId(guildId);
        Response response = ClientUtils.send(b.build(), Sm_NewGuild.Action.RESP_JOIN);
        ClientUtils.check(response);
    }

    public static Response search1(String searchArg) {
        Func_Gm.setLv(24);
        Cm_NewGuild.Builder b = Cm_NewGuild.newBuilder();
        b.setAction(Cm_NewGuild.Action.SEARCH_ONE);
        b.setSearchArgs(searchArg);
        Response response = ClientUtils.send(b.build(), Sm_NewGuild.Action.RESP_SEARCH_ONE);
        ClientUtils.check(response);
        return response;
    }

    public static Response search(int min, int max) {
        Func_Gm.setLv(24);
        Cm_NewGuild.Builder b = Cm_NewGuild.newBuilder();
        b.setAction(Cm_NewGuild.Action.SEARCH_ALL);
        b.setRound(ProtoUtils.create_Sm_Common_Round(min, max));
        Response response = ClientUtils.send(b.build(), Sm_NewGuild.Action.RESP_SEARCH_ALL);
        ClientUtils.check(response);
        return response;
    }

    public static void sync() {
        Cm_NewGuild.Builder b = Cm_NewGuild.newBuilder();
        b.setAction(Cm_NewGuild.Action.SYNC);
        Response response = ClientUtils.send(b.build(), Sm_NewGuild.Action.RESP_SYNC);
        ClientUtils.check(response);
    }

    public static void agree(String playerId) {
        Cm_NewGuild.Builder b = Cm_NewGuild.newBuilder();
        b.setAction(Cm_NewGuild.Action.AGREE);
        b.setPlayerId(playerId);
        Response response = ClientUtils.send(b.build(), Sm_NewGuild.Action.RESP_AGREE);
        ClientUtils.check(response);
    }

    public static void getApply(int min, int max) {
        Cm_NewGuild.Builder b = Cm_NewGuild.newBuilder();
        b.setAction(Cm_NewGuild.Action.GET_APPLYS);
        b.setRound(ProtoUtils.createSm_Common_Round(min, max));
        Response response = ClientUtils.send(b.build(), Sm_NewGuild.Action.RESP_GET_APPLYS);
        ClientUtils.check(response);
    }

    public static void getMember(int min, int max) {
        Cm_NewGuild.Builder b = Cm_NewGuild.newBuilder();
        b.setAction(Cm_NewGuild.Action.GET_MEMBER);
        b.setRound(ProtoUtils.createSm_Common_Round(min, max));
        Response response = ClientUtils.send(b.build(), Sm_NewGuild.Action.RESP_GET_MEMBER);
        ClientUtils.check(response);
    }

    public static void change(GuildJoinTypeEnum joinTypeEnum, int lv) {
        Cm_NewGuild.Builder b = Cm_NewGuild.newBuilder();
        b.setAction(Cm_NewGuild.Action.CHANGE);
        b.setRound(ProtoUtils.createSm_Common_Round(lv, lv));
        b.setJoinType(joinTypeEnum);
        Response response = ClientUtils.send(b.build(), Sm_NewGuild.Action.RESP_CHANGE);
        ClientUtils.check(response);
    }

    public static void appoint(String playerId, GuildJobEnum jobEnum) {
        Cm_NewGuild.Builder b = Cm_NewGuild.newBuilder();
        b.setAction(Cm_NewGuild.Action.APPOINT);
        b.setPlayerId(playerId);
        b.setJob(jobEnum);
        Response response = ClientUtils.send(b.build(), Sm_NewGuild.Action.RESP_APPOINT);
        ClientUtils.check(response);
    }

    public static void disband() {
        Cm_NewGuild.Builder b = Cm_NewGuild.newBuilder();
        b.setAction(Cm_NewGuild.Action.DISBAND);
        Response response = ClientUtils.send(b.build(), Sm_NewGuild.Action.RESP_DISBAND);
        ClientUtils.check(response);
    }


    public static void out() {
        Cm_NewGuild.Builder b = Cm_NewGuild.newBuilder();
        b.setAction(Cm_NewGuild.Action.OUT);
        Response response = ClientUtils.send(b.build(), Sm_NewGuild.Action.RESP_OUT);
        ClientUtils.check(response);
    }

    public static void kick(String playerId) {
        Cm_NewGuild.Builder b = Cm_NewGuild.newBuilder();
        b.setAction(Cm_NewGuild.Action.KICK);
        b.setPlayerId(playerId);
        Response response = ClientUtils.send(b.build(), Sm_NewGuild.Action.RESP_KICK);
        ClientUtils.check(response);
    }

    public static void giveJob(String playerId) {
        Cm_NewGuild.Builder b = Cm_NewGuild.newBuilder();
        b.setAction(Cm_NewGuild.Action.GIVE_JOB);
        b.setPlayerId(playerId);
        Response response = ClientUtils.send(b.build(), Sm_NewGuild.Action.RESP_GIVE_JOB);
        ClientUtils.check(response);
    }
}
