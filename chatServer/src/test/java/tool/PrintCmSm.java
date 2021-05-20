package tool;

import com.google.protobuf.GeneratedMessage;
import ws.protos.ChatProtos.Cm_Chat;
import ws.protos.ChatProtos.Sm_Chat;

import java.util.Date;

/**
 * Created by lee on 17-3-23.
 */
public class PrintCmSm {

    private static void print(Class<? extends GeneratedMessage> msg) {
        System.out.println("  " + msg.getSimpleName() + " = \"" + msg.getName() + "\"");
    }

    public static void main(String[] args) {
        print(Cm_Chat.class);
        print(Sm_Chat.class);
        Date date = new Date(1492841414784l);
        System.out.println(date);
    }
}
