package tool;

import com.google.protobuf.GeneratedMessage;

import java.util.Date;

/**
 * Created by lee on 17-3-23.
 */
public class PrintCmSm {

    private static void print(Class<? extends GeneratedMessage> msg) {
        System.out.println("  " + msg.getSimpleName() + " = \"" + msg.getName() + "\"");
    }

    public static void main(String[] args) {
//        print(Cm_RedPoint.class);
//        print(Sm_RedPoint.class);
        Date date = new Date(1492841414784l);
        System.out.println(date);
    }
}
