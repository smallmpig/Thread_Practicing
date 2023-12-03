package basic.synchronizer;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 另一種多線程併發控制工具，類似於countdown latch
 * 但功能更為強大，差別主要在這是可重複使用的計數器
 *
 * 可以block 指定數量的thread，當執行緒到達barrier 時，可以觸發另一個執行緒進行等待
 * 全部完成後在往主程序進行
 *
 */
public class UsingBarrier {

    public static void main(String[] args){

        Runnable runAction=()->{System.out.println("well done gues");};

        CyclicBarrier barrier=new CyclicBarrier(10,runAction);

        ExecutorService executors= Executors.newCachedThreadPool();

        Runnable task=()->{

            try {
                System.out.println("Doing task for " + Thread.currentThread().getName());
                Thread.sleep(new Random().nextInt(10) * 100);
                System.out.println("Done for " + Thread.currentThread().getName());
                barrier.await();
            }catch (InterruptedException | BrokenBarrierException ex){
                ex.printStackTrace();
            }
        };

        for (int i=0;i<20;i++){
            executors.submit(task);
        }

        executors.shutdown();
    }
}
