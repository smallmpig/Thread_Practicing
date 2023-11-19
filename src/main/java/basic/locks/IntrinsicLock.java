package basic.locks;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 內部鎖intrinsic lock(或是 monitor lock) 是每個 java Object 內部相關的(associated with)
 * 意即每個Object 都可以被用作一個lock
 *
 * intrinsic lock 是屬於互斥鎖，代表同時只有一個thread 可以持有這個資源(synchronize)
 * 當method 結束、block 結束or 發生未被捕捉異常，資源才會被釋放
 *
 * Synchronize serializes access for what is locked and guarantee memory
 * visibility for the changes that happened inside the synchronized scope to all
 * threads.
 *
 * intrinsic lock 是可以重入的，意思是可以重複取得/釋放 lock，不會出現死鎖
 *
 * Question: primitive type 是否可以作為lock=>不行，因為
 */
public class IntrinsicLock {

    private boolean state;

    private int count;

    /**
     * 當 synchronized 使用在method 上時， 是使用this 當作lock
     *
     */
    public synchronized void firstSynchronizeMethod(){

        state=!state;
        count++;
        // 只有取得lock 的thread 才能使用此方法
        // 當沒有使用 synchronized state 跟count 就會成為無序的狀態 ex count 連
        // 加 100 次不會等於 100,state 不會按順序的切換
        System.out.println("My state:"+state);
        System.out.println("My count:"+count);

    }

    /**
     * synchronized 區域("{}")的使用
     */
    public void mySynchronizedBlock() {

        //lock 的機制只會發生在有 {} 的區域
        System.out.println("Who owns my lock: " + Thread.currentThread().getName());
        synchronized (this) {
            state = !state;
            System.out.println("Who owns my lock after state changes: " + Thread.currentThread().getName());
            System.out.println("State is: " + state);
            System.out.println("====");
        }
    }

    /**
     * 重入方法的測試
     *
     */
    public synchronized void reentrancy(){

        System.out.println("Before acquiring again");

        synchronized (this) {
            System.out.println("I'm own it! " + Thread.currentThread().getName());
        }

    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        IntrinsicLock self = new IntrinsicLock();
        //循序
        for (int i = 0; i < 100; i++) {
            executor.execute(() -> self.firstSynchronizeMethod());
        }
        Thread.sleep(1000);
        //作用區域循序
        for (int i = 0; i < 10; i++) {
            executor.execute(() -> self.mySynchronizedBlock());
        }
        Thread.sleep(1000);
        //循序的
        for (int i = 0; i < 10; i++) {
            executor.execute(() -> self.reentrancy());
        }
        executor.shutdown();
    }

}
