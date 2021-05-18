package ws.gameServer.features.manager;

import ws.gameServer.features.manager.msg.GmMail_Action;
import ws.gameServer.features.manager.msg.RootTc_Action;

public enum ActionContainer {

    GmMail_Action(new GmMail_Action()), //
    RootTc_Action(new RootTc_Action()),;//

    private Action action;

    private ActionContainer(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public static String handle(String actionName, String funcName, String args) {
        for (ActionContainer ac : values()) {
            if (ac.toString().equals(actionName)) {
                return ac.getAction().handle(funcName, args);
            }
        }
        return null;
    }

}
