package basic.locks;


/**
 * form leetcode 1115
 * 給予兩個runnable function 請依序print foolbar
 */
public class WaitAndNotify {


    private static class FoolBar {
        private int n = 0;
        //使用state 做切換
        private boolean state = false;

        public FoolBar(int n) {
            this.n = n;
        }

        public void printFoo(Runnable printFoo) throws InterruptedException {
            System.out.println("printFoo:" + Thread.currentThread().getId());
            for (int i = 0; i < n; i++) {
                synchronized (this) {
                    while (state) {
                        this.wait();
                    }
                    state = true;
                    printFoo.run();
                    this.notify();
                }

            }
        }

        public void printBar(Runnable printBar) throws InterruptedException {
            System.out.println("printBar:" + Thread.currentThread().getId());
            for (int i = 0; i < n; i++) {
                synchronized (this) {
                    while (!state) {
                        this.wait();
                    }
                    state = false;
                    printBar.run();
                    this.notify();
                }

            }
        }
    }

    public static void main(String[] args) {

        FoolBar foolBar = new FoolBar(5);

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
