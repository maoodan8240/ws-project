import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducerConsumerTest {
    public static void main(String args[]) throws InterruptedException {

        //应用场景,生产者不停生产数据添加到BlockingQueue中,消费者不断从Queue中取出数据消费,两者同时进行,不会出现超消
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(10);
        Producer producer = new Producer(queue);
        Producer producer1 = new Producer(queue);
        Producer producer2 = new Producer(queue);
        Consumer consumer = new Consumer(queue);
        Consumer consumer1 = new Consumer(queue);
        Consumer consumer2 = new Consumer(queue);

        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(producer);
        service.execute(producer1);
        service.execute(producer2);
        service.execute(consumer);
        service.execute(consumer1);
        service.execute(consumer2);

        Thread.sleep(10 * 1000);
        producer.stop();
        producer1.stop();
        producer2.stop();

        Thread.sleep(2000);
        service.shutdown();
    }
}

class Producer implements Runnable {
    private volatile boolean isRunning = true;
    private BlockingQueue<String> queue;


    public static AtomicInteger count = new AtomicInteger();
    private static final int SLEEP = 1000;

    public Producer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        String data = null;
        Random random = new Random();
        System.out.println("启动生产者线程!");
        try {
            while (isRunning) {
                System.out.println("正在生产数据");
                Thread.sleep(random.nextInt(SLEEP));
                data = "data" + count.incrementAndGet();
                if (!queue.offer(data, 2, TimeUnit.SECONDS)) {
                    System.out.println("放入数据失败");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("退出生产者线程");
        }

    }

    public void stop() {
        isRunning = false;
    }

}

class Consumer implements Runnable {
    private BlockingQueue<String> queue;
    public static final int SLEEP = 1000;

    public Consumer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        System.out.println("启动消费者线程");
        Random random = new Random();
        boolean isRunnig = true;
        try {
            while (isRunnig) {
                System.out.println("正在从队列中获取数据");
                String data = queue.poll(2, TimeUnit.SECONDS);
                if (data != null) {
                    System.out.println("获取到数据:" + data);
                    System.out.println("正在消费");
                    System.out.println("队列长度:" + queue.size());
                    Thread.sleep(random.nextInt(SLEEP));
                } else {
                    isRunnig = false;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("退出消费者线程");
            System.out.println("总生产数:" + Producer.count);
        }

    }
}