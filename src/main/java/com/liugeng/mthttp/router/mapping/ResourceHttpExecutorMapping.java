package com.liugeng.mthttp.router.mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.router.executor.GeneralResourceHttpExecutor;
import com.liugeng.mthttp.router.executor.HttpExecutor;

public class ResourceHttpExecutorMapping implements HttpExecutorMapping {

	private final Map<String, HttpExecutor> urlResourceMapping;

	public ResourceHttpExecutorMapping(String rootPath) {
		urlResourceMapping = new HashMap<>();
		urlResourceMapping.put("/.*", new GeneralResourceHttpExecutor(rootPath));
	}

	@Override
	public HttpExecutor getExecutor(HttpRequestEntity requestEntity) {
		String path = requestEntity.getPath();
		for (String pattern : urlResourceMapping.keySet()) {
			if (Pattern.matches(pattern, path)) {
				return urlResourceMapping.get(pattern);
			}
		}
		return null;
	}
}
