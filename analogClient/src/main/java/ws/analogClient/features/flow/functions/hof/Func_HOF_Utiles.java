package ws.analogClient.features.flow.functions.hof;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.protos.CommonProtos.Sm_Common_IdMaptoCount;
import ws.protos.HOFProtos.Cm_HOF_HeroAndFood;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.utils.ProtoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 17-2-21.
 */
public class Func_HOF_Utiles {


    /**
     * GM命令合法化,加满经验食品
     */
    public static void legalisation() {
        Func_Gm.setLv(36);
        Func_Gm.addResource("1000001:1,4060001:99,4060501:99,4061001:9999");
    }

    /**
     * GM命令合法化添加不匹配的食品
     */
    public static void legalisation_2() {
        Func_Gm.setLv(36);
        Func_Gm.addResource("1000001:1,4060002:99,4060502:99,4061002:99");
    }


    public static List<Cm_HOF_HeroAndFood> create_Cm_HOF_HeroAndFood_List(int count) {
        List<Cm_HOF_HeroAndFood> list = new ArrayList<>();
        Cm_HOF_HeroAndFood.Builder b = Cm_HOF_HeroAndFood.newBuilder();
        b.setHeroId(100000001);
        Sm_Common_IdMaptoCount idMapToCount = ProtoUtils.create_Sm_Common_IdMaptoCount(new IdMaptoCount().add(new IdAndCount(4061001, count)));
        b.setFoodIdAndCount(idMapToCount);
        list.add(b.build());
        return list;
    }

}
