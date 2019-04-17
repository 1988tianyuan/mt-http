package com.liugeng.mthttp.router.mapping;

import org.apache.commons.configuration2.PropertiesConfiguration;

import com.liugeng.mthttp.config.Configurable;

public abstract class ExecutorMappingInitializer implements Configurable {

	protected PropertiesConfiguration config;

	public ExecutorMappingInitializer(PropertiesConfiguration config) {
		config(config);
	}

	@Override
	public void config(PropertiesConfiguration config) {
		this.config = config;
	}

	public abstract HttpExecutorMapping initMapping();
}
