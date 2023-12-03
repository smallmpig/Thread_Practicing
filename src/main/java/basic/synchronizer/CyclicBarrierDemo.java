package basic.synchronizer;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {

    public static class Soldier implements Runnable {

        private final String soldier;
        private final CyclicBarrier cyclic;

        public Soldier(CyclicBarrier cyclic, String soldier) {
            this.soldier = soldier;
            this.cyclic = cyclic;
        }

        @Override
        public void run() {

            try {
                //第一次停止，等待所有人集合
                cyclic.await();
                doWork();
                //第二次停止，等待所有人工作完成
                cyclic.await();
            } catch (BrokenBarrierException | InterruptedException ex) {
                ex.printStackTrace();
            }

        }

        private void doWork() {
            try {
                Thread.sleep(new Random().nextInt(10) * 1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            System.out.println(soldier+":任務完成");

        }
    }

    public static class BarrierRun implements Runnable {

        boolean flag;
        int N;

        public BarrierRun(boolean flag, int N) {
            this.flag = flag;
            this.N = N;
        }

        @Override
        public void run() {
            if (flag) {
                System.out.println("司令:[士兵" + N + "個,任務完成]");
            } else {
                System.out.println("司令:[士兵" + N + "個,集合完畢]");
                flag = true;
            }
        }
    }

    public static void main(String[] args){
        final int N=10;
        Thread[] allSoldier=new Thread[N];
        boolean flag=false;

        CyclicBarrier cyclic=new CyclicBarrier(N,new BarrierRun(flag,N));
        System.out.println("集合隊伍");

        for(int i=0;i<N;i++){
            System.out.println("士兵 "+i +" 報到");
            allSoldier[i]=new Thread( new Soldier(cyclic,"士兵 "+i));
            allSoldier[i].start();

//            if(i==5){
//                allSoldier[0].interrupt();
//            }
            Object obj=new Object();
            ;
        }

    }
}
