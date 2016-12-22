package com.db.rpc.service.impl;

import com.db.rpc.annotation.RpcService;
import com.db.rpc.service.HellowService;
/**
 *  µœ÷¿‡
 * @author dengbo
 */
@RpcService(cls=HellowService.class)
public class HellowServiceImpl implements HellowService{

	public String sayHellow(String name) {
		return "hellow "+name;
	}

}
