package nc.gw;
import nc.gw.command.OrderObserveCommand;
import nc.gw.entity.OrderPay;
import org.junit.Test;

import rx.Observable;
import rx.Subscriber;

/**
 * HystrixCommand的observe()与toObservable()的区别：
 * 1）observe()会立即执行HelloWorldHystrixCommand.run()；toObservable()要在toBlocking().single()或subscribe()时才执行HelloWorldHystrixCommand.run()
 * 2）observe()中，toBlocking().single()和subscribe()可以共存；在toObservable()中不行，因为两者都会触发执行HelloWorldHystrixCommand.run()，这违反了同一个HelloWorldHystrixCommand对象只能执行run()一次原则
 * 热：每个观察者都可以在事件源的中途加入，这会造成看到的是加入后的局部信息
 * 冷执行：事件源需要等待观察者加入后，才发布事件，这样，看到的是全部过程。
 * @throws Exception
 */

public class HystrixCommandObservable {
	
	
	@Test
	public void testObservableHot() throws Exception {
		// observe()是异步非堵塞性执行，同queue
		Observable<OrderPay> orderObserver = new OrderObserveCommand("orderObserverHotTestSuccess").observe();
		// single()是堵塞的
		System.out.println("orderObserver single结果：" + orderObserver.toBlocking().single());
		// 注册观察者事件
		// subscribe()是非堵塞的
		Subscriber<OrderPay> subscriber = new Subscriber<OrderPay>(){
			// 先执行onNext再执行onCompleted
			// @Override
			public void onCompleted() {
				System.out.println("orderObserver completed[聚合结果]");
			}
			// @Override
			public void onError(Throwable e) {
				e.printStackTrace();
			}
			// @Override
			public void onNext(OrderPay v) {
				System.out.println("orderObserver onNext[返回结果]: " + v);
			}
		};
	}
	
	
	@Test
	public void testObservableCol() throws Exception {

		// observe()是异步非堵塞性执行，同queue
		Observable<OrderPay> orderObserver = new OrderObserveCommand("orderObserverColTestSuccess").toObservable();
		
		// subscribe()是非堵塞的
		Subscriber<OrderPay> subscriber = new Subscriber<OrderPay>(){
			// 先执行onNext再执行onCompleted
			// @Override
			public void onCompleted() {
				System.out.println("orderObserver completed[聚合结果]");
			}
			// @Override
			public void onError(Throwable e) {
				e.printStackTrace();
			}
			// @Override
			public void onNext(OrderPay v) {
				System.out.println("orderObserver onNext[返回结果]: " + v);
			}
		};
		orderObserver.subscribe(subscriber);
	}
	

}
