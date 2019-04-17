package com.liugeng.mthttp.router.mapping;

import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.router.executor.HttpExecutor;

public interface HttpExecutorMapping {

	HttpExecutor getExecutor(HttpRequestEntity requestEntity);
}
