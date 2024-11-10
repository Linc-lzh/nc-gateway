package nc.gw;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import nc.gw.command.OrderCommand;
import nc.gw.entity.OrderPay;
import org.junit.Test;



public class HystrixCommandQueue {
	
	
	@Test
	public void testQueue() throws Exception {
		// queue()是异步非堵塞性执行：直接返回，同时创建一个线程运行HelloWorldHystrixCommand.run()
		// 一个对象只能queue()一次
		// queue()事实上是toObservable().toBlocking().toFuture()
		Future<OrderPay> future = new OrderCommand("orderQueue", 2).queue();
		
		// 使用future时会堵塞，必须等待HelloWorldHystrixCommand.run()执行完返回
		OrderPay queueResult = future.get(10000, TimeUnit.MILLISECONDS);
		// String queueResult = future.get();
		System.out.println("queue异步结果：" + queueResult);
		assertEquals("hello",queueResult.toString());
	}

}
