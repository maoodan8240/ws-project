package ws.analogClient.features.flow;

import com.alibaba.fastjson.JSON;
import ws.analogClient.features.Action;
import ws.analogClient.features.ActionResult;
import ws.analogClient.system.config.AppConfig;
import ws.common.utils.http.WsHttpClient;

public class SdkLoginAction implements Action {
    @Override
    public ActionResult run(Object... objects) {
        String account = objects[0].toString();
        String pswd = AppConfig.getString(AppConfig.Key.WS_AnalogClient_paswd);
        String re = WsHttpClient.httpSynSend_Get("http://" + AppConfig.getString(AppConfig.Key.WS_AnalogClient_sdk_ip) + "/sdk/login?accountName=" + account + "&password=" + pswd);
        boolean rs = (boolean) JSON.parseObject(re).get("_success");
        String token = JSON.parseObject(re).get("sessionId").toString();
        return new ActionResult(rs, token, account);
    }
}