package nc.gw;

import java.io.IOException;
import java.util.Map;
import org.junit.Test;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;


public class HystrixCommandCircuitBreaker extends HystrixCommand<String> {
	
    private final String key;

	protected HystrixCommandCircuitBreaker(String key) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CircuitBreakerTestGroup"))  
                .andCommandKey(HystrixCommandKey.Factory.asKey("HystrixCommandCircuitBreaker"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("CircuitBreakerTest"))
                .andThreadPoolPropertiesDefaults(	// 配置线程池
                		HystrixThreadPoolProperties.Setter()
                		.withCoreSize(200)	// 配置线程池里的线程数，设置足够多线程，以防未熔断却打满threadpool
                )
                .andCommandPropertiesDefaults(	// 配置熔断器
                		HystrixCommandProperties.Setter()
                		.withCircuitBreakerEnabled(true)
                		.withCircuitBreakerRequestVolumeThreshold(3)	//设置为3，意味着10s内请求超过3次就触发熔断器
                		.withCircuitBreakerErrorThresholdPercentage(80)	//执行3次run后，将被熔断，进入降级，即不进入run()而直接进入fallback
//                		.withCircuitBreakerForceOpen(true)	// 置为true时，所有请求都将被拒绝，直接到fallback
//                		.withCircuitBreakerForceClosed(true)	// 置为true时，将忽略错误
//                		.withExecutionIsolationStrategy(ExecutionIsolationStrategy.SEMAPHORE)	// 信号量隔离
//                		.withExecutionTimeoutInMilliseconds(5000)
                )
        );
        this.key = key;
	}

	
	//如果未熔断，但是threadpool被打满，仍然会降级，即不进入run()而直接进入fallback
	//执行3次run后，将被熔断，进入降级，即不进入run()而直接进入fallback
	@Override
    protected String run() throws Exception {
    	System.out.println("running run():" + key);
    	int num = Integer.valueOf(key);
    	if(num % 2 == 0 && num < 10) {	// 直接返回
    		return key;
    	} else {	// 无限循环模拟超时	
    		int j = 0;
        	while (true) {
        		j++;
        	}
    	}
//		return name;
    }

    @Override
    protected String getFallback() {
        return "CircuitBreaker fallback: " + key;
    }

	
    public static class UnitTest {

      @Test
      public void testSynchronous() throws IOException {
      	for(int i = 0; i < 50; i++) {
	        	try {
	        		System.out.println("===========>>>>>>>>" + new HystrixCommandCircuitBreaker(String.valueOf(i)).execute());
	        	} catch(Exception e) {
	        		System.out.println("run()抛出HystrixBadRequestException时，被捕获到这里" + e.getCause());
	        	}
      	}

      	System.out.println("------开始打印现有线程---------");
      	Map<Thread, StackTraceElement[]> map=Thread.getAllStackTraces();
      	for (Thread thread : map.keySet()) {
				System.out.println(thread.getName());
			}
      	System.out.println("thread num: " + map.size());
      	
      	//System.in.read();
      	}
    }

	
	

}
