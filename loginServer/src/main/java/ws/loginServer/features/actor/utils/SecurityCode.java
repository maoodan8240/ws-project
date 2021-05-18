package ws.loginServer.features.actor.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.EnumUtils;
import ws.protos.EnumsProtos.PlatformTypeEnum;


public class SecurityCode {
    private PlatformTypeEnum platformType; // 平台
    private String platformUid;// 平台Uid
    private int outerRealmId; // 哪一个显示服

    public SecurityCode() {
    }

    public SecurityCode(PlatformTypeEnum platformType, String platformUid, int outerRealmId) {
        this.platformType = platformType;
        this.platformUid = platformUid;
        this.outerRealmId = outerRealmId;
    }

    public PlatformTypeEnum getPlatformType() {
        return platformType;
    }

    public void setPlatformType(PlatformTypeEnum platformType) {
        this.platformType = platformType;
    }

    public int getOuterRealmId() {
        return outerRealmId;
    }

    public void setOuterRealmId(int outerRealmId) {
        this.outerRealmId = outerRealmId;
    }

    public String getPlatformUid() {
        return platformUid;
    }

    public void setPlatformUid(String platformUid) {
        this.platformUid = platformUid;
    }

    public String toJsonString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("platformType", platformType.toString());
        jsonObject.put("platformUid", platformUid);
        jsonObject.put("outerRealmId", outerRealmId);
        jsonObject.put("currentTimeMillis", System.currentTimeMillis());
        return jsonObject.toJSONString();
    }

    public static SecurityCode parseFromJsonString(String jsonString) {
        JSONObject jsonObject = (JSONObject) JSONObject.parse(jsonString);
        SecurityCode securityCode = new SecurityCode();
        securityCode.setPlatformType(EnumUtils.getEnum(PlatformTypeEnum.class, jsonObject.getString("platformType")));
        securityCode.setOuterRealmId(jsonObject.getIntValue("outerRealmId"));
        securityCode.setPlatformUid(jsonObject.getString("platformUid"));
        return securityCode;
    }
}
