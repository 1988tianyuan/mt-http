package com.liugeng.mthttp.server.handler;

import java.util.concurrent.ThreadFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class ClientInitializer extends ChannelInitializer<NioSocketChannel> {

	protected final HttpDispatcherHandler dispatcherHandler;

	public ClientInitializer(HttpDispatcherHandler dispatcherHandler) {
		this.dispatcherHandler = dispatcherHandler;
	}

	@Override
	protected void initChannel(NioSocketChannel nioSocketChannel) {
		ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("dispatcher-thread-%d").build();
		EventLoopGroup eventExecutors = new NioEventLoopGroup(0, threadFactory);
		nioSocketChannel.pipeline()
			.addLast(new HttpServerCodec())
			.addLast(new HttpObjectAggregator(Integer.MAX_VALUE))
			.addLast(new MantianHttpInitHandler())
			.addLast(eventExecutors, dispatcherHandler)
			.addLast(ExceptionCaughtHandler.INSTANCE);
	}


}
