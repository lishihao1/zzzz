package com.db.rpc.auth;

import com.db.rpc.util.SerializationUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
/**
 * rpc œÏ”¶±‡¬Î
 * @author dengbo
 */
public class RpcEncoder extends MessageToByteEncoder{
	private Class<?> genericClass;
	
	public RpcEncoder(Class<?> genericClass) {
		this.genericClass = genericClass;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
		if(genericClass.isInstance(in)){
			byte[] data =  SerializationUtil.serialize(in);
			out.writeInt(data.length);
			out.writeBytes(data);
		}
		
	}

}
