package com.db.rpc.auth;

import java.util.List;

import com.db.rpc.util.SerializationUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * rpc �������
 * @author dengbo
 */
public class RpcDecoder extends ByteToMessageDecoder{
	private Class<?> genericClass;
	
	public RpcDecoder(Class<?> genericClass) {
		this.genericClass = genericClass;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if(in.readableBytes()<4){
			return;
		}
		in.markReaderIndex();
		int dataLength = in.readInt();
		if(dataLength<0){
            ctx.close();
		}
		if(in.readableBytes()<dataLength){
			in.resetReaderIndex();
			return;
		}
		byte[] data = new byte[dataLength];
		in.readBytes(data);
		Object obj = SerializationUtil.deserialize(data, genericClass);
		out.add(obj);
	}

}
