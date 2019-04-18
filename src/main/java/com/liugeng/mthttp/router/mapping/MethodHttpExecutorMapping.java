package com.liugeng.mthttp.router.mapping;

import java.util.Map;

import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.router.HttpExecutorMappingInfo;
import com.liugeng.mthttp.router.executor.HttpExecutor;

public class MethodHttpExecutorMapping implements HttpExecutorMapping {

	private final Map<HttpExecutorMappingInfo, HttpExecutor> executorMap;

	public MethodHttpExecutorMapping(Map<HttpExecutorMappingInfo, HttpExecutor> executorMap) {
		this.executorMap = executorMap;
	}

	@Override
	public HttpExecutor getExecutor(HttpRequestEntity requestEntity) {
		HttpExecutorMappingInfo mappingInfo = new HttpExecutorMappingInfo(requestEntity.getPath(), requestEntity.getMethod());
		return executorMap.get(mappingInfo);
	}
}
