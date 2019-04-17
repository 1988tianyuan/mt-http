package com.liugeng.mthttp.router.mapping;

import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.router.executor.HttpExecutor;

public class ResourceHttpExecutorMapping implements HttpExecutorMapping {

	@Override
	public HttpExecutor getExecutor(HttpRequestEntity requestEntity) {
		return null;
	}
}
