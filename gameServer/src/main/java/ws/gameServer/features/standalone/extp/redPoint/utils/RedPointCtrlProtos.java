package ws.gameServer.features.standalone.extp.redPoint.utils;

import ws.protos.EnumsProtos.RedPointEnum;
import ws.protos.RedPointProtos.Sm_RedPoint_Info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class RedPointCtrlProtos {

    public static List<Sm_RedPoint_Info> create_Sm_RedPoint_Info_List(HashMap<RedPointEnum, Boolean> redPointToShow) {
        List<Sm_RedPoint_Info> smRedPointInfoList = new ArrayList<>();
        for (Entry<RedPointEnum, Boolean> entry : redPointToShow.entrySet()) {
            smRedPointInfoList.add(create_Sm_RedPoint_Info(entry));
        }
        return smRedPointInfoList;
    }

    public static Sm_RedPoint_Info create_Sm_RedPoint_Info(Entry<RedPointEnum, Boolean> entry) {
        Sm_RedPoint_Info.Builder b = Sm_RedPoint_Info.newBuilder();
        b.setRedPoint(entry.getKey());
        b.setShow(entry.getValue());
        return b.build();
    }

}
