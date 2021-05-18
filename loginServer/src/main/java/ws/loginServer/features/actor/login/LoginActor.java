package ws.loginServer.features.actor.login;

import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.loginServer.features.actor.login.branch.LoginActor_Auth;
import ws.loginServer.features.actor.login.branch.LoginActor_ChatAuth;
import ws.loginServer.features.actor.login.branch.LoginActor_Login;
import ws.loginServer.features.actor.login.branch.LoginActor_Register;
import ws.loginServer.features.actor.login.branch.LoginActor_RegisterResponse;
import ws.protos.ChatProtos.Cm_ChatServerLogin;
import ws.protos.PlayerLoginProtos.Cm_Login;
import ws.protos.PlayerLoginProtos.Sm_Login;
import ws.relationship.appServers.loginServer.player.msg.In_Register;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.msg.In_MessagePassToOtherServer;


public class LoginActor extends WsActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginActor.class);

    @Override
    public void onRecv(Object msg) throws Exception {
        if (msg instanceof In_MessagePassToOtherServer) {
            on_In_MessagePassToOtherServer((In_MessagePassToOtherServer) msg);
        } else if (msg instanceof In_Register.Response) {
            in_RegisterResponse((In_Register.Response) msg);
        }
    }

    private void on_In_MessagePassToOtherServer(In_MessagePassToOtherServer messagePassToOtherServer) throws Exception {
        Message message = messagePassToOtherServer.getMessage();
        if (message instanceof Cm_Login) {
            Cm_Login cm_Login = (Cm_Login) message;
            switch (cm_Login.getAction()) {
                case AUTH:
                    LoginActor_Auth.auth(cm_Login, messagePassToOtherServer.getConnFlag(), getContext(), self(), sender());
                    break;
                case REGISTER:
                    LoginActor_Register.register(cm_Login, messagePassToOtherServer.getConnFlag(), getContext(), self(), sender());
                    break;
                case LOGIN:
                    _login(cm_Login, messagePassToOtherServer.getConnFlag());
                    break;
                case RECONNECT:
                    _reconnect(cm_Login, messagePassToOtherServer.getConnFlag());
                    break;
                default:
                    break;
            }
        } else if (message instanceof Cm_ChatServerLogin) {
            Cm_ChatServerLogin cm_chatServerLogin = (Cm_ChatServerLogin) message;
            switch (cm_chatServerLogin.getAction()) {
                case AUTH:
                    LoginActor_ChatAuth.auth(cm_chatServerLogin, messagePassToOtherServer.getConnFlag(), getContext(), self(), sender());
                    break;
                default:
                    break;
            }
        }
    }

    private void in_RegisterResponse(In_Register.Response registerResponse) {
        LoginActor_RegisterResponse.registerResponse(registerResponse, self());
    }

    private void _login(Cm_Login cm_Login, String connFlag) throws Exception {
        LoginActor_Login.login(cm_Login, connFlag, Sm_Login.Action.RESP_LOGIN, getContext(), self(), sender());
    }

    private void _reconnect(Cm_Login cm_Login, String connFlag) throws Exception {
        LoginActor_Login.login(cm_Login, connFlag, Sm_Login.Action.RESP_RECONNECT, getContext(), self(), sender());
    }

}
