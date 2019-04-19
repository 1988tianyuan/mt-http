package com.liugeng.mthttp.router.resovler.serialization;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;

public interface SerializationStrategy {

	ByteBuf serialize(Object response, Charset charset);

}
