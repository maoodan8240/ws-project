package ws.relationship.gm;

import ws.relationship.enums.GmCommandFromTypeEnum;

public interface GmCommand {
    void exec(GmCommandFromTypeEnum fromTypeEnum, String commandName, String[] args);
}
