package a;

import java.util.concurrent.locks.LockSupport;

/**
 * Created by lee on 16-10-27.
 */
public class T32 {

    static class Unlock implements Runnable {
        private Thread t;

        public Unlock(Thread t) {
            this.t = t;
        }

        @Override
        public void run() {
            LockSupport.park(1);
            System.out.println("解除阻塞...");
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

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(new Unlock(Thread.currentThread()));
        t.start();
        Thread.sleep(2000l);
        t.interrupt();
        Thread.sleep(2000l);
        // t.stop();
        Thread.sleep(2000l);
        LockSupport.unpark(t);
        LockSupport.unpark(t);
        LockSupport.unpark(t);

        Object obj = LockSupport.getBlocker(t);
        System.out.println("park obj = " + obj);
        Thread.sleep(5000l);
    }
}
