package com.liugeng.mthttp.server;

import static com.liugeng.mthttp.constant.StringConstants.*;
import static com.liugeng.mthttp.utils.ThrowingConsumerUtil.*;
import static com.liugeng.mthttp.utils.asm.ClassMethodReadingVisitor.*;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

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

public class HttpServer implements Server {

	private static final Logger log = LoggerFactory.getLogger(HttpServer.class);

	private final NioEventLoopGroup workerGroup;

	private final NioEventLoopGroup bossGroup;

	private final ServerBootstrap bootstrap;

	private final int port;

	public HttpServer(int port) {
		this(port, 0, 0);
	}

	public HttpServer(int port, int bossThread, int workThread) {
		bossGroup = new NioEventLoopGroup(bossThread);
		workerGroup = new NioEventLoopGroup(workThread);
		bootstrap = new ServerBootstrap();
		this.port = port;
	}

	@Override
	public void start(final HttpServerCallback callback) {
		try {
			bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.handler(new ServerInitializer())
				.childHandler(new ClientInitializer(prepareMvcEnv()));
			bootstrap.bind(port)
				.addListener(future -> {
					if(future.isSuccess()){
						log.debug("bind port: {} successfully! server has been started!", port);
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

	private HttpDispatcherHandler prepareMvcEnv() throws Exception {
		return new HttpDispatcherHandler(retrievePackages("com.liugeng.mthttp.test"));
	}

	@Override
	public void stop() {
		bossGroup.shutdownGracefully().syncUninterruptibly();
		workerGroup.shutdownGracefully().syncUninterruptibly();
		bossGroup.terminationFuture().addListener(future -> {
			log.debug("terminate bossGroup successfully.");
			future.sync();
		});
		workerGroup.terminationFuture().addListener(future -> {
			log.debug("terminate workerGroup successfully.");
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
					pathsOnClass = classAnnotationMetadata.getAnnotationAttributes(HttpRouter.class.getName()).get(ROUTER_PATH);
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
		Set<String> pathsOnMethod = methodAnnoAttr.get(ROUTER_PATH);
		HttpMethod httpMethod = HttpMethod.valueOf((String)methodAnnoAttr.get(ROUTER_METHOD).toArray()[0]);
		ExecutedMethodWrapper methodWrapper = genMethodWrapper(classMetadata.getClassName(), methodInfo);
		HttpExecutor httpExecutor = new DefaultHttpExecutor(methodWrapper);
		pathsOnClass = pathsOnClass == null ? Collections.singleton("") : pathsOnClass;
		for (String path : pathsOnMethod) {
			pathsOnClass.forEach(pathOnClass -> {
				HttpExecutorMappingInfo mappingInfo = new HttpExecutorMappingInfo(pathOnClass + path, httpMethod);
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


}
