package com.liugeng.mthttp.router.resovler.serialization;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class TextStrategy implements SerializationStrategy {

	@Override
	public ByteBuf serialize(Object response, Charset charset) {
		byte[] bytes = response.toString().getBytes(charset);
		return Unpooled.buffer(bytes.length).writeBytes(bytes);
	}
}
