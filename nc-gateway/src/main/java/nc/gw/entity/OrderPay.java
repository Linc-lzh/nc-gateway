package nc.gw.entity;

public class OrderPay {

	private Long id;
	
	private Double money;
	
	private String hsDesp;

	public OrderPay(Long id, Double money, String hsDesp) {
		this.id = id;
		this.money = money;
		this.hsDesp = hsDesp;
	}

	public OrderPay() {
	}

	public String getHsDesp() {
		return hsDesp;
	}

	public void setHsDesp(String hsDesp) {
		this.hsDesp = hsDesp;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}


	public OrderPay setDeptno(Long id) {
		OrderPay orderPay = new OrderPay();
		orderPay.setId(id);
		orderPay.setHsDesp("该ID：" + id + "没有没有对应的信息,null--@HystrixCommand");
		return orderPay;
	}

	@Override
	public String toString() {
		return "OrderPay [id=" + id + ", money=" + money + ", hsDesp=" + hsDesp + "]";
	}
	
}
