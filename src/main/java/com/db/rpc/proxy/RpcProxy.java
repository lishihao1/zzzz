package com.db.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import com.db.rpc.client.RpcClient;
import com.db.rpc.http.RpcRequest;
import com.db.rpc.http.RpcResponse;
import com.db.rpc.server.discovery.ServiceDiscovery;

public class RpcProxy {
	
	public String serverAddress;
	
	public ServiceDiscovery serviceDiscovery;
	
	public RpcProxy(ServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}

	@SuppressWarnings("unchecked")
	public <T> T create(final Class<?> interfaceClass) throws Exception{
		if(!interfaceClass.isInterface()){
			 throw new IllegalArgumentException("The " + interfaceClass.getName() + " must be interface class!");
		}
		return (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(),
				new Class<?>[]{interfaceClass}, new InvocationHandler(){
					public Object invoke(Object obj, Method method, Object[] parameters) throws Throwable {
						RpcRequest request = new RpcRequest();//�����������
						request.setClassName(interfaceClass.getName());
						request.setMethodName(method.getName());
						request.setParameterTypes(method.getParameterTypes());
						request.setParameters(parameters);
						request.setRequestId(UUID.randomUUID().toString());
						if(serviceDiscovery!=null){
							serverAddress = serviceDiscovery.discovery();//���ַ���
						}
						String[] array = serverAddress.split(":");
						String host = array[0];
						int port = Integer.parseInt(array[1]);
						RpcClient rpcClient = new RpcClient(host,port);// ��ʼ�� RPC �ͻ���
						RpcResponse response = rpcClient.send(request); // ͨ�� RPC �ͻ��˷��� RPC ���󲢻�ȡ RPC ��Ӧ
						if(response.getThrowable()!=null){
							throw response.getThrowable();
						}
						return response.getResult();
					}
				});
		
	}

}
