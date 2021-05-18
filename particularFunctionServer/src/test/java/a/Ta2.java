package a;

import java.util.concurrent.locks.LockSupport;

/**
 * Created by zhangweiwei on 16-11-1.
 */
public class Ta2 implements Runnable {


    @Override
    public void run() {
        try {
            Thread t = Thread.currentThread();
            System.out.println("暂停......" + t.getName() + " -" + t.getState());
            LockSupport.park();
            Thread t1 = Thread.currentThread();
            System.out.println("解除暂停......" + t1.getName() + " -" + t1.getState());
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            Thread t = Thread.currentThread();
            System.out.println("finally ......" + t.getName() + " -" + t.getState());
        }
        Thread t = Thread.currentThread();
        System.out.println("最后面......" + t.getName() + " -" + t.getState());
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(new Ta2());
        t.setName("hello");
        System.out.println("-------------------" + t.getName() + " -" + t.getState());
        t.start();
        for (int i = 1; i < 5; i++) {
            Thread.sleep(1000);
            System.out.println("暂停 " + i + " 秒 --> " + t.getName() + " -" + t.getState());
        }
        // System.out.println("去unpark...");
        // LockSupport.unpark(t);
        // Thread.sleep(2000);
        // System.out.println("-------------------" + t.getName() + " -" + t.getState());

        System.out.println("去interrupt..." + t.getName() + " -" + t.getState());
        t.interrupt();
        t.interrupt();
        t.interrupt();
        Thread.sleep(2000);
        System.out.println("-------------------" + t.getName() + " -" + t.getState());


        // System.out.println("去stop...");
        // t.stop();

        Thread.sleep(100000);
    }

}
