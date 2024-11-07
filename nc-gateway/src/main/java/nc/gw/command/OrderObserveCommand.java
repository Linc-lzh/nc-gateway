package nc.gw.command;


import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import nc.gw.entity.OrderPay;
import rx.Observable;
import rx.Subscriber;

public class OrderObserveCommand extends HystrixObservableCommand<OrderPay> {
	
	public OrderObserveCommand(String commandGroupKey) {
		super(HystrixCommandGroupKey.Factory.asKey(commandGroupKey));
	}

	
	@Override
	protected Observable<OrderPay> construct() {
		return Observable.create(new Observable.OnSubscribe<OrderPay>() {
			@Override
			public void call(Subscriber<? super OrderPay> subscriber) {
				try {
					if (!subscriber.isUnsubscribed()) {
						//subscriber.onError(getExecutionException());	//抛出异常不会继续执行
						
						System.out.println("方法执行---->>");
						OrderPay orderPay = new OrderPay((long) 111, 9999999.99, "消息成功");
						//这个方法监听方法，是传递结果的，请求多次的结果通过它返回去汇总起来。
						subscriber.onNext(orderPay);
						subscriber.onCompleted();
					}
				}catch (Exception e) {
					subscriber.onError(e);
				}
			}
		});
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
	 */
	
	//服务降级Fallback | resumeWithFallback
    @Override
    protected Observable<OrderPay> resumeWithFallback() {
        return Observable.create(new Observable.OnSubscribe<OrderPay>() {
            @Override
            public void call(Subscriber<? super OrderPay> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                    	System.out.println("aaaa");
                    	OrderPay orderPay = new OrderPay();
                    	orderPay.setHsDesp("删除失败！");
                        subscriber.onNext(orderPay);
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

}