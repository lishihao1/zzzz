package com.db.rpc.client;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.db.rpc.auth.RpcDecoder;
import com.db.rpc.auth.RpcEncoder;
import com.db.rpc.http.RpcRequest;
import com.db.rpc.http.RpcResponse;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RpcClient extends SimpleChannelInboundHandler<RpcResponse>{
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

	private String host;
	private int port;
	private RpcResponse rpcResponse;
	private final Object obj = new Object();
	public RpcClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * 接收返回数据
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
		this.rpcResponse = response;
		synchronized(obj){
			obj.notifyAll();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("client caught exception", cause);
        ctx.close();
	}
	
	public RpcResponse send(RpcRequest request) throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try{
        	Bootstrap bootStrap = new Bootstrap();
        	bootStrap.group(group).channel(NioSocketChannel.class)
        		.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
					protected void initChannel(SocketChannel channel) throws Exception {
                    	channel.pipeline()
                    		.addLast(new RpcEncoder(RpcRequest.class))//请求进行编码（发送请求）
                    		.addLast(new RpcDecoder(RpcResponse.class))//响应进行解码（接收响应）
							.addLast(RpcClient.this);// 使用 RpcClient 发送 RPC 请求
					}
				}).option(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootStrap.connect(host, port).sync();
            future.channel().writeAndFlush(request).sync();
            synchronized(obj){
    			obj.wait();
    		}
            if(rpcResponse!=null){
                future.channel().closeFuture().sync();
            }
            return rpcResponse;
        }finally{
        	group.shutdownGracefully();
        }
	}
	
	


}
