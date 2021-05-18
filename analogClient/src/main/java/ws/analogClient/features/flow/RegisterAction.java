package ws.analogClient.features.flow;

import org.apache.commons.lang3.StringUtils;
import ws.analogClient.features.Action;
import ws.analogClient.features.ActionResult;
import ws.analogClient.features.utils.ClientUtils;
import ws.analogClient.system.config.AppConfig;
import ws.protos.EnumsProtos.SexEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PlayerLoginProtos.Cm_Login;
import ws.protos.PlayerLoginProtos.Sm_Login;

public class RegisterAction implements Action {
    @Override
    public ActionResult run(Object... objects) {
        if (!StringUtils.isEmpty(objects[1].toString())) {
            return new ActionResult(true, objects);
        }
        Cm_Login.Builder b1 = Cm_Login.newBuilder();
        b1.setAction(Cm_Login.Action.REGISTER);
        b1.setSecurityCode(objects[0].toString());
        b1.setPlayerName(objects[3].toString());
        b1.setPlayerIcon(1);
        b1.setSex(SexEnum.MALE);
        b1.setSpecifiedGameServerRole(AppConfig.getString(AppConfig.Key.WS_AnalogClient_game_server_role));

        Response response = ClientUtils.send(b1.build(), Sm_Login.Action.RESP_REGISTER);
        if (response != null && response.getSmLogin() != null) {
            Sm_Login login = response.getSmLogin();
            if (login.getAction() == Sm_Login.Action.RESP_REGISTER) {
                if (!response.getResult()) {
                    return new ActionResult(false);
                } else {
                    return new ActionResult(true, objects[0].toString(), login.getVpid(), login.getRpid());
                }
            }
        }
        return new ActionResult(false);
    }
}
