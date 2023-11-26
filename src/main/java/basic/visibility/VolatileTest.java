package basic.visibility;

import java.util.concurrent.TimeUnit;

/**
 * 再沒有加上 Volatile關鍵字，代表stop 無法被其他thread 可見
 * 故無法停止
 *
 */
public class VolatileTest {

//    private static volatile boolean stop=false;
    private static boolean stop=false;

   public static void main(String[] args){

       new Thread(()->{
           while (!stop){

           }
           System.out.println("Thread stop");
       }).start();

       try {
           TimeUnit.SECONDS.sleep(1);
       }catch (InterruptedException ex){

       }
       stop=true;
   }
}
