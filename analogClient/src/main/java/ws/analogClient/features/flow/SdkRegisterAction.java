package ws.analogClient.features.flow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.analogClient.features.Action;
import ws.analogClient.features.ActionResult;
import ws.analogClient.system.config.AppConfig;
import ws.analogClient.system.config.AppConfig.Key;
import ws.common.utils.http.WsHttpClient;
import ws.protos.EnumsProtos;

public class SdkRegisterAction implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(SdkRegisterAction.class);

    @Override
    public ActionResult run(Object... objects) {
        String newAccount = ObjectId.get().toString();
        if (AppConfig.getBoolean(Key.WS_AnalogClient_use_old_acc)) {
            newAccount = AppConfig.getString(AppConfig.Key.WS_AnalogClient_old_acc_name);
            return new ActionResult(true, newAccount);
        }
        if (!StringUtils.isEmpty(AppConfig.getString(Key.WS_AnalogClient_new_acc_name))) {
            newAccount = AppConfig.getString(Key.WS_AnalogClient_new_acc_name);
        }
        String pswd = AppConfig.getString(AppConfig.Key.WS_AnalogClient_paswd);
        String re = WsHttpClient.httpSynSend_Get("http://" + AppConfig.getString(AppConfig.Key.WS_AnalogClient_sdk_ip) + "/sdk/register?newAccountName=" + newAccount + "&password=" + pswd);
        JSONObject jsonObject = JSON.parseObject(re);
        boolean rs = (boolean) jsonObject.get("_success");
        if (!rs) {
            int errorCode = (int) jsonObject.get("_errorCode");
            LOGGER.error("sdk 注册失败！errorCode={} ", EnumsProtos.ErrorCodeEnum.valueOf(errorCode));
        }
        return new ActionResult(rs, newAccount);
    }
}