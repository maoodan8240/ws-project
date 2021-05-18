package ws.analogClient.features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlowExecutor.class);

    public static void run() {
        Object[] rs = null;
        for (Flow flow : Flow.values()) {
            ActionResult result = flow.getAction().run(rs);
            if (!result.isRs()) {
                LOGGER.warn("flow={}执行失败了，流程停止！", flow);
                break;
            }
            rs = result.getRets();
            LOGGER.info("flow={}执行成功了，流程继续！", flow);
        }
    }

}
