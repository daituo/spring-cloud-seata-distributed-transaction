package com.sly.seata.order.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sly.seata.common.model.order.Order;
import com.sly.seata.order.service.hystrix.OrderServiceHystrixImpl;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Order feign客户端
 * 
 * @author sly
 * @time 2019年6月12日
 */
@FeignClient(name = "seata-springcloud-order", fallback = OrderServiceHystrixImpl.class)
public interface OrderService {

	/**
	 * 新增
	 * 
	 * @param order
	 * @return
	 * @author sly
	 * @time 2019年6月12日
	 */
	@RequestMapping(value = "/order/insert", method = RequestMethod.POST)
	Map<String, Object> insert(@RequestBody Order order);


	/**
	 * 查询未支付的订单
	 */
	@RequestMapping(value = "/selectNoPayOrders", method = RequestMethod.GET)
	List<Order> selectNoPayOrders();

	/**
	 * 修改本地订单支付状态
	 * @param outTradeNo 商户订单号
	 * @param payStatus 1-待支付 2-已支付 3-支付失败
	 */
	@RequestMapping(value = "/updateOrderPayStatus", method = RequestMethod.GET)
	void updateOrderPayStatus(@RequestParam("outTradeNo") String outTradeNo,
							  @RequestParam("payStatus") int payStatus);

	/**
	 * 根据订单号查询订单
	 * @param outTradeNo
	 * @return
	 */
	@RequestMapping(value = "/selectByOrderId", method = RequestMethod.GET)
	Order selectByOrderId(@RequestParam("outTradeNo") String outTradeNo);
}
