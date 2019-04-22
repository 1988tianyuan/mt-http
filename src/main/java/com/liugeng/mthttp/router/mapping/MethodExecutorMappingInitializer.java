package com.liugeng.mthttp.router.mapping;

import static com.liugeng.mthttp.constant.StringConstants.*;
import static com.liugeng.mthttp.utils.ThrowingConsumerUtil.*;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.router.ExecutedMethodWrapper;
import com.liugeng.mthttp.router.HttpExecutorMappingInfo;
import com.liugeng.mthttp.router.annotation.HttpController;
import com.liugeng.mthttp.router.annotation.HttpRouter;
import com.liugeng.mthttp.router.executor.DefaultMethodHttpExecutor;
import com.liugeng.mthttp.router.executor.HttpExecutor;
import com.liugeng.mthttp.router.resovler.MethodResponseBodyResolver;
import com.liugeng.mthttp.utils.ClassUtils;
import com.liugeng.mthttp.utils.MetadataReader;
import com.liugeng.mthttp.utils.SimpleMetadataReader;
import com.liugeng.mthttp.utils.asm.AnnotationAttributes;
import com.liugeng.mthttp.utils.asm.AnnotationMetadata;
import com.liugeng.mthttp.utils.asm.ClassMetadata;
import com.liugeng.mthttp.utils.asm.ClassMethodMetadata;
import com.liugeng.mthttp.utils.asm.ClassMethodReadingVisitor;
import com.liugeng.mthttp.utils.io.PackageResourceLoader;
import com.liugeng.mthttp.utils.io.Resource;

public class MethodExecutorMappingInitializer extends ExecutorMappingInitializer {

	private static final Logger log = LoggerFactory.getLogger(MethodExecutorMappingInitializer.class);

	private final String rootPackage;

	public MethodExecutorMappingInitializer(PropertiesConfiguration config) {
		super(config);
		this.rootPackage = config.getString(HTTP_EXECUTOR_SCAN_PACKAGE, "");
	}

	@Override
	public HttpExecutorMapping initMapping() throws Exception {
		try {
			Map<HttpExecutorMappingInfo, HttpExecutor> executorMap = retrievePackages(rootPackage);
			return new MethodHttpExecutorMapping(executorMap);
		} catch (Exception e) {
			log.error("can't load the package: {}, please provide the correct package name", rootPackage);
			throw e;
		}
	}

	private Map<HttpExecutorMappingInfo, HttpExecutor> retrievePackages(String rootPackage) throws Exception {
		PackageResourceLoader loader = new PackageResourceLoader();
		Resource[] resources = loader.getResources(rootPackage);
		Map<HttpExecutorMappingInfo, HttpExecutor> executorMap = Maps.newHashMap();
		for (Resource resource : resources) {
			if (isInnerClass(resource)) {
				continue;
			}
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

	private boolean isInnerClass(Resource resource) {
		String path = resource.getDescription();
		return Pattern.matches(".*\\$.*\\.class", path);
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
		Set<String> pathsOnMethod = methodAnnoAttr.getByDefault(ROUTER_PATH, Collections.singleton("/"));
		HttpMethod httpMethod = HttpMethod.valueOf((String)methodAnnoAttr.getByDefault(ROUTER_METHOD,  Collections.singleton("GET")).toArray()[0]);
		ExecutedMethodWrapper methodWrapper = genMethodWrapper(classMetadata.getClassName(), methodInfo);
		HttpExecutor httpExecutor = new DefaultMethodHttpExecutor(methodWrapper, new MethodResponseBodyResolver());
		httpExecutor.config(config);
		for (String path : pathsOnMethod) {
			pathsOnClass.forEach(pathOnClass -> {
				HttpExecutorMappingInfo mappingInfo = new HttpExecutorMappingInfo(pathOnClass + path, httpMethod);
				log.info("resolved mappings: {}", mappingInfo);
				executorMap.put(mappingInfo, httpExecutor);
			});
		}
	}

	private ExecutedMethodWrapper genMethodWrapper(String clazzName, ClassMethodReadingVisitor.MethodInfo methodInfo) throws Exception {
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
