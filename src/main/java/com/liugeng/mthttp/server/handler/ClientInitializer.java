package com.liugeng.mthttp.server.handler;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.router.HttpExecutor;
import com.liugeng.mthttp.router.support.DefaultHttpExecutor;
import com.liugeng.mthttp.router.support.ExecutedMethodWrapper;
import com.liugeng.mthttp.router.support.HttpExecutorMappingInfo;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class ClientInitializer extends ChannelInitializer<NioSocketChannel> {

	@Override
	protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
		ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("dispatcher-thread-%d").build();
		EventLoopGroup eventExecutors = new NioEventLoopGroup(0, threadFactory);
		nioSocketChannel.pipeline()
			.addLast(new HttpServerCodec())
			.addLast(new HttpObjectAggregator(Integer.MAX_VALUE))
			.addLast(new MantianHttpInitHandler())
			.addLast(eventExecutors, new HttpDispatcherHandler(genExecutorMap()));
	}

	private Map<HttpExecutorMappingInfo, HttpExecutor> genExecutorMap() throws Exception {
		// todo, default now
		HttpExecutorMappingInfo mappingInfo = new HttpExecutorMappingInfo("/hello", HttpMethod.GET);
		Class<?> clazz = Class.forName("com.liugeng.mthttp.TestController");
		ExecutedMethodWrapper methodWrapper = new ExecutedMethodWrapper();
		methodWrapper.setUserClass(clazz);
		methodWrapper.setUserObject(clazz.newInstance());
		methodWrapper.setUserMethod(clazz.getMethod("hello"));
		methodWrapper.setParameters(methodWrapper.getUserMethod().getParameters());
		return Collections.singletonMap(mappingInfo, new DefaultHttpExecutor(methodWrapper));
	}

}
