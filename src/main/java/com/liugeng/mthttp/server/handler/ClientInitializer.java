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
			.addLast(eventExecutors, new HttpDispatcherHandler(retrievePackages("com.liugeng.mthttp.test")));
	}


	private Map<HttpExecutorMappingInfo, HttpExecutor> retrievePackages(String rootPackage) throws Exception {
		PackageResourceLoader loader = new PackageResourceLoader();
		Resource[] resources = loader.getResources(rootPackage);
		Map<HttpExecutorMappingInfo, HttpExecutor> executorMap = Maps.newHashMap();
		for (Resource resource : resources) {
			MetadataReader metadataReader = new SimpleMetadataReader(resource);
			AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
			if (annotationMetadata.hasAnnotation(HttpController.class.getName())) {
				if (annotationMetadata.hasAnnotation(HttpRouter.class.getName())) {
					AnnotationAttributes attributes = annotationMetadata.getAnnotationAttributes(HttpRouter.class.getName());
					String[] paths = attributes.get("path").toArray(new String[0]);
					HttpMethod method = HttpMethod.valueOf((String)attributes.get("method").toArray()[0]);
					ExecutedMethodWrapper methodWrapper = genMethodWrapper(metadataReader.getClassMetadata().getClassName());
					HttpExecutor httpExecutor = new DefaultHttpExecutor(methodWrapper);
					for (String path : paths) {
						HttpExecutorMappingInfo mappingInfo = new HttpExecutorMappingInfo(path, method);
						executorMap.put(mappingInfo, httpExecutor);
					}
				}
			}
		}
		return executorMap;
	}

	private ExecutedMethodWrapper genMethodWrapper(String clazzName) throws Exception {
		Class<?> clazz = Class.forName(clazzName);
		ExecutedMethodWrapper methodWrapper = new ExecutedMethodWrapper();
		methodWrapper.setUserClass(clazz);
		methodWrapper.setUserObject(clazz.newInstance());
		methodWrapper.setUserMethod(clazz.getMethod("hello"));
		methodWrapper.setParameters(methodWrapper.getUserMethod().getParameters());
		return methodWrapper;
	}
}
