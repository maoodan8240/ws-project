package ws.sdk.manager;

import ws.sdk.manager._module.Realmlist_Action;

public enum ActionContainer {

    Realmlist(new Realmlist_Action()), //
    ;//

    private Action action;

    ActionContainer(Action action) {
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
