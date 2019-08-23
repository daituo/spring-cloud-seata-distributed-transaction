package com.sly.seata.common.utils;

/**
 * @Author daituo
 * @Date
 **/
public class XidUtils {

    private static final  ThreadLocal<String> xidThreadLocal = new ThreadLocal<>();
    public static final String getXid(){
        return xidThreadLocal.get();
    }
    public static final void setXid(String xid){
        xidThreadLocal.set(xid);
    }
    public static final void destroy(){
        xidThreadLocal.remove();
    }
}
