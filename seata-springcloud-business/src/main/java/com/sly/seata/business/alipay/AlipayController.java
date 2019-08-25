package com.sly.seata.business.alipay;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.sly.seata.common.model.order.Order;
import com.sly.seata.common.utils.CommonUtils;
import com.sly.seata.common.utils.DateUtils;
import com.sly.seata.common.utils.XidUtils;
import com.sly.seata.order.service.OrderService;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author daituo
 * @Date
 **/
@RestController
@Slf4j
public class AlipayController {

    /** 在 SDK 调用前需要进行初始化 */
    private AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl,
                                                                AlipayConfig.app_id,
                                                                AlipayConfig.merchant_private_key,
                                                                "json",
                                                                AlipayConfig.charset,
                                                                AlipayConfig.alipay_public_key,
                                                                AlipayConfig.sign_type);

    @Autowired
    private OrderService orderService;


    /**
     * 下单并调用支付
     * @param httpRequest
     * @param httpResponse
     */
    @RequestMapping(path="/aliPay", method = RequestMethod.GET)
    @GlobalTransactional
    public Map<String, Object> aliPay(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        log.info("全局事务xid -> {}" + RootContext.getXID());
        XidUtils.setXid(RootContext.getXID());

        //创建本地订单
        Order order = new Order();
        String orderId = CommonUtils.genUUID();
        order.setOrderId(orderId);
        order.setOrderNo("NO" + orderId);
        order.setOrderDetail("商品详情" + orderId);
        order.setCreateTime(DateUtils.formateTime(new Date()));
        order.setLogicDel("N");
        order.setPayStatus(1);  //待支付状态
        orderService.insert(order);
        log.info("成功创建本地订单 -> {}",JSON.toJSONString(order));

        //创建支付API对应的request
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        //alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
        //填充业务参数
        AlipayTradePagePayVo alipayTradePagePayVo = new AlipayTradePagePayVo();
        alipayTradePagePayVo.setOut_trade_no(orderId);
        alipayRequest.setBizContent(JSON.toJSONString(alipayTradePagePayVo));

        String form="";
        try {
            //调用SDK生成表单
            form = alipayClient.pageExecute(alipayRequest).getBody();
            log.info("提交支付宝form表单 -> {}",form);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //直接将完整的表单html输出到页面
        httpResponse.setContentType("text/html;charset=" + AlipayConfig.charset);
        httpResponse.getWriter().write(form);
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
        log.info("订单 -> {} 正在支付中......",orderId);
        return null;
    }


    /**
     * 支付结束后，同步通知支付结果，不准确
     */
    @RequestMapping(path="/pay/success", method = RequestMethod.GET)
    public Map<String, Object> listPage() {
        log.info("===========>支付结束后，同步通知支付结果");
        Map<String, Object> result = new HashMap<>(16);
        result.put("status", 200);
        result.put("message", "同步通知付款成功！");
        return result;
    }


}
