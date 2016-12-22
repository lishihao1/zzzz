package com.db.rpc.demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 服务提供者
 * @author dengbo
 */
public class RpcPrivoder {
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("application-context.xml");
	}
}
