package nc.gw.service;

import nc.gw.entity.OrderPay;

import java.util.concurrent.Future;


public interface OrderPayService {

	OrderPay get(Long id);

	Future<OrderPay> addOrder(Long id);

}
