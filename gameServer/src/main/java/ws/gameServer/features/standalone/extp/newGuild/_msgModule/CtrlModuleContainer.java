package ws.gameServer.features.standalone.extp.newGuild._msgModule;

import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.extp.newGuild._msgModule.action.NewGuildRedBagCtrl;
import ws.gameServer.features.standalone.extp.newGuild._msgModule.action.NewGuildResearchCtrl;
import ws.gameServer.features.standalone.extp.newGuild._msgModule.action.NewGuildTrainCtrl;
import ws.gameServer.features.standalone.extp.newGuild.ctrl.NewGuildCtrl;
import ws.gameServer.features.standalone.extp.utils.ClientMsgActionStateful;
import ws.protos.NewGuildRedBagProtos.Cm_NewGuildRedBag;
import ws.protos.NewGuildResearchProtos.Cm_NewGuildResearch;
import ws.protos.NewGuildTrainProtos.Cm_NewGuildTrain;

import java.util.ArrayList;
import java.util.List;

public class CtrlModuleContainer<T extends Message> {
    private List<CtrlModuleContainer> containers = new ArrayList<CtrlModuleContainer>();

    private void init() {
        containers.clear();
        // -------------------------------------

        // 研究所
        CtrlModuleContainer<Cm_NewGuildResearch> researchCtrl = new CtrlModuleContainer<>(new NewGuildResearchCtrl(), Cm_NewGuildResearch.class);
        // 训练所
        CtrlModuleContainer<Cm_NewGuildTrain> trainCtrl = new CtrlModuleContainer<>(new NewGuildTrainCtrl(), Cm_NewGuildTrain.class);
        // 红包
        CtrlModuleContainer<Cm_NewGuildRedBag> redBagCtrl = new CtrlModuleContainer<>(new NewGuildRedBagCtrl(), Cm_NewGuildRedBag.class);

        containers.add(researchCtrl);
        containers.add(trainCtrl);
        containers.add(redBagCtrl);
        // -------------------------------------
    }

    private ClientMsgActionStateful action;
    private Class<T> msgClass;

    public CtrlModuleContainer() {
        init();
    }

    public CtrlModuleContainer(ClientMsgActionStateful action, Class<T> msgClass) {
        this.action = action;
        this.msgClass = msgClass;
    }

    public void onRecv(Message clientMsg, NewGuildCtrl exteControler) throws Exception {
        for (CtrlModuleContainer ac : containers) {
            if (ac.msgClass.equals(clientMsg.getClass())) {
                ac.action.onRecv(clientMsg, exteControler);
            }
        }
    }

    public void onRecvInnerMsg(InnerMsg innerMsg, NewGuildCtrl exteControler) throws Exception {
        for (CtrlModuleContainer ac : containers) {
            ac.action.onRecvInnerMsg(innerMsg, exteControler);
        }
    }

    public void onRecvPrivateMsg(PrivateMsg privateMsg, NewGuildCtrl controlerForQuery) {
        for (CtrlModuleContainer ac : containers) {
            ac.action.onRecvPrivateMsg(privateMsg, controlerForQuery);
        }
    }
}
