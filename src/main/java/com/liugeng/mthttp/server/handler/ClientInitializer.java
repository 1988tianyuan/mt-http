package com.liugeng.mthttp.server.handler;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.router.HttpExecutor;
import com.liugeng.mthttp.router.annotation.HttpController;
import com.liugeng.mthttp.router.annotation.HttpRouter;
import com.liugeng.mthttp.router.support.DefaultHttpExecutor;
import com.liugeng.mthttp.router.support.ExecutedMethodWrapper;
import com.liugeng.mthttp.router.support.HttpExecutorMappingInfo;
import com.liugeng.mthttp.utils.MetadataReader;
import com.liugeng.mthttp.utils.SimpleMetadataReader;
import com.liugeng.mthttp.utils.asm.AnnotationAttributes;
import com.liugeng.mthttp.utils.asm.AnnotationMetadata;
import com.liugeng.mthttp.utils.io.PackageResourceLoader;
import com.liugeng.mthttp.utils.io.Resource;
import io.netty.channel.ChannelHandler;
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
