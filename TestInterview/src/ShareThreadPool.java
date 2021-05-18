import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShareThreadPool {
    public static void main(String[] args) {

        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        for (int i = 0; i <= 10; i++) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            threadPool.execute(new Task(i));
        }
        threadPool.shutdown();
    }
}

class Task implements Runnable {
    private int taskID;

    public Task(int taskID) {
        this.taskID = taskID;
    }

    public void run() {
        System.out.println("执行第" + taskID + "次任务");
    }
}