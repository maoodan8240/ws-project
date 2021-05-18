package ws.analogClient.features.flow.functions;

import ws.analogClient.features.utils.ClientUtils;
import ws.protos.PlayerProtos.Cm_HeartBeat;
import ws.protos.PlayerProtos.Sm_HeartBeat;

public class Func_HeartBeat {

    public static void execute() {
        test1();
    }

    private static void test1() {
        Thread t = new Thread(() -> {
            while (true) {
                System.out.println("开始发送心跳......");
                Cm_HeartBeat.Builder b1 = Cm_HeartBeat.newBuilder();
                b1.setAction(Cm_HeartBeat.Action.SYNC);
                ClientUtils.send(b1.build(), Sm_HeartBeat.Action.RESP_SYNC);
                try {
                    Thread.sleep(30 * 1000l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}

