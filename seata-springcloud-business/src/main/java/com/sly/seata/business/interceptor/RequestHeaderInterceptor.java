package com.sly.seata.business.interceptor;

import com.sly.seata.common.utils.XidUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.sly.seata.common.constant.SeataConstant;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.seata.core.context.RootContext;

/**
 * Feign拦截器，把RootContext中的XID（XID用于标识一个局部事务属于哪个全局事务，需要在调用链路的上下文中传递）传递到上层调用链路
 * 
 * @author sly
 * @time 2019年6月12日
 */
@Component
public class RequestHeaderInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
		/**
		 * Hystrix会将请求放入Hystrix的线程池中去执行，此时会新启一个子线程处理请求。
		 * 而RootContext采用threadlocal传递数据，无法在子线程中传递，造成在子线程中通过RootContext.getXid()获取为空
		 * 因此改用自己手动set到线程变量中
		 */
		//String xid = RootContext.getXID();
		String xid = XidUtils.getXid();
		System.out.println("feign 拦截 -> xid="+ xid);
		if (StringUtils.isNotBlank(xid)) {
			template.header(SeataConstant.XID_HEADER, xid);
		}
	}

}
