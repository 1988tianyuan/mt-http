package com.liugeng.mthttp.router.resovler.serialization;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;

public class StaticeFileStrategy implements SerializationStrategy {

	@Override
	public ByteBuf serialize(Object response, Charset charset) {
		return null;
	}
}
