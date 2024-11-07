package nc.gw.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import nc.gw.entity.OrderPay;

public class OrderCommand extends HystrixCommand<OrderPay> {
	
	private final int id;
	
	public OrderCommand(String commandGroupKey,int id) {
		super(HystrixCommandGroupKey.Factory.asKey(commandGroupKey));
		this.id  = id;
	}
	
	/**
	 * 
	 * 以下四种情况将触发getFallback | resumeWithFallback调用：
	 * 1）run()方法抛出非HystrixBadRequestException异常
	 * 2）run()方法调用超时
	 * 3）熔断器开启拦截调用
	 * 4）线程池/队列/信号量是否跑满
	 * 
	 * 实现getFallback()后，执行命令时遇到以上4种情况将被fallback接管，不会抛出异常或其他
	 * 如果继承的是HystrixObservableCommand，要重写Observable construct() 
	 */
	@Override
	protected OrderPay run() throws Exception {
		if(this.id == 0) {
			int result = 1/0;
			System.out.println(result);
		}
		return new OrderPay((long) 111, 9999999.99, "消息成功");
	}
	
	
	@Override
	protected OrderPay getFallback() {
      return new OrderPay((long) 111, 9999999.99, "消息失败");
	}
}