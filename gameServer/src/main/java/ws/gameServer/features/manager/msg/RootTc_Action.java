package ws.gameServer.features.manager.msg;

import ws.gameServer.features.manager.Action;

public class RootTc_Action implements Action {
    private static final String refresh = "refresh";

    @Override
    public String handle(String funcName, String args) {
        if (refresh.equals(funcName)) {
            return refresh(args);
        }
        return null;
    }

    private String refresh(String args) {
        return args;
    }
}