package basic.collections;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Synchronized Collection 意味著被操作的collection 是被一個
 * 用synchronized 方法封裝的public method
 * <p>
 * 在Atomic 的操作下視線程安全的(thread safe)，所以非原子性的操作 size+add
 * 就不是
 * <p>
 * It's required to use client side locking for compound actions.
 * <p>
 * Synchronized collections doesn't support concurrent iteration+modification.
 * They'll throw ConcurrentModificationException
 */
public class UsingSynchronizedCollection {


    public static void insertIfAbsent(Vector<Long> list, Long val) {

        synchronized (list) {
            boolean contains = list.contains(val);

            if (!contains) {
                list.add(val);
                System.out.println("Value added: " + val);
            }
        }
    }


    public static void insertIfAbsentUnsafe(Vector<Long> list, Long val) {
        boolean contains = list.contains(val);

        if (!contains) {
            list.add(val);
            System.out.println("Value added: " + val);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        ExecutorService executors = Executors.newCachedThreadPool();

        Vector<Long> vector = new Vector<>();

        Runnable insertIfAbsent = () -> {
            long millSecond = System.currentTimeMillis() / 1000;
            // insertIfAbsent(vector,millSecond);
            insertIfAbsentUnsafe(vector,millSecond);
        };

        for (int i = 0; i < 10001; i++) {
            executors.submit(insertIfAbsent);
        }
        executors.shutdown();
        executors.awaitTermination(4000, TimeUnit.SECONDS);

        // Using the wrappers for not sync collections
        // List<String> synchronizedList = Collections.synchronizedList(abcList);
        // Collections.synchronizedMap(m)
        // Collections.synchronizedXXX

    }
}
