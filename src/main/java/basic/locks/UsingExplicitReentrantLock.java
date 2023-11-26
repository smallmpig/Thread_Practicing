package basic.locks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Reentrant Lock 特性與 synchronized 相同，屬於互斥鎖，具備可見性(Visibility)
 *
 * 與synchronized 不同得是，實作了其他功能，例如公平鎖，trylock 等等
 *
 * 公平鎖:代表所有的鎖都有獲得執行的機會，按照鎖請求的順序來管控鎖資源(使用Queue)
 * 非公平鎖:亦即不使用Queue 來操作
 *
 */
public class UsingExplicitReentrantLock {

    ReentrantLock reentrantLock=new ReentrantLock();

    boolean state;

    public void lockMyHearth(){
        reentrantLock.lock();

        try{
            //state 會有序地進行切換
            System.out.println("Changing stated in a serialized way");
            state = !state;
            System.out.println("Changed: " + state);
        }finally {
            reentrantLock.unlock();
        }
    }

    public void lockMyHearthWithTiming() throws InterruptedException {

        if(!reentrantLock.tryLock(1l, TimeUnit.SECONDS)){
            //會讓其他執行緒取得鎖失敗
            System.err.println("Failed to acquire the lock - it's already held.");
        }else{
            try {
                System.out.println("Simulating a blocking computation - forcing tryLock() to fail");
                Thread.sleep(2000);
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    public static void main(String[] args){
        ExecutorService executor = Executors.newCachedThreadPool();
        UsingExplicitReentrantLock self=new UsingExplicitReentrantLock();

        for (int i = 0; i < 100; i++) {
            executor.execute(self::lockMyHearth);
        }

        for (int i = 0; i < 40; i++) {
            executor.execute(() -> {
                try {
                    self.lockMyHearthWithTiming();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        executor.shutdown();

    }
}
