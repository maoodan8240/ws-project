package ws.analogClient.features.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.analogClient.features.utils.receiveModule.Module_Sm_Mail;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MessageHandlerProtos.Response;

/**
 * Created by lee on 17-4-6.
 */
public class MessageReceiveUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageReceiveUtils.class);

    public static void onRecv(Response response, Code code) {
        if (code == Code.Sm_Mail) {
            Module_Sm_Mail.on_Sm_Mail(response);
        }
    }


}
