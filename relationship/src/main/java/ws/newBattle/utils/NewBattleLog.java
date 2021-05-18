package ws.newBattle.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Created by zhangweiwei on 17-6-2.
 */
public class NewBattleLog {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewBattleLog.class);
    private StringBuffer sb = new StringBuffer();

    public void add(String content, Object... args) {
        try {
            String s = String.format(content, args);
            sb.append(s).append("\n");
        } catch (Exception e) {
            LOGGER.error("添加战斗log失败！content={} args={}", content, Arrays.toString(args), e);
        }
    }

    public void print() {
        LOGGER.debug("\n==================================================\n" +
                "战斗过程:\n{}\n" +
                "==================================================\n", sb.toString());
    }
}
