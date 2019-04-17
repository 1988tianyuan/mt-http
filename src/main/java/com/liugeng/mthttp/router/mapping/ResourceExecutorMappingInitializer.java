package com.liugeng.mthttp.router.mapping;

import org.apache.commons.configuration2.PropertiesConfiguration;

public class ResourceExecutorMappingInitializer extends ExecutorMappingInitializer {

	private String staticPath;

	public ResourceExecutorMappingInitializer(PropertiesConfiguration config) {
		super(config);
		this.staticPath = config.getString(STATIC_FILE_PATH, "/");
	}

	@Override
	public HttpExecutorMapping initMapping() {
		return null;
	}
}
