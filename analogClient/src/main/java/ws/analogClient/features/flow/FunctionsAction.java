package ws.analogClient.features.flow;

import ws.analogClient.features.Action;
import ws.analogClient.features.ActionResult;
import ws.analogClient.features.flow.functions.Func_HeartBeat;
import ws.analogClient.features.flow.functions.guild.redbag.Func_Guild_RedBag;

public class FunctionsAction implements Action {

    @Override
    public ActionResult run(Object... objects) {
        Func_HeartBeat.execute();
        // Func_Gm.execute();
        // --- fuctions
        Func_Guild_RedBag.execute();
        return new ActionResult(false);
    }
}
