package com.liugeng.mthttp.router.mapping;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

public class ResourceExecutorMappingInitializer extends ExecutorMappingInitializer {

	private final String staticPath;

	public ResourceExecutorMappingInitializer(PropertiesConfiguration config) {
		super(config);
		String staticPath = config.getString(STATIC_FILE_PATH, "");
		if(StringUtils.startsWith(staticPath, "/")) {
			staticPath = RegExUtils.replaceFirst(staticPath, "/", "");
		}
		this.staticPath = staticPath;
	}

	@Override
	public HttpExecutorMapping initMapping() {
		return new ResourceHttpExecutorMapping(staticPath);
	}
}
