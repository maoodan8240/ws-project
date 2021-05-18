package ws.logServer.system.network;

import com.google.protobuf.Message;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.network.exception.CodeOfMessagePrototypeAlreadyExistsException;
import ws.common.network.server.implement._CodeToMessagePrototype;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.relationship.exception.BusinessLogicMismatchConditionException;

import java.lang.reflect.Method;
import java.util.Map.Entry;

public class CodeToMsgPrototypeFromConfig extends _CodeToMessagePrototype {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeToMsgPrototypeFromConfig.class);
    private static final String CONFIG_NAME = "codeToMsgPrototype.conf";
    private static final String CODE_PREFIX = "ws.protos.";

    public CodeToMsgPrototypeFromConfig() {
        init();
    }

    private void init() throws CodeOfMessagePrototypeAlreadyExistsException {
        try {
            Config config = ConfigFactory.load(CONFIG_NAME);
            for (Entry<String, ConfigValue> entry : config.entrySet()) {
                if (entry.getKey().startsWith(CODE_PREFIX)) {
                    String codeStr = entry.getKey().replace(CODE_PREFIX, "");
                    Code code = Code.valueOf(codeStr);
                    add(code.getNumber(), dynamicGenMessage(config.getString(entry.getKey())));
                    LOGGER.debug("加载code=[{}]--->message=[{}]", codeStr, entry.getKey());
                }
            }
        } catch (Exception e) {
            String msg = String.format("加载消息码-消息原型出错！ e=%s", e);
            throw new BusinessLogicMismatchConditionException(msg);
        }
    }

    /**
     * 根据 proto的类型名称，动态生成Message
     *
     * @param protoTypeClassName
     * @return
     */
    private Message dynamicGenMessage(String protoTypeClassName) {
        try {
            Class clzz = Class.forName(protoTypeClassName);
            Method method = clzz.getMethod("newBuilder");
            Object builderObj = method.invoke(null);
            Method getInsMethod = builderObj.getClass().getMethod("getDefaultInstanceForType");
            return (Message) getInsMethod.invoke(builderObj);
        } catch (Exception e) {
            String msg = String.format("加载Proto类出错！ protoTypeClassName=%s", protoTypeClassName);
            throw new BusinessLogicMismatchConditionException(msg);
        }
    }
}
