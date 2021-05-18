package ws.gameServer.features.standalone.extp.signin;


import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.signin.ctrl.SigninCtrl;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.SigninProtos.Cm_Signin;
import ws.protos.SigninProtos.Cm_Signin.Action;
import ws.protos.SigninProtos.Sm_Signin;
import ws.relationship.topLevelPojos.signin.Signin;
import ws.relationship.utils.ProtoUtils;


public class SigninExtp extends AbstractPlayerExtension<SigninCtrl> {
    public static boolean useExtension = true;

    public SigninExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(SigninCtrl.class, Signin.class);
    }

    @Override
    public void _initReference() throws Exception {
        getControlerForQuery()._initReference();
    }

    @Override
    public void _postInit() throws Exception {
        getControlerForQuery()._initAll();
    }

    @Override
    public void onRecvMyNetworkMsg(Message clientMsg) throws Exception {
        if (clientMsg instanceof Cm_Signin) {
            Cm_Signin cm = (Cm_Signin) clientMsg;
            onCm_Signin(cm);
        }
    }

    @Override
    public void onRecvInnerMsg(InnerMsg innerMsg) throws Exception {
        if (innerMsg instanceof In_PlayerLoginedRequest) {
            onPlayerLogined((In_PlayerLoginedRequest) innerMsg);
        } else if (innerMsg instanceof In_DayChanged) {
            onDayChanged();
        }
    }

    @Override
    public void onRecvPrivateMsg(PrivateMsg privateMsg) throws Exception {
    }

    private void onCm_Signin(Cm_Signin cm) throws Exception {
        Sm_Signin.Builder b = Sm_Signin.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_Signin.Action.SYNC_VALUE:
                    b.setAction(Sm_Signin.Action.RESP_SYNC);
                    sync();
                    break;
                case Cm_Signin.Action.SIGNIN_VALUE:
                    b.setAction(Sm_Signin.Action.RESP_SIGNIN);
                    signin();
                    break;
                case Cm_Signin.Action.COMULATIVE_REWARD_VALUE:
                    b.setAction(Sm_Signin.Action.RESP_COMULATIVE_REWARD);
                    comulativeReward();
                    break;
                case Action.ADD_VIP_SIGNIN_VALUE:
                    b.setAction(Sm_Signin.Action.RESP_ADD_VIP_SIGNIN);
                    addVipSignIn();
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_Signin, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
    }

    private void onPlayerLogined(In_PlayerLoginedRequest im_PlayerLogined) throws Exception {
        if (im_PlayerLogined.getPlayerId().equals(ownerCtrl.getTarget().getPlayerId())) {
            // 本玩家, 同步信息
            sync();
        }
    }

    private void onDayChanged() throws Exception {
        getControlerForQuery()._resetDataAtDayChanged();
        getControlerForQuery().sync();
    }

    private void sync() {
        getControlerForQuery().sync();
    }

    private void signin() {
        getControlerForQuery().signin();
    }

    private void comulativeReward() {
        getControlerForQuery().comulativeReward();
    }

    private void addVipSignIn() {
        getControlerForQuery().addVipSignin();
    }

}
