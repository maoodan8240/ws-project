package ws.gameServer.features.standalone.extp.dataCenter.enums;

public enum NotifyAddValueTypeEnum {
    /**
     * <pre>
     * 覆盖值，覆盖tiv(typeCode --- missionId --- value)中的value值
     * 前提: 新值 > tiv中的value值
     * </pre>
     */
    COVER,
    /**
     * <pre>
     * 递增值，增加到tiv(typeCode --- missionId --- value)中的value值
     * 前提: 递增值 > 0
     * </pre>
     */
    ADD
}
