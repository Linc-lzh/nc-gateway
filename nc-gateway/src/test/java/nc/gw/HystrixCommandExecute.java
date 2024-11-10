package nc.gw;

import static org.junit.Assert.assertEquals;

import nc.gw.command.OrderCommand;
import nc.gw.entity.OrderPay;
import org.junit.Test;


public class HystrixCommandExecute {
	
	@Test
	public void testSuccessExecute() {
		// execute()是同步堵塞式执行：先创建一个线程运行OrderCommand.run()，再返回往下执行
		// 一个对象只能execute()一次
		// execute()事实上是queue().get()
		System.out.println("mainthread:" + Thread.currentThread().getName());
		OrderPay executeResult = new OrderCommand("orderCommandTest",2).execute();
		System.out.println("execute同步结果：" + executeResult);
		assertEquals("executeResult: ", executeResult.toString());
	}

	
	@Test
	public void testFailExecute() {
		// execute()是同步堵塞式执行：先创建一个线程运行OrderCommand.run()，再返回往下执行
		// 一个对象只能execute()一次
		// execute()事实上是queue().get()
		System.out.println("mainthread:" + Thread.currentThread().getName());
		OrderPay executeResult = new OrderCommand("orderCommandTest",0).execute();
		System.out.println("execute同步结果：" + executeResult);
		assertEquals("executeResult: ", executeResult.toString());
	}
	
}
