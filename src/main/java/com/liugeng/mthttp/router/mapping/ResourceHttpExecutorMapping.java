package com.liugeng.mthttp.router.mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.router.executor.DefaultMethodHttpExecutor;
import com.liugeng.mthttp.router.executor.DefaultResourceHttpExecutor;
import com.liugeng.mthttp.router.executor.HttpExecutor;

public class ResourceHttpExecutorMapping implements HttpExecutorMapping {

	private final Map<String, HttpExecutor> urlResourceMapping;

	public ResourceHttpExecutorMapping(String rootPath) {
		urlResourceMapping = new HashMap<>();
		urlResourceMapping.put(genUrlPattern(rootPath, "/**"), new DefaultResourceHttpExecutor());
	}

	private String genUrlPattern(String rootPath, String suffix) {
		if (StringUtils.isBlank(rootPath)) {
			return suffix;
		} else {
			if (StringUtils.endsWith(rootPath, "/")) {
				int last = StringUtils.lastIndexOf(rootPath, "/");
				return StringUtils.substring(rootPath, 0, last) + suffix;
			} else {
				return rootPath + suffix;
			}
		}
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
