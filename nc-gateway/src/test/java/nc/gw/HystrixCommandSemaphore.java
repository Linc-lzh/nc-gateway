package nc.gw;

import java.io.IOException;
import java.util.Map;

import nc.gw.command.OrderCommand;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.HystrixThreadPoolKey;


/**
 * 信号量隔离
 */
public class HystrixCommandSemaphore extends HystrixCommand<String> {
	
    private final String id;


	@Autowired
	protected HystrixCommandSemaphore(String id) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("SemaphoreTestGroup"))  
                .andCommandKey(HystrixCommandKey.Factory.asKey("SemaphoreTestKey"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("SemaphoreTestThreadPoolKey"))
                .andCommandPropertiesDefaults(	// 配置信号量隔离
                		HystrixCommandProperties.Setter()
                		.withExecutionIsolationStrategy(ExecutionIsolationStrategy.SEMAPHORE)	// 信号量隔离
                		.withExecutionIsolationSemaphoreMaxConcurrentRequests(3)
                		.withFallbackIsolationSemaphoreMaxConcurrentRequests(1)
                )
             // 设置了信号量隔离后，线程池配置将变无效
//                .andThreadPoolPropertiesDefaults(	
//                		HystrixThreadPoolProperties.Setter()
//                		.withCoreSize(13)	// 配置线程池里的线程数
//                )
        );
        this.id = id;

	}

	@Override
	protected String run() throws Exception {
    	return "run(): id="+id+"，线程名是" + Thread.currentThread().getName();
	}
	
	@Override
    protected String getFallback() {
    	return "getFallback(): id="+id+"，线程名是" + Thread.currentThread().getName();
    }

	
	public static class UnitTest {

        @Test
        public void testSynchronous() throws IOException {
        	try {
        		Thread.sleep(2000);
	        	for(int i = 0; i < 5; i++) {
	        		final int j = i;
	        		// 自主创建线程来执行command，创造并发场景
	                Thread thread = new Thread(new Runnable() {
//	                    @Override  
	                    public void run() {
	                    	// 这里执行两类command：HystrixCommand4SemaphoreTest设置了信号量隔离、HelloWorldHystrixCommand未设置信号量
	                        System.out.println("-----------" + new OrderCommand("NC" + j, 2).execute().toString());
	                        System.out.println("===========" + new HystrixCommandSemaphore("NC" + j).execute());	// 被信号量拒绝的线程从这里抛出异常
	                        System.out.println("-----------" + new OrderCommand("NC" + j, 2).execute().toString());
	                    }  
	                });  
	                thread.start();
	        	}
        	} catch(Exception e) {
        		e.printStackTrace();
        	}
        	
        	System.out.println("------开始打印现有线程---------");
        	Map<Thread, StackTraceElement[]> map=Thread.getAllStackTraces();
        	for (Thread thread : map.keySet()) {
				System.out.println(thread.getName());
			}
        	System.out.println("thread num: " + map.size());
        	System.in.read();
        }
	}
    
	
	

}
