package com.liugeng.mthttp.server.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerInitializer extends ChannelInitializer<NioServerSocketChannel> {

	@Override
	protected void initChannel(NioServerSocketChannel nioServerSocketChannel) throws Exception {
		log.debug("starting server...");
	}
}
