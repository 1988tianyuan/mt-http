package com.liugeng.mthttp.router.mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.router.executor.GeneralResourceHttpExecutor;
import com.liugeng.mthttp.router.executor.HttpExecutor;
import com.liugeng.mthttp.router.resovler.ResourceResolver;

public class ResourceHttpExecutorMapping implements HttpExecutorMapping {

	private final Map<String, HttpExecutor> urlResourceMapping;

	public ResourceHttpExecutorMapping(String rootPath) {
		urlResourceMapping = new HashMap<>();
		GeneralResourceHttpExecutor resourceExecutor = new GeneralResourceHttpExecutor(rootPath);
		resourceExecutor.addResolver(new ResourceResolver());
		urlResourceMapping.put("/.*", resourceExecutor);
	}

	@Override
	public HttpExecutor getExecutor(HttpRequestEntity requestEntity) {
		HttpMethod method = requestEntity.getMethod();
		if (method.equals(HttpMethod.GET)) {
			String path = requestEntity.getPath();
			for (Map.Entry<String, HttpExecutor> pattern : urlResourceMapping.entrySet()) {
				if (Pattern.matches(pattern.getKey(), path)) {
					return pattern.getValue();
				}
			}
		}
		return null;
	}
}
