package com.db.rpc.server;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.reflect.FastClass;

import com.db.rpc.http.RpcRequest;
import com.db.rpc.http.RpcResponse;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.cglib.reflect.FastMethod;

/**
 * 处理rpc请求
 * @author dengbo
 */
public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {
	private final static Logger logger = LoggerFactory.getLogger(RpcHandler.class);
	private final Map<String,Object> handlerMap;

	public RpcHandler(Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
		RpcResponse response = new RpcResponse();
		response.setRequestId(request.getRequestId());
		try{
			Object result = handle(request);
			response.setResult(result);
		}catch(Throwable t){
			response.setThrowable(t);
		}
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		
	}

	/**
	 * 根据request请求 调用对应方法 返回结果
	 * @param request
	 * @return
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	private Object handle(RpcRequest request) throws Exception {
		if(handlerMap==null||handlerMap.isEmpty()){
			throw new RuntimeException("not found method:"+request.getMethodName());
		}
		Object impl = handlerMap.get(request.getClassName());
		FastClass serviceFastClass = FastClass.create(impl.getClass());
        FastMethod serviceFastMethod = serviceFastClass.getMethod(request.getMethodName(), request.getParameterTypes());
		return serviceFastMethod.invoke(impl, request.getParameters());
	}
	
}
