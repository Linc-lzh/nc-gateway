package nc.gw.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import nc.gw.command.OrderCommand;
import nc.gw.command.OrderObserveCommand;
import nc.gw.entity.OrderPay;
import nc.gw.service.OrderPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import rx.Observable;
import rx.Observer;


@RestController
@RequestMapping("/order")
@DefaultProperties(defaultFallback = "defaultFail")
public class OrderController {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private OrderPayService orderPayService;
	
	/**
	 * 同步操作
	 * @param id
	 */
	@GetMapping(value = "/list/{id}")
	@HystrixCommand(fallbackMethod = "orderIdError")
	public OrderPay orderQuery(@PathVariable("id") Long id) {
		OrderPay orderPay = orderPayService.get(id);
		if(orderPay == null) {
			throw new RuntimeException("没有订单ID相关信息！");
		}
		return orderPay;
	}
	
	/**
	 * 发生熔断时调用的方法
	 * @param id
	 */
	public OrderPay orderIdError(@PathVariable("id") Long id) {
		return new OrderPay().setDeptno(id);
	}
	
	/**
	 * 非阻塞式 -> Future
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * 
	 */
	@GetMapping(value = "/pay/{id}")
	public OrderPay orderCreate(@PathVariable("id") Long id) throws InterruptedException, ExecutionException {
		Future<OrderPay> bookFuture = orderPayService.addOrder(id);
	    return bookFuture.get();
	}
	
	
	
	/**
	 * @HystrixCommand(observableExecutionMode = ObservableExecutionMode.EAGER)表示使用observe模式来执行  ｜ observe命令在调用的时候会立即返回一个Observable。
	 * @HystrixCommand(observableExecutionMode = ObservableExecutionMode.LAZY)表示使用toObservable模式来执行  ｜ toObservable则不会立即返回一个Observable，订阅者调用数据的时候才会执行。
	 * 响应式函数编程
	 */
	@GetMapping(value = "/del/{id}")
	public String orderDelete(@PathVariable("id") Long id) throws InterruptedException, ExecutionException {
        List<OrderPay> list = new ArrayList<>();

		OrderObserveCommand bookCommand = new OrderObserveCommand("orderObserver");
		//热执行
        //Observable<OrderPay> observable = bookCommand.observe();
		
		
        //冷执行
		Observable<OrderPay> observable =bookCommand.toObservable();
		
        observable.subscribe(new Observer<OrderPay>() {
            @Override
            public void onCompleted() {
				System.out.println("orderObserver completed[聚合结果]");
            }
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }
            @Override
            public void onNext(OrderPay o) {
				System.out.println("orderObserver onNext[返回结果]: " + o);
                list.add(o);
            }
        });
		return list.toString();
		
		
	}
	
	/**
	 * @HystrixCommand(observableExecutionMode = ObservableExecutionMode.EAGER)表示使用observe模式来执行  ｜ observe命令在调用的时候会立即返回一个Observable。
	 * @HystrixCommand(observableExecutionMode = ObservableExecutionMode.LAZY)表示使用toObservable模式来执行  ｜ toObservable则不会立即返回一个Observable，订阅者调用数据的时候才会执行。
	 * 响应式函数编程
	 */
	@GetMapping(value = "/dels/{id}")
	public OrderPay orderDels(@PathVariable("id") Long id) throws InterruptedException, ExecutionException {
		OrderCommand bookCommand = new OrderCommand("OrderObserveCommand",2);
		Future<OrderPay> queue = bookCommand.queue();
		OrderPay orderPay = queue.get();
		return orderPay;
	}
}
