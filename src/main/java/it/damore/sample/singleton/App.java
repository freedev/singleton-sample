package singleton;

import java.util.concurrent.CountDownLatch;

public class App {
  
  public static class MySingleton {
    
    private final int initValue;
    
    public static class MySingletonHolder {
      private static final MySingleton mySingleton;
      static {
        System.out.println("THREAD SAFE " + Thread.currentThread().getName() + " static init before");
        mySingleton = new MySingleton(System.getProperties().size());
        System.out.println("THREAD SAFE " + Thread.currentThread().getName() + " static init after");
      }
    }
    
    private MySingleton(int value) {
      System.out.println("THREAD SAFE " + Thread.currentThread().getName() + " into the constructor before");
      this.initValue = value;
      System.out.println("THREAD SAFE " + Thread.currentThread().getName() + " into the constructor after");
    }
    
    public static MySingleton getInstance() {
      System.out.println(Thread.currentThread().getName() + " getInstance() before");
      MySingleton mysingleton = MySingletonHolder.mySingleton;
      System.out.println(Thread.currentThread().getName() + " getInstance() after");
      return mysingleton;
    }
    
    public int getInitValue() {
      System.out.println(Thread.currentThread().getName() + " getInitValue()");
      return initValue;
    }
  }
  
  public static void main(String[] args) throws InterruptedException {
    System.out.println("start");
    int size = 5;
    Runnable[] threads = new Runnable[size];
    final CountDownLatch latchBefore = new CountDownLatch(size);
    final CountDownLatch latchAfter = new CountDownLatch(size);
    for (int i = 0; i < size; i++) {
      threads[i] = new Runnable() {
        public void run()
        {
          try {
            System.out.println(Thread.currentThread().getName() + " run() before");
            latchBefore.await();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          MySingleton instance = MySingleton.getInstance();
          instance.getInitValue();
          latchAfter.countDown();
          try {
            latchAfter.await();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          System.out.println(Thread.currentThread().getName() + " run() after");
        }
      };
      new Thread(threads[i]).start();
      Thread.sleep(10);
      latchBefore.countDown();
    }
    latchAfter.await();
    Thread.sleep(100);
    System.out.println("end");
  }

}
