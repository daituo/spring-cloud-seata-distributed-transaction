package com.sly.seata.business.alipay;

import lombok.Data;

/**
 * 支付 vo
 * @Author daituo
 * @Date
 **/
@Data
public class AlipayTradePagePayVo {

    /** 商户订单号 */
    private String out_trade_no;

    /** 商品编码*/
    private String product_code = "FAST_INSTANT_TRADE_PAY";

    /** 支付金额 */
    private String total_amount = "0.01";

    /** 订单商品名称 */
    private String subject = "Iphone6 16G";

    /** 订单商品描述 */
    private String body = "Iphone6 16G xxxxxxxxxxxxxxxxxxxx";

    /** 支付超时时间 30min*/
    public static String timeout_express = "30m";

}
