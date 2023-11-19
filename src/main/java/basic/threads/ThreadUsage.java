package basic.threads;

public class ThreadUsage {

    /**
     * java 提供做基本的thread 用法
     */
    public static void main(String[] args) throws InterruptedException {

        //建立thread 的幾種方法
        Thread thread=new Thread();
        thread.start();

        Thread threadWithTask=new Thread(()->{
            System.out.println("Say Hello");
        });
        threadWithTask.start();

        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()){
                    System.out.println("Say hi");
                }
            }
        };
        Thread interrupatblyTask=new Thread(runnable);

        interrupatblyTask.start();
        Thread.sleep(3000);
        interrupatblyTask.interrupt();
    }


}
