package ws.analogClient.features;

public class ActionResult {
    private boolean rs;
    private Object[] rets;

    public ActionResult(boolean rs, Object... rets) {
        this.rs = rs;
        this.rets = rets;
    }

    public boolean isRs() {
        return rs;
    }

    public Object[] getRets() {
        return rets;
    }

}
