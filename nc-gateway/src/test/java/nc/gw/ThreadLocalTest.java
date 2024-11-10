package nc.gw;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;


public class ThreadLocalTest {
	
	static TransmittableThreadLocal<String> threadLocal = new TransmittableThreadLocal<String>();
	
    static ExecutorService pool =  TtlExecutors.getTtlExecutorService(Executors.newFixedThreadPool(2));


    
    public static void main(String[] args) {
        for(int i=0;i<100;i++) {
            int j = i;
            pool.execute(new Thread(new Runnable() {
                @Override
                public void run() {
                	ThreadLocalTest.threadLocal.set("线程值:"+j);
                    new Service().call();
                }
            }));
        }
        System.out.println("主线程结束");
    }
}


class Service {
	public void call() {
		ThreadLocalTest.pool.execute(new Runnable() {
            @Override
            public void run() {
                new Dao().call();
            }
        });
    }

}


class Dao {
    public void call() {
        System.out.println("==========================");
        System.out.println("底层线程:" + ThreadLocalTest.threadLocal.get());
    }
}