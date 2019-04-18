package com.liugeng.mthttp.server;

import static com.liugeng.mthttp.constant.StringConstants.*;
import static com.liugeng.mthttp.utils.ThrowingConsumerUtil.*;
import static com.liugeng.mthttp.utils.asm.ClassMethodReadingVisitor.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.liugeng.mthttp.config.Configurable;
import io.netty.channel.EventLoopGroup;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.router.executor.HttpExecutor;
import com.liugeng.mthttp.router.annotation.HttpController;
import com.liugeng.mthttp.router.annotation.HttpRouter;
import com.liugeng.mthttp.router.executor.DefaultHttpExecutor;
import com.liugeng.mthttp.router.ExecutedMethodWrapper;
import com.liugeng.mthttp.router.HttpExecutorMappingInfo;
import com.liugeng.mthttp.server.handler.ClientInitializer;
import com.liugeng.mthttp.server.handler.HttpDispatcherHandler;
import com.liugeng.mthttp.server.handler.ServerInitializer;
import com.liugeng.mthttp.utils.ClassUtils;
import com.liugeng.mthttp.utils.MetadataReader;
import com.liugeng.mthttp.utils.SimpleMetadataReader;
import com.liugeng.mthttp.utils.asm.AnnotationAttributes;
import com.liugeng.mthttp.utils.asm.AnnotationMetadata;
import com.liugeng.mthttp.utils.asm.ClassMetadata;
import com.liugeng.mthttp.utils.asm.ClassMethodMetadata;
import com.liugeng.mthttp.utils.io.PackageResourceLoader;
import com.liugeng.mthttp.utils.io.Resource;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HttpServer implements Server, Configurable {

	private static final Logger log = LoggerFactory.getLogger(HttpServer.class);

	private NioEventLoopGroup workerGroup;

	private NioEventLoopGroup bossGroup;

	private NioEventLoopGroup dispatcherGroup;

	private ServerBootstrap bootstrap;

	private final int port;

	private AtomicBoolean started = new AtomicBoolean(false);

	private String scanPackage;

	private PropertiesConfiguration config;

	public HttpServer(int port) {
		this.port = port;
	}

	@Override
	public void start(final HttpServerCallback callback) {
		try {
			if (isStarted()) return;
			bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.handler(new ServerInitializer())
				.childHandler(new ClientInitializer(prepareMvcEnv(dispatcherGroup)));
			String host = config.getString(SERVER_BIND_HOST, "0.0.0.0");
			bootstrap.bind(host, port)
				.addListener(future -> {
					if(future.isSuccess()){
						log.debug("bind port: {} successfully! server has been started!", port);
						started.set(true);
						callback.onSuccess();
					} else {
						callback.onError();
					}
				});
		} catch (Exception e) {
			log.error("error during init HttpServer, stop all the process.", e);
			callback.onError();
		} finally {
			final HttpServer server = this;
			Runtime.getRuntime().addShutdownHook(new Thread("httpServer-shutdown-hook") {
				@Override
				public void run() {
					server.stop();
				}
			});
		}
	}

	private HttpDispatcherHandler prepareMvcEnv(EventLoopGroup eventExecutors) throws Exception {
		HttpDispatcherHandler handler = new HttpDispatcherHandler(eventExecutors);
		handler.config(this.config);
		return handler;
	}

	@Override
	public void stop() {
		bossGroup.shutdownGracefully().syncUninterruptibly();
		workerGroup.shutdownGracefully().syncUninterruptibly();
		dispatcherGroup.shutdownGracefully().syncUninterruptibly();
		bossGroup.terminationFuture().addListener(future -> {
			future.sync();
			log.debug("terminate bossGroup successfully.");
		});
		workerGroup.terminationFuture().addListener(future -> {
			future.sync();
			log.debug("terminate workerGroup successfully.");
		});
		dispatcherGroup.terminationFuture().addListener(future -> {
			future.sync();
			log.debug("terminate dispatcher eventLoop group successfully.");
		});
	}

	@Override
	public void config(PropertiesConfiguration config) {
		bossGroup = new NioEventLoopGroup(config.getInt(SERVER_EVENTLOOP_BOSS_THREAD, 0));
		workerGroup = new NioEventLoopGroup(config.getInt(SERVER_EVENTLOOP_WORKER_THREAD, 0));
		bootstrap = new ServerBootstrap();

		ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("dispatcher-thread-%d").build();
		dispatcherGroup = new NioEventLoopGroup(config.getInt(DISPATCHER_EVENTLOOP_THREAD, 0), threadFactory);

		this.config = config;
	}

	public boolean isStarted() {
		return started.get();
	}
}
