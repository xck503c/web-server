import com.xck.SysConstants;
import org.junit.Test;

import java.util.concurrent.*;

public class TestPutBlockPolicy {

    private static BlockingQueue<Runnable> transferWorkQueue = new ArrayBlockingQueue<Runnable>(20);
    private static ExecutorService transferPool = new ThreadPoolExecutor(SysConstants.CPUS
            , SysConstants.CPUS, 60, TimeUnit.SECONDS, transferWorkQueue, new TaskPutBlockPolicy());

    @Test
    public void test(){
        final Thread t1 = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 30; i++) {
                    System.out.println("submit " + i);
                    transferPool.submit(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    boolean is = Thread.currentThread().isInterrupted();
                    System.out.println(is);
                    if(is) break;
                }

                System.out.println("put end");
            }
        });
        final Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("shutdown");
                transferPool.shutdown();
                t1.interrupt();
            }
        });
        t1.start();
        t.start();
        try {
            Thread.sleep(60*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static class TaskPutBlockPolicy implements RejectedExecutionHandler{

        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if(!executor.isShutdown()){
                try {
                    System.out.println("put");
                    transferWorkQueue.put(r);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("set interrupt");
                }
            }
        }
    }
}
