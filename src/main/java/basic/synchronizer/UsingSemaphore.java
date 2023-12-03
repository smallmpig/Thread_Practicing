package basic.synchronizer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 允許多個線程同時訪問的控制方法
 * <p>
 * 1.設定允許最多可以有多少資源訪問
 * 2.資源可以被重複的使用以及釋放
 * 3.如果資源的訪問數量達到上限，就會block 等待其他執行緒釋放資源
 * <p>
 * ps 多個線程訪問同一個resource 會不保證一致性，即使使用Atomic
 */
public class UsingSemaphore {

    private static AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) {

        ExecutorService executors = Executors.newCachedThreadPool();
        //允許多少資源訪問
        Semaphore semaphore = new Semaphore(2);

        Runnable run = () -> {

            try {
                System.out.println("try to Acquire resource");

                if (semaphore.tryAcquire()) {

                    System.out.println("Acquire resource " + Thread.currentThread().getName());
                    Thread.sleep(1000);
                    count.addAndGet(1);
                    System.out.println("release resource " + Thread.currentThread().getName()+ "count "+count.get());
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } finally {
                //釋放資源
                semaphore.release();
            }
        };

        for (int i = 0; i < 20; i++) {
            executors.execute(run);
        }

        while (count.get() == 20) {
            executors.shutdown();
        }
    }

}
