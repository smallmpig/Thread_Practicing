package basic.locks;

/**
 * Q 答案會是多少
 * A 不知道，因為count 是屬於Immutable Object
 * https://matthung0807.blogspot.com/2020/04/java-immutable-class_3.html#google_vignette
 * 1. 不提供setter 方法
 * 2. 所有的屬性宣告為private final使其無法被修改。
 * 3. 類別宣告為final class使無法被繼承，方法無法被覆寫。或是把建構式設為private，並改以工廠方法提供物件的實例。
 */
public class BadLockUsing {

    private Integer count = new Integer(0);

    public void badLockCount() {
        for (int i = 0; i < 10000; i++) {
            // wrong answer because of Immutable Object without setter
            // so when get count lock count will return a new Object
            // that make thread lock different lock;
            // synchronized (count) {
            synchronized (this) {
                count++;
            }
        }
    }

    public Integer getCount() {
        return count;
    }

    public static void main(String[] args) throws InterruptedException {
        BadLockUsing badLockUsing = new BadLockUsing();

        Thread t1 = new Thread(() -> {
            badLockUsing.badLockCount();
        });

        Thread t2 = new Thread(() -> {
            badLockUsing.badLockCount();
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(badLockUsing.getCount());
    }
}
