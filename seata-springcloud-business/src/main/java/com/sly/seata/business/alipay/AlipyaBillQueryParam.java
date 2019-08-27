package com.sly.seata.business.alipay;

import lombok.Data;

/**
 * 阿里账单查询参数
 *
 * @Author daituo
 * @Date
 **/
@Data
public class AlipyaBillQueryParam {

    /**
     * trade 指商户基于支付宝交易收单的业务账单
     * signcustomer 是指基于商户支付宝余额收入及支出等资金变动的帐务账单
     */
    private String bill_type;

    /**
     * 账单时间：日账单格式为yyyy-MM-dd
     */
    private String bill_date;
}
