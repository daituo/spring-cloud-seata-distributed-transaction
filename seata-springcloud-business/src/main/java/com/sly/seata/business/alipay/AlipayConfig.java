package com.sly.seata.business.alipay;

/**
 * 支付宝配置类
 * @Author daituo
 * @Date
 **/
public class AlipayConfig {

    /** 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号 */
    public static String app_id = "2016090800466344";

    /** 商户私钥，您的PKCS8格式RSA2私钥 */
    public static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCtxYxU2uTv18UNbanVIsPDOV8mt++6uwHDlxUvB6hKvYKiO6xEd4FNYlY2NRxAOzsmfmgRh0QI6y1vIiD3Pqx9gvq7KDfYE8kG1UJ1oPPDq+0HOFXM274NI9wLPoXzf2HqWPKFuMNB6vUU0GVgzT5EfuXZ4FcW1VzRENCNDc8MI/t4znBS7ew1WSUn/wAiHYOWXuk8wwE2q3F51yFUHZkrq08vKoTipPmuZZLSw8Nygek9P1dOhJouHH3S2v40xCBDh8ccTe2sWuKWVVB04jEvQNWCGLcxD7usA2BoDHn1RpUR3+1+VRKVkY5ReAWY6Cw0bvlJJIVazSN8qmSEGowfAgMBAAECggEBAJ+g6F+O0V3N/VNvmOKU0/ZDfrgHoe4MGfu9jxUr2JoH6rfzTzV5/krC+eVD9pa+koCzABCSDJqQx6TeWiAl77hAmOlScbJJwpJHV4zM9QCbr9rBEnhkp/pUJbAimFRtgNoRKYBTkPK9zrN3GJnLENdFJwp1fisYhvH6m0noFHnWD51asDEeRdinNFiK32iTf7p6mdwV6Fm1/Eyc/3e7PfXTGAoVMK381wwGmdMUZj6n27lrxmnb/SBEgqg080pjrfmFBJfIdq98prWXaYSlqvjxrpwuIaAue3DuX4DtDEkqZI02ZbYv86kNhrSY3An72dTMo2kSUpp5y7AhlxMsZEkCgYEA91rIarCpD5oDfcYgsIEr1Zfr2wcX7a9x8kcluT9TLdQCo1L1wG+AkHQTJz1k0vQAJ4I18ZPt3FjDrgiGJ4ogKUYhBBDbUBPDILEa95CH8BX8fGEojGhXuxTytglRvhgFKobcMPiTbX5bW59Aa/9eUf4Xz1qhqPz/UZazURGCVksCgYEAs9hg3qDm66fpoQFEuAqgWWl051/5pebdjdTyKx8mhmY+dEcjIUN2pU8Q6h2c6j9RSrq4THEuFaENzliFUz6BUNBs0idm5yBvSIr22a9PD0WqoDo00AA00ATQH1/e9l3BrLXONYOtsZ9DOmEnULh5awmzylIGL3LFMPvc+pXnTP0CgYA1+N0aSonZGaYBX6XqXYUPCiMwm4+hEVMvpk6Y6TRhH42jzYpJ/wpyeqFjQoYpcYxHEqZqm/iex8+Kvdln7z+tQdwaYwM3qdg93ecgdazzuhhpm1mZqXrbG2sKRWZMsDkQWnTZSqQAlD2i9FC+P+54vZS8be4wTWP4jYV6/vMNmQKBgDtWXge2QNppyUyk9X5fiy5U/AxG/0B8noboRopxua1IWLzWp8GW17ONFcCYyqgDAhhM6PfXs+PRApQdYsfnbJU0X2MrOulxKyZdKzsBe21HiBdbfQdPuh0UKXLgl9jjcD5Csem4K3vaVz0Qxv78DpuBVP+g9od7L5KM1wkaonklAoGAOYGRRq0pw7BUalEfMM/rBrFUIR1LDuSotDMBA/aRZwS7UBbkRrfQSvABW8o1F8jdyArBKKLLNYw3Sycv6iqq11VbbH0vmeLLBDY/u1wuP4Jp7E4RnXcYTD3ECcC7dXFF/ykSeK5tM49BT7RXCTVUpV8kANUsXbK0KvZ4eKyHQ/Q=";

    /** 支付宝公钥, 对应APPID下的支付宝公钥 */
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApsk2ubIYq6sCyGNtkXteWAaf21s5KlF+6ZguKPcslYhiw0f+VI3/1YLcH9ArG85GOcnzINiFZoYxJf0WEBA/Lh49VY19l4Wiyzjn1+wpVftWc5VCGYhfjiRYOXPiWA1ETYB1dHFMvV9syLw770wzEbCWv01VmeW2ytkBq42IgCv4k7M7eCw5WAg7WiJmLpNhX6z+pjfI7zf3ZsmSDWRc12QqNeiwBSLu2ETiGmBtmujX0qDZs8nqXTHKK0MnZ6S6eNFeYsFVEguOoeafq2p0gD/18hzHZC7086c98cn6NnswkUfJLXwiku0K0Kx3wolJCSPx8b6bQ4QuZGg3XOgU/QIDAQAB";

    /** 服务器异步通知页面路径,需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 */
    public static String notify_url = "http://117.136.12.122:7100/notice/payStatus";

    /** 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 */
    public static String return_url = "http://117.136.12.122:7100/return/payStatus";

    /** 签名方式 */
    public static String sign_type = "RSA2";

    /** 字符编码格式 */
    public static String charset = "utf-8";

    /** 支付宝网关 */
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    /** 支付日志 */
    public static String log_path = "/";



}
