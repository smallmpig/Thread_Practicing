package basic.locks;

public class ProducerAndConsumer {


    private static class Producer implements Runnable{
        private Clerk clerk;
        public Producer(Clerk clerk){
            this.clerk=clerk;
        }

        @Override
        public void run() {

            for(int i=0;i<100;i++){
                try {
                    Thread.sleep((int) (Math.random() * 1000));
                }catch (InterruptedException ex){
                    //ignored
                }
                try {
                    clerk.setProduct(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private static class Consumer implements Runnable{

        private Clerk clerk;
        public Consumer(Clerk clerk){
            this.clerk=clerk;
        }

        @Override
        public void run() {

            for(int i=0;i<100;i++){
                try {
                    Thread.sleep((int) (Math.random() * 1000));
                }catch (InterruptedException ex){
                    //ignored
                }
                try {
                    clerk.getProduct();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }


    private static class Clerk{
        int product=-1;

        public void getProduct() throws InterruptedException {

            synchronized (this){
                while (product==-1){
                    this.wait();
                }
                System.out.println("consume product :"+ this.product);
                this.product=-1;
                notify();
            }

        }

        public void setProduct(int prod) throws InterruptedException {

            synchronized (this){
                while (product!=-1){
                    this.wait();
                }
                this.product=prod;
                System.out.println("produce product :"+ this.product);
                this.notify();
            }
        }

        public static void main(String[] args){
            Clerk clerk=new Clerk();

            Thread t1=new Thread(new Producer(clerk));
            Thread t2=new Thread(new Consumer(clerk));

            t1.start();
            t2.start();

        }


    }



}
