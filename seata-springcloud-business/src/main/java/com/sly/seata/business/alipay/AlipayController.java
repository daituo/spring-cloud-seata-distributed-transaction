package com.sly.seata.business.alipay;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author daituo
 * @Date
 **/
@RestController
@Slf4j
public class AlipayController {

    /**
     * 1-待支付 2-已支付 3-支付失败
     */
    public static final Integer Waiting_Pay = 1;
    public static final Integer Payed = 2;
    public static final Integer Pay_Fail = 3;

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
     */
    @RequestMapping(path="/aliPay", method = RequestMethod.GET)
    @GlobalTransactional
    public String aliPay(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
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
        order.setPayStatus(Waiting_Pay);   //待支付状态
        orderService.insert(order);
        log.info("成功创建本地订单 -> {}",JSON.toJSONString(order));

        //创建支付API对应的request
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);

        //填充业务参数
        AlipayTradePagePayVo alipayTradePagePayVo = new AlipayTradePagePayVo();
        alipayTradePagePayVo.setOut_trade_no(orderId);
        alipayRequest.setBizContent(JSON.toJSONString(alipayTradePagePayVo));

        String form = "";
        AlipayTradePagePayResponse payResponse = null;
        try {
            //调用SDK生成表单
            payResponse = alipayClient.pageExecute(alipayRequest);
            form = payResponse.getBody();
            log.info("提交支付宝form表单内容 -> {}",form);
            //直接将完整的表单html输出到页面
            httpResponse.setContentType("text/html;charset=" + AlipayConfig.charset);
            httpResponse.getWriter().write(form);
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
            if (payResponse.isSuccess()) {
                log.info("Call Pay success, AlipayTradePagePayResponse -> {}",JSON.toJSONString(payResponse));
            } else {
                log.info("Call Pay fail, AlipayTradePagePayResponse -> {}",JSON.toJSONString(payResponse));
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;

    }



    /**
     * 支付结束后，Get同步通知支付结果
     * 真实同步url===>http://117.136.12.122:7100/pay/success?charset=utf-8
     *                                                      &out_trade_no=613A58A022DF49F79DB6E62621AC4B07
     *                                                      &method=alipay.trade.page.pay.return
     *                                                      &total_amount=0.01
     *                                                      &sign=URmzxQYT7zKq3SzT6kzI84QpqqCK60uwZebCIl3FhOV76d8Lcz3OAgnPaVwkOn5ewFoVjkLcHc%2F9hLQ8s0GoR%2Buiye%2B8sqLhsd5sMm1IjmKUIpgmLIbk23XSThSZyezT3UPCpvsF5mX0zfVU%2BuQyjmyIirYBYTlHL6gylRNnpzKuMQfItaAX6fiDP8Bl5Vb7EvScEIQqh%2FsAO6cCDvANItJvoJhX65qeQFsORJidd2Bb5is2deznZxpB673BZR3phPSdAFKhVRHzHuXvbjSWlgyJOY0fCw90yMkz1EGLJTTqrkCk84cnxuEqmFFoNlLLxFI0QoPf%2BAV5pGIu42jOnQ%3D%3D
     *                                                      &trade_no=2019082622001486971000019819
     *                                                      &auth_app_id=2016090800466344
     *                                                      &version=1.0
     *                                                      &app_id=2016090800466344
     *                                                      &sign_type=RSA2
     *                                                      &seller_id=2088102174735367
     *                                                      &timestamp=2019-08-26+14%3A38%3A44
     */
    @RequestMapping(path="/return/payStatus/", method = RequestMethod.GET)
    public Map<String, Object> returnPayStatus() {
        //log.info("===========>支付结束后，同步通知支付结果");
        //Map<String, Object> result = new HashMap<>(16);
        //result.put("status", 200);
        //result.put("message", "同步通知付款成功！");
        return null;
    }



    /**
     * 支付结束后，Post异步通知支付结果，支付是否成功已异步通知为准
     *
     */
    @RequestMapping(path="/notice/payStatus", method = RequestMethod.POST)
    public String noticePayStatus(HttpServletRequest request) {
        //将异步通知中收到的所有参数都存放到paramsMap中
        Map<String, String> paramsMap = getReceiveParamMap(request);
        log.info("支付宝支付回调参数 -> {}",paramsMap);

        boolean signVerified = false;
        try {
            signVerified = signAndParamVerified(paramsMap,signVerified);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return "failure";
        }

        if (signVerified) {
            //修改订单状态-已支付
            orderService.updateOrderPayStatus(paramsMap.get("out_trade_no"), Payed);
            return "success";
        } else {
            /**
             * 如果商户反馈给支付宝的字符不是 success 这7个字符，支付宝服务器会不断重发通知，直到超过24小时22分钟。
             * 一般情况下，25小时以内完成8次通知（通知的间隔频率一般是：4m,10m,10m,1h,2h,6h,15h）
             */
            return "failure";
        }
    }



    /**
     * 调用SDK验证签名&&校验业务参数
     * @param paramsMap
     * @param signVerified
     */
    private boolean signAndParamVerified(Map<String, String> paramsMap, boolean signVerified) throws AlipayApiException {
        signVerified = AlipaySignature.rsaCheckV2(paramsMap, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);
        log.info("====>异步通知参数验签结果：{}",signVerified);

        if(signVerified){
            /**
             * 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
             * 接收到异步通知并验签通过后，一定要检查通知内容，包括通知中的 app_id、out_trade_no、total_amount 是否与请求中的一致，并根据 trade_status 进行后续业务处理。
             */
            String tradeStatus = paramsMap.get("trade_status");
            log.info("====> tradeStatus : {}",tradeStatus);
            if ("TRADE_SUCCESS".equals(tradeStatus)) {  //只有TRADE_SUCCESS才会触发异步通知
                String outTradeNo = paramsMap.get("out_trade_no");   //获取通知的商户订单号参数
                Order order = orderService.selectByOrderId(outTradeNo);

                //订单不存在
                if (order == null) {
                    log.info("订单号【" + outTradeNo + "】不存在");
                    return false;
                }

                String appId = paramsMap.get("app_id");
                if (!AlipayConfig.app_id.equals(appId)) {
                    log.info("无效的AppID -> {}",appId);
                    return false;
                }

                String totalAmount = paramsMap.get("total_amount").toString();
                if (!totalAmount.equals(new AlipayTradePagePayVo().getTotal_amount())) {
                    log.info("回调金额->{},与订单金额->{}不一致",totalAmount,new AlipayTradePagePayVo().getTotal_amount());
                    return false;
                }
                return true;
            }
        } else {
            // TODO 验签失败则记录异常日志，并在response中返回failure.
        }
        return false;
    }



    /**
     * 获取Post请求的所有参数
     * @param request
     */
    private Map<String, String> getReceiveParamMap(HttpServletRequest request) {
        Map<String, String> paramsMap = new HashMap<>();
        Map<String, String[]> requestParameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entrySet : requestParameterMap.entrySet()) {
            String name = entrySet.getKey();
            String[] values = entrySet.getValue();
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            paramsMap.put(name, valueStr);
        }
        return paramsMap;
    }



    /**
     * 查询订单支付状态
     */
    @RequestMapping(path="/pay/info", method = RequestMethod.GET)
    public String payInfo() throws AlipayApiException {
        List<Order> noPayOrders = orderService.selectNoPayOrders();
        log.info("未支付订单列表 -> {}",noPayOrders.toString());
        String orderId = noPayOrders.get(0).getOrderId();

        //根据商户订单号查询支付信息
        Map<String,String> biz = new HashMap<>();
        biz.put("out_trade_no", orderId);
        AlipayTradeQueryRequest tradeQueryRequest = new AlipayTradeQueryRequest();  //创建订单查询对应的request
        tradeQueryRequest.setBizContent(JSON.toJSONString(biz));
        AlipayTradeQueryResponse tradeQueryResponse = alipayClient.execute(tradeQueryRequest);
        if (tradeQueryResponse.isSuccess()) {
            log.info("call alipay.trade.query response -> {}",JSON.toJSONString(tradeQueryResponse));
            /**
             * 查询时，支付宝返回的交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）
             *                          TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）
             *                          TRADE_SUCCESS（交易支付成功）
             *                          TRADE_FINISHED（交易结束，不可退款）
             */
            if ("TRADE_SUCCESS".equals(tradeQueryResponse.getTradeStatus())) {  //交易支付成功
                //修改本地订单支付状态
                orderService.updateOrderPayStatus(tradeQueryResponse.getOutTradeNo(),Payed);
            }
        } else {
            log.info("call alipay.trade.query fail -> {}",JSON.toJSONString(tradeQueryResponse));
        }
        return JSON.toJSONString(tradeQueryResponse);
    }



    /**
     * 下载支付宝对账单
     */
    @RequestMapping(path="/bill/download", method = RequestMethod.GET)
    public void downloadAlipayBill(HttpServletRequest request, HttpServletResponse response) {
        AlipyaBillQueryParam billQueryParam = new AlipyaBillQueryParam();
        String billDate = "2019-08-26";
        billQueryParam.setBill_type("trade");
        billQueryParam.setBill_date(billDate);
        AlipayDataDataserviceBillDownloadurlQueryRequest billDownloadRequest = new AlipayDataDataserviceBillDownloadurlQueryRequest();
        billDownloadRequest.setBizContent(JSON.toJSONString(billQueryParam));
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            AlipayDataDataserviceBillDownloadurlQueryResponse billDownloadResponse = alipayClient.execute(billDownloadRequest);
            log.info("call alipay download bill response -> {}",JSON.toJSONString(billDownloadResponse));
            if (billDownloadResponse.isSuccess()) {
                //预下载电子账单
                String billDownloadUrl = billDownloadResponse.getBillDownloadUrl();
                URL url = new URL(billDownloadUrl);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(5 * 1000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Charsert", "UTF-8");
                httpURLConnection.connect();
                inputStream = httpURLConnection.getInputStream();
                String filePath = AlipayConfig.log_path + "alipayBill" + File.separator + "bill_" + billDate + ".xls";
                File billFile = new File(filePath);
                String parentFilePath = billFile.getParent();
                File parentDir = new File(parentFilePath);
                if (!parentDir.exists()) {
                    parentDir.mkdir();
                }
                fileOutputStream = new FileOutputStream(new File(filePath));
                byte[] temp = new byte[1024];
                int b;
                while ((b = inputStream.read(temp)) != -1) {
                    fileOutputStream.write(temp, 0, b);
                    fileOutputStream.flush();
                }
                //IOUtils.copy(inputStream, fileOutputStream);

                //浏览器直接下载
                //response.addHeader("Content-Disposition", "attachment;filename=bill_" + billDate + ".csv");
                //response.addHeader("Content-Type", "application/octet-stream");
                //IOUtils.copy(inputStream, response.getOutputStream());
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(inputStream != null) inputStream.close();
                if(fileOutputStream != null) fileOutputStream.close();
                if(httpURLConnection != null) httpURLConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
