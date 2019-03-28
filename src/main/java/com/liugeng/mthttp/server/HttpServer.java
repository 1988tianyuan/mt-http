package com.liugeng.mthttp.server;

import static com.liugeng.mthttp.constant.StringConstants.*;
import static com.liugeng.mthttp.utils.ThrowingConsumerUtil.*;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.router.HttpExecutor;
import com.liugeng.mthttp.router.annotation.HttpController;
import com.liugeng.mthttp.router.annotation.HttpRouter;
import com.liugeng.mthttp.router.support.DefaultHttpExecutor;
import com.liugeng.mthttp.router.support.ExecutedMethodWrapper;
import com.liugeng.mthttp.router.support.HttpExecutorMappingInfo;
import com.liugeng.mthttp.server.handler.ClientInitializer;
import com.liugeng.mthttp.server.handler.HttpDispatcherHandler;
import com.liugeng.mthttp.server.handler.ServerInitializer;
import com.liugeng.mthttp.utils.MetadataReader;
import com.liugeng.mthttp.utils.SimpleMetadataReader;
import com.liugeng.mthttp.utils.asm.AnnotationAttributes;
import com.liugeng.mthttp.utils.asm.AnnotationMetadata;
import com.liugeng.mthttp.utils.asm.ClassMetadata;
import com.liugeng.mthttp.utils.asm.ClassMethodMetadata;
import com.liugeng.mthttp.utils.asm.ClassMethodReadingVisitor;
import com.liugeng.mthttp.utils.io.PackageResourceLoader;
import com.liugeng.mthttp.utils.io.Resource;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

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
						callback.onSuccess();
					}else {
						callback.onError();
					}
				});
		} catch (Exception e) {
			log.error("error during init HttpServer, stop all the process", e);
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
				// resolve HttpRouter on this class
				Set<String> pathsOnClass = null;
				if (classAnnotationMetadata.hasAnnotation(HttpRouter.class.getName())) {
					pathsOnClass = resolveClassRouter(metadataReader, executorMap);
				}
				// resolve HttpRouter on this class
				resolveMethodRouter(pathsOnClass, classMethodMetadata, executorMap, classMetadata);
			}
		}
		return executorMap;
	}

	private Set<String> resolveClassRouter(MetadataReader metadataReader, Map<HttpExecutorMappingInfo, HttpExecutor> executorMap) throws
		Exception {
		AnnotationMetadata camData = metadataReader.getAnnotationMetadata();
		AnnotationAttributes attributes = camData.getAnnotationAttributes(HttpRouter.class.getName());
		return attributes.get(ROUTER_PATH);
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

	private void buildMappingInfo(ClassMethodReadingVisitor.MethodInfo methodInfo, Map<HttpExecutorMappingInfo, HttpExecutor> executorMap,
		Set<String> pathsOnClass, ClassMethodMetadata methodMetadata, ClassMetadata classMetadata) throws Exception {
		AnnotationAttributes methodAnnoAttr = methodMetadata.getMethodAnnotationAttr(methodInfo, HttpRouter.class.getName());
		Set<String> pathsOnMethod = methodAnnoAttr.get(ROUTER_PATH);
		HttpMethod httpMethod = HttpMethod.valueOf((String)methodAnnoAttr.get(ROUTER_METHOD).toArray()[0]);
		ExecutedMethodWrapper methodWrapper = genMethodWrapper(classMetadata.getClassName(), methodInfo);
		HttpExecutor httpExecutor = new DefaultHttpExecutor(methodWrapper);
		for (String path : pathsOnMethod) {
			pathsOnClass.forEach(pathOnClass -> {
				HttpExecutorMappingInfo mappingInfo = new HttpExecutorMappingInfo(pathOnClass + path, httpMethod);
				executorMap.put(mappingInfo, httpExecutor);
			});
		}
	}

	// todo, refactor with both reflection and asm, now it's just reflection
	private ExecutedMethodWrapper genMethodWrapper(String clazzName, ClassMethodReadingVisitor.MethodInfo methodInfo) throws Exception {
		Type[] types = methodInfo.getArgTypes();
		Class<?>[] paramArgClazzs = new Class[0];
		if (types != null && types.length > 0) {
			paramArgClazzs = new Class[types.length];
			for (int i = 0; i < types.length; i++) {
				Class<?> paramArgClazz = Class.forName(types[i].getClassName());
				paramArgClazzs[i] = paramArgClazz;
			}
		}
		Class<?> clazz = Class.forName(clazzName);
		ExecutedMethodWrapper methodWrapper = new ExecutedMethodWrapper();
		methodWrapper.setUserClass(clazz);
		methodWrapper.setUserObject(clazz.newInstance());
		methodWrapper.setUserMethod(clazz.getMethod(methodInfo.getMethodName(), paramArgClazzs));
		methodWrapper.setParameters(methodWrapper.getUserMethod().getParameters());
		return methodWrapper;
	}
}
