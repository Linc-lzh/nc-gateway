package nc.gw.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import nc.gw.entity.OrderPay;
import nc.gw.service.OrderPayService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;

@Service
public class OrderPayServiceImpl implements OrderPayService {

	private Map<Long, OrderPay> resultSet = new HashMap<>();
	@Override
	public OrderPay get(Long id) {
		return resultSet.get(id);
	}

	@Override
	@HystrixCommand(fallbackMethod = "orderError")
    public Future<OrderPay> addOrder(Long id) {
        return new AsyncResult<OrderPay>() {
            @Override
            public OrderPay invoke() {
            	int i = (int) (1/id);
            	System.out.println("接受ID：" + id + "结果" + i);
            	OrderPay orderPay = new OrderPay();
				orderPay.setMoney(9999.99);
				orderPay.setId(id);
				resultSet.put(id, orderPay);
				return orderPay;
            }

 			@Override
 			public OrderPay get() {
                 return invoke();
 			}
        };
    }
	
	
	public OrderPay orderError(@PathVariable("id") Long id, Throwable throwable) {
		 //fallback逻辑
		System.out.println("异常信息：" + throwable.getMessage());
		OrderPay orderPay = new OrderPay();
		orderPay.setId(-1L);
		orderPay.setMoney(0.0);
		orderPay.setHsDesp("-=====");
		return orderPay;
	}

}
