package com.liugeng.mthttp.server;

import static com.liugeng.mthttp.constant.StringConstants.*;
import static com.liugeng.mthttp.utils.ThrowingConsumerUtil.*;
import static com.liugeng.mthttp.utils.asm.ClassMethodReadingVisitor.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

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

	private volatile boolean started = false;

	private String scanPackage;

	private PropertiesConfiguration config;

	public HttpServer(int port) {
		this.port = port;
	}

	@Override
	public void start(final HttpServerCallback callback) {
		try {
			if (isStarted()) throw new RuntimeException("http server is already started!");
			bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.handler(new ServerInitializer())
				.childHandler(new ClientInitializer(prepareMvcEnv(dispatcherGroup)));
			String host = prepareBingHost();
			bootstrap.bind(host, port)
				.addListener(future -> {
					if(future.isSuccess()){
						log.debug("bind port: {} successfully! server has been started!", port);
						started = true;
						callback.onSuccess();
					}else {
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

	private String prepareBingHost() {
		String configHost;
		try {
			configHost = config.getString(SERVER_BIND_HOST);
			if (StringUtils.isEmpty(configHost)) {
				configHost = InetAddress.getLocalHost().getHostName();
			}
		} catch (UnknownHostException e) {
			configHost = "0.0.0.0";
		}
		config.addProperty(SERVER_BIND_HOST, configHost);
		return configHost;
	}

	private HttpDispatcherHandler prepareMvcEnv(EventLoopGroup eventExecutors) throws Exception {
		try {
			HttpDispatcherHandler handler = new HttpDispatcherHandler(retrievePackages(scanPackage), eventExecutors);
			handler.config(this.config);
			return handler;
		} catch (Exception e) {
			log.error("can't load the package: {}, please provide the correctly package name", scanPackage);
			throw e;
		}
	}

	@Override
	public void stop() {
		bossGroup.shutdownGracefully().syncUninterruptibly();
		workerGroup.shutdownGracefully().syncUninterruptibly();
		dispatcherGroup.shutdownGracefully().syncUninterruptibly();
		bossGroup.terminationFuture().addListener(future -> {
			log.debug("terminate bossGroup successfully.");
			future.sync();
		});
		workerGroup.terminationFuture().addListener(future -> {
			log.debug("terminate workerGroup successfully.");
			future.sync();
		});
		dispatcherGroup.terminationFuture().addListener(future -> {
			log.debug("terminate dispatcher eventLoop group successfully.");
			future.sync();
		});
	}


	private Map<HttpExecutorMappingInfo, HttpExecutor> retrievePackages(String rootPackage) throws Exception {
		PackageResourceLoader loader = new PackageResourceLoader();
		Resource[] resources = loader.getResources(rootPackage);
		Map<HttpExecutorMappingInfo, HttpExecutor> executorMap = Maps.newHashMap();
		for (Resource resource : resources) {
			MetadataReader metadataReader = new SimpleMetadataReader(resource);
			AnnotationMetadata classAnnotationMetadata = metadataReader.getAnnotationMetadata();
			ClassMethodMetadata classMethodMetadata = metadataReader.getClassMethodMetadata();
			ClassMetadata classMetadata = metadataReader.getClassMetadata();
			if (classAnnotationMetadata.hasAnnotation(HttpController.class.getName())) {
				// resolve HttpRouter on class
				Set<String> pathsOnClass = null;
				if (classAnnotationMetadata.hasAnnotation(HttpRouter.class.getName())) {
					pathsOnClass = classAnnotationMetadata.getAnnotationAttributes(HttpRouter.class.getName())
						.getByDefault(ROUTER_PATH, Collections.singleton(""));
				}
				// resolve HttpRouter on method
				resolveMethodRouter(pathsOnClass, classMethodMetadata, executorMap, classMetadata);
			}
		}

		return executorMap;
	}

	private void resolveMethodRouter(Set<String> pathsOnClass, ClassMethodMetadata methodMetadata,
		Map<HttpExecutorMappingInfo, HttpExecutor> executorMap, ClassMetadata classMetadata) {
		methodMetadata.getMethodInfoSet()
			.stream()
			.filter(methodInfo -> methodMetadata.checkMethodAnnotationAbsent(methodInfo, HttpRouter.class.getName()))
			.forEach(
				throwingConsumerWrapper(
					methodInfo -> buildMappingInfo(methodInfo, executorMap, pathsOnClass, methodMetadata, classMetadata)
				)
			);
	}

	private void buildMappingInfo(MethodInfo methodInfo, Map<HttpExecutorMappingInfo, HttpExecutor> executorMap,
		Set<String> pathsOnClass, ClassMethodMetadata methodMetadata, ClassMetadata classMetadata) throws Exception {
		AnnotationAttributes methodAnnoAttr = methodMetadata.getMethodAnnotationAttr(methodInfo, HttpRouter.class.getName());
		Set<String> pathsOnMethod = methodAnnoAttr.getByDefault(ROUTER_PATH, Collections.singleton("/"));
		HttpMethod httpMethod = HttpMethod.valueOf((String)methodAnnoAttr.getByDefault(ROUTER_METHOD,  Collections.singleton("GET")).toArray()[0]);
		ExecutedMethodWrapper methodWrapper = genMethodWrapper(classMetadata.getClassName(), methodInfo);
		HttpExecutor httpExecutor = new DefaultHttpExecutor(methodWrapper);
		httpExecutor.config(config);
		for (String path : pathsOnMethod) {
			pathsOnClass.forEach(pathOnClass -> {
				HttpExecutorMappingInfo mappingInfo = new HttpExecutorMappingInfo(pathOnClass + path, httpMethod);
				log.info("resolved mappings: {}", mappingInfo);
				executorMap.put(mappingInfo, httpExecutor);
			});
		}
	}

	private ExecutedMethodWrapper genMethodWrapper(String clazzName, MethodInfo methodInfo) throws Exception {
		String[] types = methodInfo.getArgTypes();
		Class<?>[] paramArgClazzs = new Class[0];
		// resolve method parameters type
		if (types != null && types.length > 0) {
			paramArgClazzs = new Class[types.length];
			for (int i = 0; i < types.length; i++) {
				Class<?> paramArgClazz = ClassUtils.parseType(types[i]);
				paramArgClazzs[i] = paramArgClazz;
			}
		}
		// resolve class type
		Class<?> clazz = ClassUtils.parseType(clazzName);
		ExecutedMethodWrapper methodWrapper = new ExecutedMethodWrapper();
		methodWrapper.setUserClass(clazz);
		methodWrapper.setUserObject(clazz.newInstance());
		methodWrapper.setUserMethod(clazz.getMethod(methodInfo.getMethodName(), paramArgClazzs));
		methodWrapper.setParameters(methodWrapper.getUserMethod().getParameters());
		return methodWrapper;
	}

	@Override
	public void config(PropertiesConfiguration config) {
		bossGroup = new NioEventLoopGroup(config.getInt(SERVER_EVENTLOOP_BOSS_THREAD, 0));
		workerGroup = new NioEventLoopGroup(config.getInt(SERVER_EVENTLOOP_WORKER_THREAD, 0));
		bootstrap = new ServerBootstrap();
		scanPackage = config.getString(HTTP_EXECUTOR_SCAN_PACKAGE);

		ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("dispatcher-thread-%d").build();
		dispatcherGroup = new NioEventLoopGroup(config.getInt(DISPATCHER_EVENTLOOP_THREAD, 0), threadFactory);

		this.config = config;
	}

	public boolean isStarted() {
		return started;
	}
}
