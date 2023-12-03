package basic.locks;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * condition 的await 跟 signal 同等於
 * synchronized 中的Object.wait() and Object.notify()
 * 同樣使用leetcode 的題目當作解法
 */
public class ReentrantWithCondition {

    private static class FoolBar {

        private ReentrantLock lock = new ReentrantLock(false);
        Condition condition = lock.newCondition();
        private int n = 0;
        //使用state 做切換
        private boolean state = false;

        public FoolBar(int n) {
            this.n = n;
        }

        public void printFoo(Runnable printFoo) throws InterruptedException {
            System.out.println("printFoo:" + Thread.currentThread().getId());
            for (int i = 0; i < n; i++) {
                try {
                    lock.lock();
                    while (state) {
                        condition.await();
                    }
                    state = true;
                    printFoo.run();
                    condition.signal();
                } finally {
                    lock.unlock();
                }
            }

        }


        public void printBar(Runnable printBar) throws InterruptedException {
            System.out.println("printBar:" + Thread.currentThread().getId());
            for (int i = 0; i < n; i++) {
                try {
                    lock.lock();
                    while (!state) {
                        condition.await();
                    }
                    state = false;
                    printBar.run();
                    condition.signal();
                } finally {
                    lock.unlock();
                }

            }
        }
    }

    public static void main(String[] args) {

        ReentrantWithCondition.FoolBar foolBar = new ReentrantWithCondition.FoolBar(5);

        Thread t1 = new Thread(() -> {
            System.out.println("t1:" + Thread.currentThread().getId());
            try {
                foolBar.printFoo(() -> System.out.print("foo"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread t2 = new Thread(() -> {
            System.out.println("t2:" + Thread.currentThread().getId());
            try {
                foolBar.printBar(() -> System.out.print("bar"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        t2.start();
    }
}
