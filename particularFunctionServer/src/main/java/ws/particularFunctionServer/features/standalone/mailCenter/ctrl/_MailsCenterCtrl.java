package ws.particularFunctionServer.features.standalone.mailCenter.ctrl;

import akka.actor.ActorContext;
import org.bson.types.ObjectId;
import ws.common.utils.mc.controler.AbstractControler;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.particularFunctionServer.features.standalone.mailCenter.utils.MailsCenterUtils;
import ws.relationship.base.ServerRoleEnum;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.db.getter.In_AllCommon_Strings_OneIdOnePojoGetter;
import ws.relationship.base.msg.db.saver.In_AllCommon_Strings_OneIdOnePojoSaver;
import ws.relationship.base.msg.mail.In_HasNewGmMail;
import ws.relationship.base.msg.mail.In_SyncGmMail;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.topLevelPojos.mailCenter.GmMail;
import ws.relationship.topLevelPojos.mailCenter.MailsCenter;
import ws.relationship.utils.ActorMsgSynchronizedExecutor;
import ws.relationship.utils.ClusterMessageSender;
import ws.relationship.utils.InitCommonCreatedTargetsDB;
import ws.relationship.utils.RelationshipCommonUtils;

import java.util.List;
import java.util.Objects;


// todo 过期的邮件未删除， target一次性去一次性存，可以改为单条存取
public class _MailsCenterCtrl extends AbstractControler<MailsCenter> implements MailsCenterCtrl {
    private ActorContext actorContext;

    @Override
    public void sendPrivateMsg(PrivateMsg privateMsg) {

    }

    @Override
    public void setActorContext(ActorContext actorContext) {
        this.actorContext = actorContext;
    }

    @Override
    public List<GmMail> syncGmMail(In_SyncGmMail.Request request) {
        loadMailCenterFromDB();
        return MailsCenterUtils.filterGmMails(getTarget().getGmMails(), request);
    }

    @Override
    public void addNewGmMail(GmMail gmMail) {
        Objects.requireNonNull(gmMail, "新增的全服邮件gmMail不能为空！");
        loadMailCenterFromDB();
        target.getGmMails().add(gmMail);
        save();
        ClusterMessageSender.sendMsgToServer(actorContext, ServerRoleEnum.WS_GameServer, new In_HasNewGmMail.Request(), ActorSystemPath.WS_GameServer_Selection_Player);
    }


    /**
     * 从数据库加载数据
     *
     * @return
     */
    private void loadMailCenterFromDB() {
        if (this.getTarget() == null) {
            In_AllCommon_Strings_OneIdOnePojoGetter.Request request = new In_AllCommon_Strings_OneIdOnePojoGetter.Request(MailsCenter.class);
            In_AllCommon_Strings_OneIdOnePojoGetter.Response response = ActorMsgSynchronizedExecutor.sendMsgToSingleServerIgnoreEnv(ServerRoleEnum.WS_MongodbRedisServer, actorContext, ActorSystemPath.WS_MongodbRedisServer_Selection_PojoGetterManager, request);
            RelationshipCommonUtils.checkDbResponse(response);
            if (response.getTopLevelPojo() == null) {
                if (InitCommonCreatedTargetsDB.containsTopLevelPojo(MailsCenter.class.getSimpleName())) {
                    throw new BusinessLogicMismatchConditionException("MailsCenter已经创建过，但是获取失败，请检查！");
                }
            }
            if (response.getTopLevelPojo() == null) {
                newMailsCenter();
            } else {
                this.setTarget((MailsCenter) response.getTopLevelPojo());
            }
        }
    }


    /**
     * 创建新的MailsCenter
     */
    private void newMailsCenter() {
        this.setTarget(new MailsCenter(ObjectId.get().toString()));
        save();
        InitCommonCreatedTargetsDB.update(this.getTarget());
    }

    /**
     * 保存数据
     */
    private void save() {
        In_AllCommon_Strings_OneIdOnePojoSaver.Request request = new In_AllCommon_Strings_OneIdOnePojoSaver.Request(getTarget());
        ClusterMessageSender.sendMsgToServer(actorContext, ServerRoleEnum.WS_MongodbRedisServer, request, ActorSystemPath.WS_MongodbRedisServer_Selection_PojoSaver);
    }
}
