package ws.particularFunctionServer.features.standalone.jmxManager;


import ws.particularFunctionServer.features.standalone.jmxManager._module.bean._GmCommand_Action;
import ws.particularFunctionServer.features.standalone.jmxManager._module.bean._GmMail_Action;
import ws.particularFunctionServer.features.standalone.jmxManager._module.bean._ReGetCenterPlayer_Action;

public enum ActionContainer {

    GmMail_Action(new _GmMail_Action()), //
    GmCommand_Action(new _GmCommand_Action()), //
    ReGetCenterPlayer_Action(new _ReGetCenterPlayer_Action()), //
    ;//

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
