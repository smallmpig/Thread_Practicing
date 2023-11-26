package basic.visibility;

/**
 * 此處展現在缺乏可見性與原子性的狀況下
 * 數值如何被寫壞
 * ps 要在32位元下執行才會出現
 */
public class MultiThreadLong {
//    private static long to;
    //加上關鍵字在jvm 會特別處理這個變數確保所有資源可見
    private static volatile long to;

    private static class ChangeT implements Runnable{

        private long to;

        public ChangeT(long val){
            this.to=val;
        }

        @Override
        public void run() {
            while (true){
                MultiThreadLong.to=to;
                Thread.yield();
            }
        }
    }

    private static class ReadT implements Runnable{

        @Override
        public void run() {

            while (true){
                long tmp=MultiThreadLong.to;
                if(tmp!=111L && tmp!=-999L && tmp!=333L && tmp!=-444L){
                    System.out.println(tmp);
                }
                Thread.yield();
            }
        }
    }

    public static void main(String[] args){
        new Thread(new ChangeT(111L)).start();
        new Thread(new ChangeT(-999L)).start();
        new Thread(new ChangeT(333L)).start();
        new Thread(new ChangeT(-444L)).start();
        new Thread(new ReadT()).start();
    }
}
