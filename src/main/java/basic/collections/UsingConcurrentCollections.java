package basic.collections;

import java.util.*;
import java.util.concurrent.*;

/**
 * 使用sdk 的Concurrency collection，因為實作方式不同所以比
 * Syncronized Collection 有更好的效能
 * 使用Concurrency Map iterator 可以參考  fail-fast Iterators fail-safe Iterator
 *
 * lock striping 參考這篇 https://github.com/alimate/lock-striping
 *
 *
 *
 */
public class UsingConcurrentCollections {


    /**
     * 用於多執行緒處理的 Map
     *
     * 使用了鎖分離(Lock Striping)，將Hash 表攤開，使用一組鎖來做同步的處理
     * 需要注意的問題
     * 1.不要throw ConcurrentModificationException
     * 2.size 以及 isEmpty 會有一致性問題，不可靠
     * 3.支援園子性操作，調用者不用額外加鎖
     * 4.可以併行讀取，但是一致性較弱的原因所以使用 iterator 會不準確
     *
     */
    public static void usingConcurrencyMap(){
        System.out.println("=== ConcurrentHashMap ===");

        ExecutorService executors=Executors.newCachedThreadPool();
        Random random=new Random();

        Map<UUID,Integer> valuesPerUuid = new ConcurrentHashMap<UUID, Integer>();

        valuesPerUuid.put(UUID.randomUUID(),random.nextInt());

        for (int i = 0; i < 100; i++) {
            if (i % 6 == 0) {
                // write
                executors.execute(() -> {
                    UUID uuid = UUID.randomUUID();
                    Integer value = random.nextInt(10);
                    System.out.println("Added " + uuid + " - " + value);
                    valuesPerUuid.putIfAbsent(uuid, value);
                });
            } else {
                // read
                executors.execute(() -> System.out.println("Printed " + valuesPerUuid.values().toString()));
            }
        }

        // Finishing
        executors.shutdown();
        try {
            executors.awaitTermination(2000, TimeUnit.SECONDS);
            // space for other examples
            Thread.sleep(2000);
            System.out.println("\n\n\n\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 作為synchronized list 的取代，基於Immutable Object 的蓋念
     *
     * 在讀取多於寫入的狀況下使用
     * 當更新時，會複製一個新的List 來使用，使用較短的 synchronized 來確保可見性
     *
     * 使用iterator 時會複製一個目前狀態的副本
     *
     * 支援原子性操作
     *
     */
    public static void usingCopyOnWriteArrayList(){
        System.out.println("=== CopyOnWriteArrayList ===");
        ExecutorService executor = Executors.newCachedThreadPool();
        Random random = new Random();
        // No ConcurrentModificationException
        List<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<Integer>();

        for (int i = 0; i < 100; i++) {
            if (i % 8 == 0) {
                // write
                executor.execute(() -> {
                    Integer value = random.nextInt(10);
                    System.err.println("Added " + value);
                    copyOnWriteArrayList.add(value);
                });
            } else {
                // read
                executor.execute(() -> {
                    StringBuilder builder = new StringBuilder();
                    for (Integer value : copyOnWriteArrayList) {
                        builder.append(value + " ");
                    }
                    System.out.println("Reading " + builder.toString());
                });
            }
        }

        // Finishing
        executor.shutdown();
        try {
            executor.awaitTermination(2000, TimeUnit.SECONDS);
            // space for other examples
            Thread.sleep(2000);
            System.out.println("\n\n\n\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 可用於生產者/消費者模式
     *
     * Blocking methods: put/take; Timed blocking methods: offer, poll;
     *
     *  Can be bounded or unbounded.
     *
     */
    public static void usingBlockingQueue() {
        System.out.println("=== BlockingQueue ===");

        // Bounded UUID queue
        LinkedBlockingQueue<UUID> uuidQueue = new LinkedBlockingQueue<UUID>(10);

        System.out.println("Queue will execute for 10s");

        // Multiple consumers
        Runnable runConsumer = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    UUID uuid = uuidQueue.take();
                    System.out.println("Consumed: " + uuid + " by " + Thread.currentThread().getName());

                } catch (InterruptedException e) {
                    // interrupted pattern
                    // InterruptedException makes isInterrupted returns false
                    Thread.currentThread().interrupt();
                    System.err.println("Consumer Finished");
                }
            }
        };
        Thread consumer1 = new Thread(runConsumer);
        consumer1.start();
        Thread consumer2 = new Thread(runConsumer);
        consumer2.start();

        // Producer Thread
        Runnable runProducer = () -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Random r = new Random();
                    // Delay producer
                    Thread.sleep(r.nextInt(1000));
                    UUID randomUUID = UUID.randomUUID();
                    System.out.println("Produced: " + randomUUID + " by " + Thread.currentThread().getName());
                    uuidQueue.put(randomUUID);
                }
            } catch (InterruptedException e) {
                // interrupted pattern
                System.err.println("Producer Finished");
            }
        };

        // Multiple producers - Examples using simple threads this time.
        Thread producer1 = new Thread(runProducer);
        producer1.start();
        Thread producer2 = new Thread(runProducer);
        producer2.start();
        Thread producer3 = new Thread(runProducer);
        producer3.start();

        try {
            // Queue will run for 10secs
            Thread.sleep(10000);
            producer1.interrupt();
            producer2.interrupt();
            producer3.interrupt();
            consumer1.interrupt();
            consumer2.interrupt();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        usingConcurrencyMap();
        usingCopyOnWriteArrayList();
        usingBlockingQueue();

    }


}
