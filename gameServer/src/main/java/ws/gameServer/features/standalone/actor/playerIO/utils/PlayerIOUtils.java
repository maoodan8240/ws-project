package ws.gameServer.features.standalone.actor.playerIO.utils;

import ws.gameServer.system.ServerGlobalData;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PlayerLoginProtos.Sm_OperationForbidden;
import ws.protos.PlayerLoginProtos.Sm_OperationForbidden.Action;
import ws.protos.errorCode.ErrorCodeProtos.ErrorCodeEnum;
import ws.relationship.topLevelPojos.centerPlayer.CenterPlayer;
import ws.relationship.topLevelPojos.sdk.realm.OuterToInnerRealmList;
import ws.relationship.topLevelPojos.sdk.realm.RealmStatus;
import ws.relationship.utils.ProtoUtils;

/**
 * Created by zhangweiwei on 17-7-27.
 */
public class PlayerIOUtils {

    /**
     * <pre>
     * 玩家是否可以操作
     * 先判断是否是白名单用户，直接进入操作
     * 黑名单用户 OR 被锁定的用户 不能进入
     * </pre>
     *
     * @param centerPlayer
     * @return
     */
    public static Response tryOperationForbidden(CenterPlayer centerPlayer) {
        OuterToInnerRealmList realmList = ServerGlobalData.getOuterToInnerRealmList(centerPlayer.getOuterRealmId());
        if (realmList.getRealmStatus() == RealmStatus.MAINTAIN) {
            // 维护状态，只有白名单可以进入操作
            if (centerPlayer.isWhite()) {
                return null;
            }
            return createSm_OperationForbiddenResponse(ErrorCodeEnum.OPERATION_FORBIDDEN_SYSTEM_MAINTAIN);
        } else {
            // 正常状态，黑名单不能进入操作
            if (centerPlayer.isBlack()) {
                return createSm_OperationForbiddenResponse(ErrorCodeEnum.OPERATION_FORBIDDEN_BLACK_PLAYER);
            }
            // 正常状态，被锁定的玩家不能进入操作
            if (centerPlayer.getLastLockTime() > System.currentTimeMillis()) {
                return createSm_OperationForbiddenResponse(ErrorCodeEnum.OPERATION_FORBIDDEN_BELOCKED_PLAYER);
            }
            return null;
        }
    }


    public static Response createSm_OperationForbiddenResponse(ErrorCodeEnum errorCode) {
        Response.Builder br = ProtoUtils.create_Response(Code.Sm_OperationForbidden, Action.RESP_FORBIDDEN, errorCode);
        Sm_OperationForbidden.Builder b = Sm_OperationForbidden.newBuilder();
        b.setAction(Action.RESP_FORBIDDEN);
        br.setSmOperationForbidden(b);
        return br.build();
    }
}
