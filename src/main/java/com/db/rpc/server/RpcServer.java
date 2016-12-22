package com.db.rpc.server;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.db.rpc.annotation.RpcService;
import com.db.rpc.auth.RpcDecoder;
import com.db.rpc.auth.RpcEncoder;
import com.db.rpc.http.RpcRequest;
import com.db.rpc.http.RpcResponse;
import com.db.rpc.service.registry.ServiceRegistry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class RpcServer implements ApplicationContextAware,InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

	private String serverAddress;
	private ServiceRegistry serviceRegistry;
	private Map<String,Object> handlerMap = new HashMap<String,Object>();//�ӿ��Լ�ʵ�����ϵӳ��

	
	public RpcServer(String serverAddress, ServiceRegistry serviceRegistry) {
		this.serverAddress = serverAddress;
		this.serviceRegistry = serviceRegistry;
	}

	/**
	 * spring��ʼ����ɣ���ȡ�����Ķ���
	 */
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		//��ȡ����RpcServiceע��ʵ����
		Map<String,Object> map = applicationContext.getBeansWithAnnotation(RpcService.class);
		if(map!=null&&!map.isEmpty()){
			for(Object impl:map.values()){
				String interfaceName = impl.getClass().getAnnotation(RpcService.class).cls().getName();
				handlerMap.put(interfaceName, impl);
			}
		}
	}
	
	/**
	 * ����ʼ�������ʱ��ִ�и÷���
	 */
	public void afterPropertiesSet() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try{
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					//����һ������ͻ�����Ϣ�͸�����Ϣ�¼�����(Handler)
					protected void initChannel(SocketChannel channel) throws Exception {
						channel.pipeline()
						.addLast(new RpcDecoder(RpcRequest.class))//��������н���
						.addLast(new RpcEncoder(RpcResponse.class))//����Ӧ���б���
						.addLast(new RpcHandler(handlerMap)); // ���� RPC ����
					}
				})
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
			String[] array = serverAddress.split(":");
			String host = array[0];
			int port = Integer.parseInt(array[1]);
			ChannelFuture future = bootstrap.bind(host, port).sync();
			logger.debug("server started on port {}",port);
			if(serviceRegistry !=null){
				serviceRegistry.registry(serverAddress);//�������ַע�ᵽע������
			}
			future.channel().closeFuture().sync();
		}finally{
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
		
	}


}
