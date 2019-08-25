package com.sly.seata.business.alipay;

import lombok.Data;

/**
 * 支付 vo
 * @Author daituo
 * @Date
 **/
@Data
public class AlipayTradePagePayVo {

    /** 订单唯一标识符 */
    private String out_trade_no;

    /** 商品编码*/
    private String product_code = "FAST_INSTANT_TRADE_PAY";

    /** 支付金额 */
    private String total_amount = "0.01";

    /** 订单主题 */
    private String subject = "Iphone6 16G";

    private String body = "Iphone6 16G xxxxxxxxxxxxxxxxxxxx";

}
