package ws.analogClient.features.flow.functions.guild.research;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.flow.functions.guild.base.Func_Guild_Utils;
import ws.protos.EnumsProtos.GuildInstituteTypeEnum;
import ws.protos.EnumsProtos.GuildResearchProjectTypeEnum;
import ws.protos.EnumsProtos.GuildResearchTypeEnum;

/**
 * Created by lee on 6/5/17.
 */
public class Func_Guild_Research {
    public static void execute() {
        test1();
        test2();
        test2();
        test2();
        test2();
        test2();
    }

    public static void test1() {
        Func_Guild_Utils.sync();
        Func_Guild_Research_Utils.sync();
    }

    /**
     * 研究所升级
     */
    public static void test2() {
        Func_Gm.addResource("1:99999999");
        Func_Guild_Research_Utils.research(GuildResearchProjectTypeEnum.GR_GUILD_LV, GuildResearchTypeEnum.MONEY_RESEARCH, GuildInstituteTypeEnum.GI_LV);
    }
}
