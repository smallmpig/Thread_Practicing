package basic.synchronizer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 實用的多執行緒控制工具
 * 延遲目前的進程(progress)，直到所有的工作(thread)都被安排完成
 *
 *  使用時
 *  In CountDownLatch, each event adds 1. When ready, countDown() is called,
 *  decrementing by counter by 1. await() method releases when counter is 0.
 *  在所有安排的執行緒(event)中，當執行工作完成十，呼叫countDown，latch 會-1，當counter =0 時
 *  latch 會釋放await 讓主程序繼續
 *
 *  只能單次使用的 synchronizer
 */
public class UsingLatch {

    public static void main(String[] args){
        ExecutorService executors= Executors.newCachedThreadPool();
        CountDownLatch latch=new CountDownLatch(3);
        Runnable runnable=()->{
            try {
                Thread.sleep(1000);
                System.out.println("Service in " + Thread.currentThread().getName() + " initialized.");
                latch.countDown();
            }catch (InterruptedException ex){
                ex.printStackTrace();
            }

        };
        executors.execute(runnable);
        executors.execute(runnable);
        executors.execute(runnable);

        try {
            latch.await(2, TimeUnit.SECONDS);
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }

        executors.shutdown();
    }
}
