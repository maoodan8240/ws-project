package a;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lee on 16-11-1.
 */
public class Ta1 {
    private static final ReentrantLock lock = new ReentrantLock(true);

    public void exe() {
        lock.lock();
        try {
            Thread.sleep(1000000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        Ta1 t = new Ta1();
        Thread th1 = new Thread(new Runnable() {
            @Override
            public void run() {
                t.exe();
            }
        });

        Thread th2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("th2222 sleeping");
                    Thread.sleep(1000 * 20l);
                    System.out.println("th2222 sleep end");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                t.exe();
            }
        });

        Thread th3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("th3333 sleeping");
                    Thread.sleep(1000 * 100l);
                    System.out.println("th3333 sleep end");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                t.exe();
            }
        });


        th1.setName("th1111");
        th2.setName("th2222");
        th3.setName("th3333");
        th1.start();
        th2.start();
        th3.start();

        Thread th4 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("th4444 sleeping");
                    Thread.sleep(1000 * 40l);
                    System.out.println("th4444 sleep end");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("th4444 去停止 th2222 .... ");
                th2.stop();
            }
        });
        th4.start();
    }
}
