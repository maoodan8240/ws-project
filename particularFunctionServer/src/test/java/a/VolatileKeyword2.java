package a;

/**
 * The volatile keyword is used as a modifier on member variables to force individual
 * threads to reread the variable’s value from shared memory every time the variable is
 * accessed. In addition, individual threads are forced to write changes back to shared
 * memory as soon as they occur. This way, two different threads always see the same
 * value for a member variable at any particular time.
 */
public class VolatileKeyword2 {

    static class A1 {
        private volatile int i = 10;

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }
    }


    static class Th1 implements Runnable {
        A1 t3;

        public Th1(A1 t3) {
            this.t3 = t3;
        }

        @Override
        public void run() {
            System.out.println(">>>>>>>>>>>>>>");
            int j = 0;
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                j++;
                if (j == 2) {
                    t3.setI(9);
                }
                System.out.println(j + " 秒...");
            }
        }
    }

    static class Th2 implements Runnable {
        public A1 t3;

        public Th2(A1 t3) {
            this.t3 = t3;
        }

        @Override
        public void run() {
            long t1 = System.currentTimeMillis();
            // 如果i非volatile变量，则永远无法跳出while, 因为每次获取的i值都为缓存在当前线程栈中的旧值;
            // i为volatile变量，当其他线程对i进行修改时，此处每次都会重新到主存中获取最新的i值
            while (t3.getI() == 10) {//
            }
            long t2 = System.currentTimeMillis();
            System.out.println("退出while循环花费时间：" + (t2 - t1));
        }
    }

    public static void main(String[] args) throws InterruptedException {
        A1 t3 = new A1();
        Th1 t1 = new Th1(t3);
        Th2 t2 = new Th2(t3);
        new Thread(t2).start();
        new Thread(t1).start();
    }
}
