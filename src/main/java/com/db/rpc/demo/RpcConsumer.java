package com.db.rpc.demo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.db.rpc.proxy.RpcProxy;
import com.db.rpc.service.HellowService;
import com.sun.media.jfxmedia.logging.Logger;

/**
 * 服务消费者
 * @author dengbo
 */
public class RpcConsumer {
	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("application-context-client.xml");
		RpcProxy rpcProxy = (RpcProxy) ctx.getBean("rpcProxy");
		HellowService h = rpcProxy.create(HellowService.class);
		String result = h.sayHellow("yiyi");
		System.out.println(result);
	}
}
