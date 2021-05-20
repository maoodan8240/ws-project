package a;

import java.util.concurrent.locks.LockSupport;

/**
 * Created by lee on 16-10-27.
 */
public class T31 {

    static class Unlock implements Runnable {
        private Thread t;

        public Unlock(Thread t) {
            this.t = t;
        }

        @Override
        public void run() {
            for (int i = 1; i < 10; i++) {
                try {
                    Thread.sleep(1000l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(i + " 秒");
            }
            LockSupport.unpark(t);
        }
    }

    static class Lock implements Runnable {
        private Thread t;

        public Lock(Thread t) {
            this.t = t;
        }

        @Override
        public void run() {
            LockSupport.park();
        }
    }


    public static void main(String[] args) {
        LockSupport.unpark(Thread.currentThread());
        LockSupport.unpark(Thread.currentThread());
        LockSupport.unpark(Thread.currentThread());
        LockSupport.unpark(Thread.currentThread());
        LockSupport.unpark(Thread.currentThread());

        new Thread(new Unlock(Thread.currentThread())).start();
        System.out.println(">>>>>>>>>");
        LockSupport.park();
        System.out.println("解锁了...a");
        LockSupport.park();
        System.out.println("解锁了...b");
        LockSupport.park();
        System.out.println("解锁了...c");
        LockSupport.park();
        System.out.println("解锁了...d");
        LockSupport.park();
        System.out.println("解锁了...e");
        LockSupport.park();
        System.out.println("解锁了...f");
        LockSupport.park();
        System.out.println("解锁了...g");
    }
}
