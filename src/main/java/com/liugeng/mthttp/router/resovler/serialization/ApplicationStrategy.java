package com.liugeng.mthttp.router.resovler.serialization;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.lang3.ClassUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ApplicationStrategy implements SerializationStrategy {

	@Override
	public ByteBuf serialize(Object response, Charset charset) throws Exception {
		InputStream inputStream = (InputStream) response;
		byte[] bytes = new byte[inputStream.available()];
		inputStream.read(bytes);
		return Unpooled.buffer(bytes.length).writeBytes(bytes);
	}
}
