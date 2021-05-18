package ws.gameServer.system.logHandler;

public enum LogExcep {

    POJO_DATA("POJO异常数据："),

    LOGIC_RETURN("逻辑RETURN："),

    LOGIC_WARN("逻辑警告："),

    LOGIC_EXCEP("逻辑异常：");

    private LogExcep(String head) {
        this.head = head;
    }

    private String head;

    public String getHead() {
        return head;
    }
}
