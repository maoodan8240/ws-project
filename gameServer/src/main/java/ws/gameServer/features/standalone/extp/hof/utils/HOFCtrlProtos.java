package ws.gameServer.features.standalone.extp.hof.utils;

import ws.protos.HOFProtos.Sm_HOF_Info;
import ws.relationship.topLevelPojos.hof.HOF_Info;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HOFCtrlProtos {


    public static List<Sm_HOF_Info> create_Sm_HOF_Info_List(Map<Integer, HOF_Info> heroIdToHOF_Info) {
        List<Sm_HOF_Info> sm_HOF_Info_List = new ArrayList<>();
        Set<Entry<Integer, HOF_Info>> entrySet = heroIdToHOF_Info.entrySet();
        for (Entry<Integer, HOF_Info> entry : entrySet) {
            sm_HOF_Info_List.add(create_Sm_HOF_Info(entry.getValue()));
        }
        return sm_HOF_Info_List;
    }

    public static Sm_HOF_Info create_Sm_HOF_Info(HOF_Info hofInfo) {
        Sm_HOF_Info.Builder b = Sm_HOF_Info.newBuilder();
        b.setHeroId(hofInfo.getHeroId());
        b.setFavorLevel(hofInfo.getFavorLevel());
        b.setFavorStage(hofInfo.getFavorStage());
        b.setExp(hofInfo.getOvfFavoExp());
        return b.build();
    }
}
