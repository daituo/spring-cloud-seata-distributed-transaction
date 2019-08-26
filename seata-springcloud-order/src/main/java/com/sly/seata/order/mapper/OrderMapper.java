package com.sly.seata.order.mapper;

import com.sly.seata.common.model.order.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单mapper
 * 
 * @author sly
 * @time 2019年6月12日
 */
public interface OrderMapper {

	/**
	 * 新增
	 * 
	 * @param order
	 * @return
	 * @author sly
	 * @time 2019年6月12日
	 */
	int insert(Order order);

	/**
	 * 查询所有未支付订单
	 */
	List<Order> selectNoPayOrders();

	void updateOrderPayStatus(@Param("outTradeNo") String outTradeNo,
							  @Param("payStatus") int payStatus);

	Order selectByOrderId(String outTradeNo);
}
